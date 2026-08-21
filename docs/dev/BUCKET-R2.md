# Bucket do acervo (Cloudflare R2) — o que precisa existir

Instrução curta pra quem for criar o bucket. O que o app espera está em
[ENTREGA-DE-ARTE.md](ENTREGA-DE-ARTE.md); aqui é só o passo a passo da conta.

## 1. Criar

1. Conta em [dash.cloudflare.com](https://dash.cloudflare.com) → **R2**.
2. **Create bucket**, nome `atmosfera-acervo`, região automática.
3. Em **Settings → Public access → Allow Access**, liberar o domínio público
   (`https://pub-<id>.r2.dev`). Domínio próprio (ex.: `acervo.atmosfera.app`)
   funciona igual e é melhor de trocar depois — mas não é pré-requisito.

Por que público: o app baixa a arte sem login, e a **compra é validada pela Play,
não pelo arquivo** — o bucket não guarda nada que dê dinheiro. Baixar a arte de
graça não destrava o cenário no app.

## 2. Subir

O conteúdo é a pasta `dist/` gerada por `tools/pacote_cenas.py` (rodar dentro de
`atmosfera 2.0/`). Hoje: **31 cenas, 131 artes, 114 MB** — cabe folgado no
plano grátis (10 GB de armazenamento e egress zero).

Subir preservando a estrutura, a raiz do bucket sendo a raiz do `dist/`:

```
manifest.json
thumb/<cena>__<arte>.webp
preview/<cena>__<arte>.webp
pack/<cena>/_cena.zip
pack/<cena>/<arte>.zip
```

Dá pra arrastar a pasta no painel do R2, ou pela linha de comando com
[rclone](https://rclone.org/s3/) / `aws s3 sync` (o R2 fala S3):

```bash
rclone sync dist/ r2:atmosfera-acervo --checksum --progress
```

**Ordem importa numa atualização:** subir primeiro os arquivos novos, o
`manifest.json` **por último**. O app só pede o que está no índice — se o índice
chegar antes do arquivo, quem abrir a Loja nesse intervalo toma 404.

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

- Conferir no navegador: `<URL base>manifest.json` tem que abrir e listar as
  cenas.
- No app debug: apontar o servidor, escolher um cenário e usar **"Baixar arte
  desta cena"** — o painel mostra o progresso e a prévia recarrega com a arte
  baixada.
