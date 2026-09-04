# Bucket do acervo (Cloudflare R2) — o que precisa existir

Instrução curta pra quem for criar o bucket. O que o app espera está em
[ENTREGA-DE-ARTE.md](ENTREGA-DE-ARTE.md); aqui é só o passo a passo da conta.

## 1. Criar

1. Conta em [dash.cloudflare.com](https://dash.cloudflare.com) → **R2**.
2. **Ativar o R2 exige um cartão na conta**, mesmo ficando no plano grátis: a
   Cloudflare faz uma autorização temporária de US$ 5 (não é cobrança) e só
   fatura quem passar dos 10 GB / 1 milhão de escritas por mês. Nosso acervo
   inteiro são 266 MB — a conta fica em zero. Este passo é do dono da conta;
   ninguém mais digita cartão por ele.
3. **Create bucket**, nome `atmosfera-acervo`, região automática.
4. Em **Settings → Public access → Allow Access**, liberar o domínio público
   (`https://pub-<id>.r2.dev`). Domínio próprio (ex.: `acervo.atmosfera.app`)
   funciona igual e é melhor de trocar depois — mas não é pré-requisito.
5. **R2 → API → Create API token**, permissão **Object Read & Write**, escopo
   só neste bucket. Guardar o par Access Key ID / Secret — é com ele que o
   `tools/subir_acervo.py` sobe o acervo.

Se um dia não quiser cartão nenhum na conta: **Cloudflare Pages** (upload
direto, sem repositório) serve os mesmos arquivos estáticos, sem exigir meio de
pagamento, e o app não sabe a diferença — muda só a URL base. O limite lá são
25 MiB por arquivo e 20 mil arquivos por deploy; hoje o acervo tem 821 arquivos
e o maior tem 2,2 MB, então caberia. R2 continua sendo a escolha por causa do
egress zero, que é exatamente o custo de um app que só faz download.

Por que público: o app baixa a arte sem login, e a **compra é validada pela Play,
não pelo arquivo** — o bucket não guarda nada que dê dinheiro. Baixar a arte de
graça não destrava o cenário no app.

## 2. Subir

O conteúdo é a pasta `dist/` gerada por `tools/pacote_cenas.py` (rodar dentro de
`atmosfera 2.0/`). Hoje (regerado em 2026-09-03): **55 cenas, 255 artes,
266 MB** — 232 MB de packs, 29 MB de preview, 5 MB de thumb. Cabe folgado no
plano grátis (10 GB de armazenamento e egress zero); mesmo triplicando o acervo
continua dentro.

Subir preservando a estrutura, a raiz do bucket sendo a raiz do `dist/`:

```
manifest.json
thumb/<cena>__<arte>.webp
preview/<cena>__<arte>.webp
pack/<cena>/_cena.zip
pack/<cena>/<arte>.zip
```

Usar `tools/pacote_cenas.py` para gerar e **`tools/subir_acervo.py` para subir**
(os dois rodam de dentro de `atmosfera 2.0/`):

```bash
python tools/subir_acervo.py --simular
```

```bash
python tools/subir_acervo.py
```

As credenciais nunca vão na linha de comando (ficariam no histórico do shell):
o script lê variáveis de ambiente ou `%USERPROFILE%\.atmosfera-r2.env`, que fica
fora do repositório:

```
R2_ACCOUNT_ID=...
R2_ACCESS_KEY_ID=...
R2_SECRET_ACCESS_KEY=...
R2_BUCKET=atmosfera-acervo
R2_PUBLIC_URL=https://pub-xxxxxxxx.r2.dev/
```

O script existe porque arrastar a pasta no painel (ou `rclone sync` cru) erra
três coisas que quebram o app em produção:

- **Ordem.** Ele sobe o `manifest.json` **por último**, sempre. O app só pede o
  que está no índice — índice antes do arquivo = 404 pra quem abrir a Loja
  naquele intervalo.
- **`Cache-Control`.** `immutable` de um ano na arte, `max-age=300` no índice
  (ver §4). O painel do R2 não põe nenhum dos dois.
- **`Content-Type`.** `image/webp` e `application/zip` corretos.

Além disso ele compara tamanho + MD5 com o ETag do bucket e só manda o que
mudou, então republicar depois de regerar o `dist/` custa só as cenas novas.
`--limpar` (opcional) apaga do bucket o que saiu do `dist/`; sem a flag, nada é
apagado.

## 3. Ligar no app

Uma constante: `Acervo.BASE_PADRAO` em
`app/src/main/java/com/atmosfera/wallpaper/engine/Acervo.kt` recebe a URL com
barra no fim:

```kotlin
const val BASE_PADRAO = "https://pub-<id>.r2.dev/"
```

Em build de debug dá pra apontar pra outro servidor sem recompilar: painel de
teste → seção **Acervo** → campo do servidor (é assim que se testa contra o
servidor local do tester, `http://10.0.2.2:8123/dist/` no emulador).

## 4. Cache

Os arquivos são imutáveis (arte nova = nome novo no manifesto), então vale pôr
`Cache-Control: public, max-age=31536000, immutable` neles — **menos no
`manifest.json`**, que precisa de algo curto (`max-age=300`) pra cena nova
aparecer no mesmo dia. No R2 isso é a aba **Settings → Object lifecycle /
metadata**, ou o header no upload.

## 5. Depois de subir

- `python tools/subir_acervo.py --verificar` — faz o GET do `manifest.json` na
  URL pública, conta as cenas e confere o `Content-Type`/`Cache-Control` de um
  pack e de um thumb de amostra. Roda sozinho no fim de cada upload.
- Ou no navegador: `<URL base>manifest.json` tem que abrir e listar as cenas.
- No app debug: apontar o servidor, escolher um cenário e usar **"Baixar arte
  desta cena"** — o painel mostra o progresso e a prévia recarrega com a arte
  baixada.
