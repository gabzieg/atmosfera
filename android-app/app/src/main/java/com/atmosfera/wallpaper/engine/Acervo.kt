package com.atmosfera.wallpaper.engine

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
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
 * Por que existe: com a biblioteca inteira dentro do APK ele foi a 844 MB e a
 * Play aceita 500 MB no módulo base. Fora do app, o instalado cai pra ~16 MB
 * (motor + folhas de efeito + o wallpaper grátis) e cada wallpaper comprado
 * pesa 0,5–1,2 MB. Ver `docs/dev/ENTREGA-DE-ARTE.md`.
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
    const val BASE_PADRAO = ""

    private const val PREFS = "atmosfera_acervo"
    private const val KEY_BASE = "base_url"
    private const val TTL_MANIFESTO_MS = 6 * 60 * 60 * 1000L   // 6 h

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun base(c: Context): String =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_BASE, BASE_PADRAO)
            ?.takeIf { it.isNotBlank() }?.let { if (it.endsWith("/")) it else "$it/" } ?: ""

    /** Troca o servidor. Existe pra teste local (`http://10.0.2.2:8123/dist/`)
     *  sem precisar rebuildar o app. */
    fun definirBase(c: Context, url: String) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_BASE, url).apply()
    }

    // ── onde as coisas ficam no aparelho ─────────────────────────────────────
    fun raiz(c: Context): File = File(c.filesDir, "acervo")

    /** Pasta com fundo/frente/luzes_off de uma ARTE. */
    fun pastaArte(raiz: File, cena: String, arte: String) = File(raiz, "pack/$cena/$arte")

    /** Pasta com o que é comum à cena (zonas, profundidade). */
    fun pastaCena(raiz: File, cena: String) = File(raiz, "pack/$cena/_cena")

    /** Já dá pra rodar esta arte sem rede? */
    fun temArte(c: Context, cena: String, arte: String): Boolean {
        val r = raiz(c)
        return File(pastaArte(r, cena, arte), "fundo.webp").exists() &&
            File(pastaCena(r, cena), "zonas.webp").exists()
    }

    // ── manifesto ────────────────────────────────────────────────────────────
    /**
     * Índice remoto. Usa o cache local quando ele é recente (ou quando não há
     * rede) — é isso que faz a Loja abrir offline mostrando o que já viu.
     * Sem servidor configurado e sem cache, devolve [Manifesto.VAZIO].
     */
    suspend fun manifesto(c: Context, forcar: Boolean = false): Manifesto =
        withContext(Dispatchers.IO) {
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
        val faltaCena = !File(pastaCena(r, cena), "zonas.webp").exists()
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
                baixarZip(url + z.arquivo, z.sha256, pastaCena(r, cena), andamento)
                base = z.bytes
            }
            baixarZip(url + a.pack.arquivo, a.pack.sha256, pastaArte(r, cena, arte), andamento)
            send(Progresso.Baixando(1f))
            send(Progresso.Ok)
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
        File(raiz(c), "pack/$cena").deleteRecursively()
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
    private fun baixarZip(url: String, sha: String, destino: File, andamento: (Long) -> Unit) {
        val dados = http.newCall(Request.Builder().url(url).build()).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
            val body = resp.body ?: throw IllegalStateException("resposta vazia")
            val fonte = body.byteStream()
            val buf = ByteArray(64 * 1024)
            val saida = java.io.ByteArrayOutputStream()
            var lidos = 0L
            while (true) {
                val n = fonte.read(buf)
                if (n <= 0) break
                saida.write(buf, 0, n); lidos += n; andamento(lidos)
            }
            saida.toByteArray()
        }
        extrairZip(dados, sha, destino)
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

        val tmp = File(destino.path + ".tmp")
        tmp.deleteRecursively(); tmp.mkdirs()
        ZipInputStream(dados.inputStream()).use { zis ->
            while (true) {
                val entrada = zis.nextEntry ?: break
                val nome = entrada.name
                // Zip Slip: entrada com ".." escaparia da pasta do acervo.
                if (entrada.isDirectory || nome.contains("..") ||
                    nome.startsWith("/") || nome.contains("\\")) { zis.closeEntry(); continue }
                File(tmp, nome).outputStream().use { zis.copyTo(it) }
                zis.closeEntry()
            }
        }
        destino.deleteRecursively()
        destino.parentFile?.mkdirs()
        if (!tmp.renameTo(destino)) {
            tmp.deleteRecursively()
            throw IllegalStateException("não consegui gravar a arte")
        }
    }

    /** O manifesto guarda só os 16 primeiros hex do sha256 (o arquivo é nosso,
     *  não é defesa contra adversário — é contra download truncado). */
    internal fun confere(dados: ByteArray, sha: String): Boolean {
        val d = MessageDigest.getInstance("SHA-256").digest(dados)
        val hex = StringBuilder(d.size * 2)
        for (b in d) hex.append("%02x".format(b))
        return hex.startsWith(sha.lowercase())
    }
}
