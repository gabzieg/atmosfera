# Entrega de arte por download (o app deixa de carregar a biblioteca dentro)

Status: **fase 1 pronta no código; fase 2 travada esperando a URL do bucket.**
Escrito em 2026-08-20, números remedidos em 2026-09-03. Dono do subsistema:
Rafael (motor).

## 1. Por que

Hoje toda a arte mora em `assets/atmosfera/` e vai dentro do APK.

| | hoje | com download |
|---|---|---|
| `assets/atmosfera/` dentro do APK | **1,58 GB** | **0,6 MB** (só o wp grátis) |
| biblioteca inteira (55 cenas / 255 artes) | dentro do app | **266 MB** no bucket |
| baixar 1 wallpaper comprado | — | **0,27–2,21 MB** (mediana 0,93) |
| grade da Loja inteira em thumbnail | — | 5,2 MB (~20 KB cada) |
| preview de pré-venda (543×724) | — | ~115 KB cada |
| índice (`manifest.json`) | — | 77 KB |

A Play aceita no máximo **500 MB** no módulo base — ou seja, hoje o app **não
pode ser publicado**, e a distância só cresce: em 2026-08-20 eram 31 cenas e
770 MB de arte, hoje são 55 cenas e 1,58 GB. Números medidos, não estimados:
rodar `python tools/pacote_cenas.py` (na pasta `atmosfera 2.0/`) reproduz.

O maior download (`telhados`/`point`, 2,21 MB) é o teto atual; ele passou de
1,2 MB porque entraram cenas com arte mais detalhada que a média de agosto.

Formato: **WebP q92** nas artes (1,88 MB → 0,26 MB; PSNR 40 dB, sem artefato
visível a 3× de zoom em pixel art) e **WebP lossless** em `zonas` e
`profundidade`, que são MÁSCARA — ali a cor é dado (amarelo = pingo sólido,
vermelho = pingo na água; o cinza é distância) e compressão com perda
inventaria zona na borda. As folhas de sprite também vão a lossless
(21,2 MB → 14,3 MB) e continuam DENTRO do app.

## 2. O que fica no app e o que baixa

**Dentro do APK/AAB (~16 MB):** código, as 24 folhas de efeito
(`sprites_*.png` → `.webp` lossless), `neve_acumulo*`, `nevoa`, `bandeira_*`, e
**a cabana em pixel** — o wallpaper grátis. O app precisa funcionar na primeira
abertura, sem rede e sem conta.

**No bucket:** todo o resto, no layout que `tools/pacote_cenas.py` gera:

```
manifest.json                     índice (40 KB)
thumb/<cena>__<arte>.webp         240×320  — grade da Loja
preview/<cena>__<arte>.webp       543×724  — tela do cenário (pré-venda)
pack/<cena>/_cena.zip             zonas.webp + zonas.json + profundidade.webp
pack/<cena>/<arte>.zip            fundo.webp + frente.webp + luzes_off.webp
```

ZIP por **arte**, não por cena: cena com 8 artes pesa 8×, e o usuário só precisa
da arte que está usando. O `_cena.zip` (zonas/profundidade) é comum às artes e
baixa uma vez.

`manifest.json` traz, por arquivo, `bytes` + `sha256` (16 hex) — dá pra validar
o download e detectar arte atualizada sem baixar de novo:

```json
{"versao":1,"formato":"webp","qualidade":92,
 "cenas":{"porto":{
   "cena":{"arquivo":"pack/porto/_cena.zip","bytes":111983,"sha256":"c4ae17b8b2890a17"},
   "artes":{"pixel":{
     "pack":   {"arquivo":"pack/porto/pixel.zip","bytes":515376,"sha256":"544246b70dbe99f6"},
     "thumb":  {"arquivo":"thumb/porto__pixel.webp","bytes":10080,"sha256":"3fe2e5f29bb04b51"},
     "preview":{"arquivo":"preview/porto__pixel.webp","bytes":55226,"sha256":"bb3bb2bf158326e4"}}}}}}
```

**Por que R2 e não Play Asset Delivery:** com asset pack do Google, cada cena
nova exige uma release nova do app. O ritmo aqui é de cena por semana — com
bucket + manifest, wallpaper novo entra no ar sem passar pela Play.

## 3. A interface (o que o front chama)

Tudo que o front precisa saber cabe num objeto novo do motor,
`engine/Acervo.kt`. **A ideia é que a Loja não fale de HTTP, arquivo nem
cache** — ela pergunta e pede, como já faz com `Catalogo`/`Plano`:

```kotlin
object Acervo {
    /** Já está no aparelho e pronto pra usar? */
    fun temArte(c: Context, cena: String, arte: String): Boolean

    /** Baixa arte + o pack da cena. Emite progresso 0f..1f e termina em
     *  Resultado.Ok ou Resultado.Erro(motivo). Idempotente e cancelável. */
    fun baixarArte(c: Context, cena: String, arte: String): Flow<Progresso>

    /** Miniatura/preview do cache; baixa se faltar. null = sem rede e sem cache. */
    suspend fun thumb(c: Context, cena: String, arte: String): File?
    suspend fun preview(c: Context, cena: String, arte: String): File?

    /** Índice remoto (cache local com TTL). Vazio = offline na primeira vez. */
    suspend fun catalogoRemoto(c: Context): Manifesto

    /** Espaço ocupado e faxina — pra tela de Ajustes, quando ela existir. */
    fun bytesEmDisco(c: Context): Long
    fun apagarArte(c: Context, cena: String, arte: String)
}
```

Regras que o motor garante (o front não precisa tratar):

- **Nada de tela branca.** Cena pedida sem arquivo baixado → o motor cai no
  wallpaper grátis e avisa por `Cena`; nunca fica preto.
- **Offline** → `catalogoRemoto` devolve o último índice em cache; sem cache
  nenhum, devolve vazio e a Loja mostra o estado "sem conexão". O que já está
  baixado continua funcionando 100% offline, inclusive o wallpaper rodando.
- **Compra é a fonte da verdade**, não o arquivo: o download só começa depois de
  `Plano`/`Catalogo` liberarem. Arquivo baixado não destrava cenário.

## 4. Impacto real no trabalho do Gabriel

Curto: **dá pra entregar isso sem tocar em nenhuma tela dele.** As telas
continuam chamando `SceneThumbnail(sceneId, arte)` com a mesma assinatura — o
que muda é de onde aquele bitmap sai, e isso é uma função privada de 7 linhas.

| Arquivo | Muda o quê | Dono | Tamanho |
|---|---|---|---|
| `engine/Acervo.kt` (novo) | download, cache, manifest, sha256 | Rafael | ~250 linhas |
| `engine/EffectEngine.kt` | carrega de `filesDir` (cai nos assets se for o wp grátis); nomes `.webp` | Rafael | ~15 linhas |
| `assets/atmosfera/**` | sai a biblioteca, ficam sprites + cabana pixel | Rafael | remoção |
| `ui/components/SceneThumbnail.kt` | `assets.open(...)` → `Acervo.thumb(...)`; assinatura pública IGUAL | Rafael (componente, não tela) | ~7 linhas |
| `ui/StoreTab.kt`, `ui/SceneDetailScreen.kt`, `ui/HomeTab.kt` | **nada obrigatório** | Gabriel | 0 |

O que fica **opcional** pro Gabriel, quando ele quiser (é UX, não encanamento):

1. Botão que hoje é "comprar" virar **comprar → baixar com barra de progresso**
   (o `Flow<Progresso>` já entrega o número).
2. Estado **"sem conexão"** na Loja, em vez de grade vazia.
3. **"Gerenciar downloads"** em Ajustes (quanto ocupa, apagar arte que não usa) —
   `bytesEmDisco`/`apagarArte` já existem pra isso.

Sem esses três, o app funciona: a Loja mostra as miniaturas, comprar baixa com
o spinner que já existe, e o wallpaper aplica. Com eles, fica bom.

**O que exige conversa com ele antes:** nada de código. Só o aviso de que a
primeira versão com isso **invalida o cache de assets** — quem já tem o app
instalado vai rebaixar a arte do cenário que estiver usando (0,5–1,2 MB).

## 5. Ordem de execução

| Fase | O quê | Depende de |
|---|---|---|
| 0 ✅ | `tools/pacote_cenas.py`: `dist/` com manifest, thumb, preview e packs | — |
| 1 ✅ | `Acervo` + motor lendo de `filesDir`, testado contra um servidor local | nada (dá pra fazer já) |
| 2 ⛔ | Subir `dist/` no R2, apontar a URL base, medir download real no aparelho | conta R2 + URL |
| 3 ⛔ | Tirar a biblioteca do `assets/`, converter sprites p/ WebP, medir o AAB | fase 2 verde |
| 4 | UX de download na Loja/Ajustes | Gabriel, quando quiser |

O que a fase 1 entregou: [`engine/Acervo.kt`](../../android-app/app/src/main/java/com/atmosfera/wallpaper/engine/Acervo.kt)
(manifesto, sha256, download cancelável com `Flow<Progresso>`, cache em
`filesDir/acervo/`, `bytesEmDisco`/`apagarArte`), `EffectEngine` carregando do
acervo com queda pros assets, e a seção **Acervo** do painel de debug (campo do
servidor + "baixar arte desta cena"). `Acervo.BASE_PADRAO` continua `""` — é a
única linha que a fase 2 precisa preencher.

`SceneThumbnail.kt` ainda lê `assets.open("atmosfera/…fundo.png")`: é a troca de
7 linhas da tabela acima e só faz sentido junto com a fase 3, senão a Loja
passaria a baixar thumb de arte que já está dentro do APK.

A fase 1 roda inteira contra `http://localhost:8123/dist/` — o mesmo servidor
do tester. Não fico parado esperando o bucket.

## 6. Pendências desta decisão

- **URL base do bucket** (`https://<algo>.r2.dev/` ou domínio próprio) e se o
  conteúdo fica público ou assinado. Público é mais simples e o risco é só
  cópia de arte, não de dinheiro — a compra continua sendo validada pela Play.
- **Segundo wallpaper grátis**: hoje o plano leva só a cabana pixel. Cabe outro
  em ~0,8 MB se ele quiser dois na instalação.
- **Política de faxina**: apagar arte não usada depois de X dias, ou só manual.
