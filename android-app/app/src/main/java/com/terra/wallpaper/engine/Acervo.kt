package com.terra.wallpaper.engine

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import com.terra.wallpaper.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

/**
 * ACERVO — a arte das cenas mora FORA do app e é baixada sob demanda.
 *
 * O app inclui cinco cenas e miniaturas. As demais artes sao entregues sob
 * demanda; o tamanho depende de cada pacote. O indice embarcado fixa os hashes.
 *
 * O que o FRONT precisa saber: só os métodos públicos daqui. A Loja não fala de
 * HTTP, arquivo nem cache — pergunta [temArte], pede [baixarArte] e mostra
 * [thumb]/[preview], como já faz com `Catalogo`/`Plano`.
 *
 * Regras que este objeto garante:
 *  - **Compra é a fonte da verdade, não o arquivo.** Baixar não destrava nada:
 *    quem libera é `Catalogo`/`Plano`, e o chamador confere ANTES.
 *  - **Offline não quebra.** O que já está no disco funciona sem rede; o
 *    manifesto fica em cache; falta de rede vira [Progresso.Erro], não crash.
 *  - **Nada pela metade.** O ZIP é verificado (sha256) e extraído numa pasta
 *    temporária que só então vira a definitiva.
 */
object Acervo {

    // ── modelo do manifest.json (gerado por `tools/pacote_cenas.py`) ─────────
    data class ArquivoRemoto(val arquivo: String = "", val bytes: Long = 0,
                             val sha256: String = "")

    data class ArtePack(val pack: ArquivoRemoto = ArquivoRemoto(),
                        val thumb: ArquivoRemoto = ArquivoRemoto(),
                        val preview: ArquivoRemoto = ArquivoRemoto())

    data class CenaPack(val cena: ArquivoRemoto = ArquivoRemoto(),
                        val artes: Map<String, ArtePack> = emptyMap())

    data class Manifesto(val versao: Int = 0, val formato: String = "webp",
                         val cenas: Map<String, CenaPack> = emptyMap()) {

        fun arte(cena: String, arte: String): ArtePack? = cenas[cena]?.artes?.get(arte)

        /** Quanto pesa baixar esta arte agora (arte + o pack comum da cena). */
        fun bytesDe(cena: String, arte: String, jaTemCena: Boolean): Long {
            val c = cenas[cena] ?: return 0
            return (c.artes[arte]?.pack?.bytes ?: 0) + (if (jaTemCena) 0 else c.cena.bytes)
        }

        companion object {
            val VAZIO = Manifesto()

            /** Texto → manifesto. JSON quebrado devolve [VAZIO] (nunca lança):
             *  índice corrompido no cache não pode derrubar a Loja. */
            fun parse(json: String): Manifesto = try {
                Gson().fromJson(json, Manifesto::class.java) ?: VAZIO
            } catch (e: JsonSyntaxException) {
                VAZIO
            }
        }
    }

    /** Estado de um download, emitido por [baixarArte]. */
    sealed class Progresso {
        /** 0f..1f — dá pra ligar direto numa barra de progresso. */
        data class Baixando(val fracao: Float) : Progresso()
        object Ok : Progresso()
        data class Erro(val motivo: String) : Progresso()
    }

    // ── configuração ────────────────────────────────────────────────────────
    /** URL base do bucket (R2). Vazia = ainda não configurada: aí o app só usa
     *  o que veio embutido (o wallpaper grátis) e a Loja fica sem catálogo. */
    val BASE_PADRAO: String get() = BuildConfig.ACERVO_URL

    private const val PREFS = "atmosfera_acervo"
    private const val KEY_BASE = "base_url"
    private const val TTL_MANIFESTO_MS = 6 * 60 * 60 * 1000L   // 6 h

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followSslRedirects(false)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun base(c: Context): String {
        val raw = if (BuildConfig.DEBUG) c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_BASE, BASE_PADRAO).orEmpty() else BASE_PADRAO
        if (raw.isBlank()) return ""
        val uri = runCatching { java.net.URI(raw) }.getOrNull() ?: return ""
        val local = BuildConfig.DEBUG && uri.scheme == "http" && uri.host in setOf("10.0.2.2", "127.0.0.1", "localhost")
        if ((!local && uri.scheme != "https") || uri.host.isNullOrBlank() ||
            uri.userInfo != null || uri.query != null || uri.fragment != null || uri.path.contains("..")) return ""
        return raw.trimEnd('/') + "/"
    }

    private var indice: Manifesto? = null
    @Synchronized private fun indice(c: Context): Manifesto {
        indice?.let { return it }
        return runCatching { c.assets.open("atmosfera/catalogo/manifest.json")
            .bufferedReader().use { Manifesto.parse(it.readText()) } }.getOrDefault(Manifesto.VAZIO)
            .also { indice = it }
    }
    private fun vigente(pasta: File, info: ArquivoRemoto): Boolean =
        info.sha256.length == 64 && runCatching { File(pasta, ".sha256").readText() == info.sha256 }.getOrDefault(false)

    /** Troca o servidor. Existe pra teste local (`http://10.0.2.2:8123/dist/`)
     *  sem precisar rebuildar o app. */
    fun definirBase(c: Context, url: String) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_BASE, url).apply()
    }

    // ── onde as coisas ficam no aparelho ─────────────────────────────────────
    fun raiz(c: Context): File = File(c.filesDir, "acervo")

    /** Pasta com fundo/frente/luzes_off de uma ARTE. */
    fun pastaArte(raiz: File, cena: String, arte: String): File {
        require(cena.matches(Regex("[a-z0-9_]+")) && arte.matches(Regex("[a-z0-9_]+")))
        return File(raiz, "pack/$cena/$arte")
    }

    /** Pasta com o que é comum à cena (zonas, profundidade). */
    fun pastaCena(raiz: File, cena: String) = pastaArte(raiz, cena, "_cena")

    /** Já dá pra rodar esta arte sem rede? */
    fun temArte(c: Context, cena: String, arte: String): Boolean {
        val r = raiz(c)
        fun tem(pasta: File, nome: String) = listOf("png", "webp").any { File(pasta, "$nome.$it").isFile }
        val pack = indice(c).cenas[cena] ?: return false
        val art = pack.artes[arte] ?: return false
        if (!vigente(pastaArte(r, cena, arte), art.pack) || !vigente(pastaCena(r, cena), pack.cena)) return false
        return tem(pastaArte(r, cena, arte), "fundo") && tem(pastaArte(r, cena, arte), "frente") &&
            (tem(pastaArte(r, cena, arte), "zonas") || tem(pastaCena(r, cena), "zonas"))
    }

    fun bytesParaBaixar(c: Context, cena: String, arte: String): Long {
        val m = indice(c)
        val comum = m.cenas[cena]?.cena ?: return 0
        return m.bytesDe(cena, arte, vigente(pastaCena(raiz(c), cena), comum))
    }

    // ── manifesto ────────────────────────────────────────────────────────────
    /**
     * Índice remoto. Usa o cache local quando ele é recente (ou quando não há
     * rede) — é isso que faz a Loja abrir offline mostrando o que já viu.
     * Sem servidor configurado e sem cache, devolve [Manifesto.VAZIO].
     */
    suspend fun manifesto(c: Context, forcar: Boolean = false): Manifesto =
        withContext(Dispatchers.IO) {
            // O indice que acompanha o app fixa os hashes independentemente do bucket.
            val embarcado = runCatching {
                c.assets.open("atmosfera/catalogo/manifest.json").bufferedReader().use { Manifesto.parse(it.readText()) }
            }.getOrNull()
            if (embarcado != null && embarcado.cenas.isNotEmpty()) return@withContext embarcado
            if (!BuildConfig.DEBUG) return@withContext Manifesto.VAZIO
            val cache = File(raiz(c), "manifest.json")
            val fresco = cache.exists() &&
                System.currentTimeMillis() - cache.lastModified() < TTL_MANIFESTO_MS
            if (!forcar && fresco) return@withContext Manifesto.parse(cache.readText())
            val url = base(c)
            if (url.isEmpty()) {
                return@withContext if (cache.exists()) Manifesto.parse(cache.readText())
                else Manifesto.VAZIO
            }
            try {
                val txt = baixarTexto(url + "manifest.json")
                cache.parentFile?.mkdirs()
                cache.writeText(txt)
                Manifesto.parse(txt)
            } catch (e: Exception) {
                if (cache.exists()) Manifesto.parse(cache.readText()) else Manifesto.VAZIO
            }
        }

    // ── download da arte ─────────────────────────────────────────────────────
    /**
     * Baixa a arte (e o pack comum da cena, se ainda não estiver aqui).
     * Idempotente: arte já baixada emite [Progresso.Ok] na hora. Cancelar a
     * coroutine cancela o download — o que estava pela metade fica no `.tmp`
     * e é descartado na próxima tentativa.
     *
     * NÃO confere compra: quem chama valida em `Catalogo`/`Plano` antes.
     */
    fun baixarArte(c: Context, cena: String, arte: String): Flow<Progresso> = channelFlow {
        if (temArte(c, cena, arte)) { send(Progresso.Ok); return@channelFlow }
        val url = base(c)
        if (url.isEmpty()) { send(Progresso.Erro("servidor não configurado")); return@channelFlow }

        val m = manifesto(c)
        val a = m.arte(cena, arte)
        if (a == null) { send(Progresso.Erro("arte fora do catálogo")); return@channelFlow }

        val r = raiz(c)
        val faltaCena = !vigente(pastaCena(r, cena), m.cenas[cena]!!.cena)
        val total = m.bytesDe(cena, arte, !faltaCena).coerceAtLeast(1)
        var base = 0L
        send(Progresso.Baixando(0f))
        // `trySend` (não `send`) dentro do laço de leitura: o download não pode
        // travar esperando a UI consumir cada bloco — progresso perdido é só um
        // quadro de barra a menos.
        val andamento = { lidos: Long ->
            trySend(Progresso.Baixando(((base + lidos).toFloat() / total).coerceIn(0f, 1f)))
            Unit
        }
        try {
            if (faltaCena) {
                val z = m.cenas[cena]!!.cena
                validarArquivo(z)
                baixarZip(url + z.arquivo, z.sha256, pastaCena(r, cena), andamento)
                base = z.bytes
            }
            validarArquivo(a.pack)
            baixarZip(url + a.pack.arquivo, a.pack.sha256, pastaArte(r, cena, arte), andamento)
            if (!temArte(c, cena, arte)) throw IllegalStateException("Pacote incompleto; arte não aplicada.")
            send(Progresso.Baixando(1f))
            send(Progresso.Ok)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            send(Progresso.Erro(e.message ?: "falhou o download"))
        }
    }.flowOn(Dispatchers.IO)

    /** Miniatura da Loja (240×320). Baixa se faltar; null = sem rede e sem cache. */
    suspend fun thumb(c: Context, cena: String, arte: String): File? =
        imagem(c, cena, arte, "thumb")

    /** Preview da tela do cenário (543×724) — o "meio-termo" da pré-venda. */
    suspend fun preview(c: Context, cena: String, arte: String): File? =
        imagem(c, cena, arte, "preview")

    private suspend fun imagem(c: Context, cena: String, arte: String, tipo: String): File? =
        withContext(Dispatchers.IO) {
            val destino = File(raiz(c), "$tipo/${cena}__$arte.webp")
            if (destino.exists()) return@withContext destino
            val url = base(c)
            if (url.isEmpty()) return@withContext null
            val info = manifesto(c).arte(cena, arte)
                ?.let { if (tipo == "thumb") it.thumb else it.preview } ?: return@withContext null
            try {
                val dados = baixarBytes(url + info.arquivo)
                if (info.sha256.isNotEmpty() && !confere(dados, info.sha256)) return@withContext null
                destino.parentFile?.mkdirs()
                val tmp = File(destino.path + ".tmp")
                tmp.writeBytes(dados)
                if (!tmp.renameTo(destino)) { tmp.delete(); return@withContext null }
                destino
            } catch (e: Exception) {
                null
            }
        }

    // ── espaço em disco (pra tela de Ajustes) ────────────────────────────────
    fun bytesEmDisco(c: Context): Long = raiz(c).walkBottomUp()
        .filter { it.isFile }.sumOf { it.length() }

    /** Apaga uma arte baixada. O pack comum da cena fica (as outras artes usam). */
    fun apagarArte(c: Context, cena: String, arte: String) {
        pastaArte(raiz(c), cena, arte).deleteRecursively()
    }

    /** Apaga a cena inteira — todas as artes e o pack comum. */
    fun apagarCena(c: Context, cena: String) {
        pastaCena(raiz(c), cena).parentFile!!.deleteRecursively()
    }

    // ── encanamento ─────────────────────────────────────────────────────────
    private fun baixarTexto(url: String): String = http.newCall(
        Request.Builder().url(url).build()).execute().use { resp ->
        if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
        resp.body?.string() ?: throw IllegalStateException("resposta vazia")
    }

    private fun baixarBytes(url: String): ByteArray = http.newCall(
        Request.Builder().url(url).build()).execute().use { resp ->
        if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
        resp.body?.bytes() ?: throw IllegalStateException("resposta vazia")
    }

    /**
     * Baixa o ZIP, confere o sha256 e extrai. Só troca a pasta definitiva no
     * fim — pack pela metade nunca vira cena carregável.
     */
    private fun validarArquivo(info: ArquivoRemoto) {
        require(info.sha256.matches(Regex("[a-fA-F0-9]{64}"))) { "Hash completo do pacote ausente." }
        require(info.bytes in 1..128L * 1024 * 1024) { "Tamanho do pacote inválido." }
        require(info.arquivo.matches(Regex("pack/[a-z0-9_]+/(?:[a-z0-9_]+|_cena)\\.zip"))) { "Caminho de pacote inválido." }
    }

    private suspend fun baixarZip(url: String, sha: String, destino: File, andamento: (Long) -> Unit) {
        destino.parentFile?.mkdirs()
        val arquivo = File.createTempFile("pack-", ".download", destino.parentFile)
        val call = http.newCall(Request.Builder().url(url).build())
        try {
        suspendCancellableCoroutine<Unit> { cont ->
            cont.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    arquivo.delete()
                    if (cont.isActive) cont.resumeWithException(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.use { resp ->
                            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code}")
                            val body = resp.body ?: throw IOException("Resposta vazia.")
                            val digest = MessageDigest.getInstance("SHA-256")
                            arquivo.outputStream().use { output ->
                                body.byteStream().use { input ->
                                    val buffer = ByteArray(64 * 1024); var total = 0L
                                    while (true) {
                                        if (!cont.isActive) throw CancellationException()
                                        val n = input.read(buffer); if (n < 0) break
                                        total += n; if (total > 128L * 1024 * 1024) throw IOException("Pacote grande demais.")
                                        output.write(buffer, 0, n); digest.update(buffer, 0, n); andamento(total)
                                    }
                                }
                            }
                            val hash = digest.digest().joinToString("") { "%02x".format(it) }
                            if (hash != sha.lowercase()) throw IOException("Arquivo corrompido no download.")
                        }
                        if (cont.isActive) cont.resume(Unit) else arquivo.delete()
                    } catch (e: Exception) {
                        arquivo.delete()
                        if (cont.isActive) cont.resumeWithException(e)
                    }
                }
            })
        }
        instalarZip(arquivo.inputStream(), destino, sha)
        } finally { arquivo.delete() }
    }

    /**
     * Confere o sha256 e extrai o ZIP em [destino], trocando a pasta só no fim.
     * Separado do download porque é a parte arriscada (e a testável sem rede):
     * pack truncado não pode virar cena pela metade, e entrada de ZIP com `..`
     * não pode escrever fora da pasta do acervo.
     */
    internal fun extrairZip(dados: ByteArray, sha: String, destino: File) {
        if (sha.isNotEmpty() && !confere(dados, sha))
            throw IllegalStateException("arquivo corrompido no download")

        instalarZip(dados.inputStream(), destino, sha)
    }

    @Synchronized private fun instalarZip(stream: java.io.InputStream, destino: File, sha: String = "") {
        val tmp = File(destino.path + ".tmp")
        tmp.deleteRecursively(); tmp.mkdirs()
        try {
        var extraidos = 0L
        var arquivos = 0
        ZipInputStream(stream).use { zis ->
            while (true) {
                val entrada = zis.nextEntry ?: break
                val nome = entrada.name
                // Zip Slip: entrada com ".." escaparia da pasta do acervo.
                if (entrada.isDirectory || nome.contains("..") ||
                    nome.startsWith("/") || nome.contains("\\")) { zis.closeEntry(); continue }
                val alvo = File(tmp, nome)
                if (!alvo.canonicalPath.startsWith(tmp.canonicalPath + File.separator)) continue
                arquivos++
                alvo.parentFile?.mkdirs()
                alvo.outputStream().use { out ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val n = zis.read(buffer); if (n < 0) break
                        extraidos += n
                        if (extraidos > 256L * 1024 * 1024) throw IOException("Pacote expandido grande demais.")
                        out.write(buffer, 0, n)
                    }
                }
                zis.closeEntry()
            }
        }
        require(arquivos > 0) { "Pacote sem arquivos validos." }
        if (sha.isNotEmpty()) File(tmp, ".sha256").writeText(sha)
        val anterior = File(destino.path + ".anterior")
        anterior.deleteRecursively()
        if (destino.exists() && !destino.renameTo(anterior)) throw IOException("Não foi possível preservar o pacote anterior.")
        destino.parentFile?.mkdirs()
        if (!tmp.renameTo(destino)) {
            tmp.deleteRecursively()
            anterior.renameTo(destino)
            throw IllegalStateException("não consegui gravar a arte")
        }
        anterior.deleteRecursively()
        } finally { tmp.deleteRecursively() }
    }

    /** Compatibilidade com fixtures antigas; downloads exigem os 64 caracteres. */
    internal fun confere(dados: ByteArray, sha: String): Boolean {
        val d = MessageDigest.getInstance("SHA-256").digest(dados)
        val hex = StringBuilder(d.size * 2)
        for (b in d) hex.append("%02x".format(b))
        return hex.startsWith(sha.lowercase())
    }
}
