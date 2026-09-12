---
name: abrir-pr
description: Fluxo de pull request do Atmosfera — decide se a mudança exige PR ou pode ir direto na main, nomeia a branch, roda o gate de build, revisa o diff (/code-review) e abre o PR já preenchido. Use ao abrir PR, preparar branch, ou antes de commitar/pushar em `.github/` — desde 2026-09-11 a única área que ainda exige PR.
---

# Abrir PR no Atmosfera

Projeto efetivamente solo (**Gabriel** + Claude) desde 2026-09-11 — o **Rafael**
ficou só com a publicação de releases de novos packs de wallpaper (conteúdo),
não toca mais código. Historicamente todo mundo commitava direto na `main`, o
que já custou um merge conflitado no `EffectEngine`; hoje o único portão que
sobra é o `.github/` (o meta-guarda), e a regra abaixo existe só pra isso.

## 1. Precisa de PR?

**SIM — abra branch + PR** se o diff toca:

| Caminho | Por quê |
|---|---|
| `.github/**` | É o meta-guarda: quebrar aqui desliga todos os outros guardas (CI, hook, aviso). |

**NÃO precisa** — pode commitar direto na `main`: **todo o resto, `engine/` e
`assets/atmosfera/` incluídos.** Documentação, texto, `ui/`, `weather/`,
`service/`, `billing/`, `engine/`, `assets/`, manifesto, `build.gradle`.

> **A lista encolheu duas vezes.** Em 2026-08-09 saíram `billing/`,
> `AndroidManifest.xml` e `build.gradle` (a proteção virou automática — o
> `PermissoesDeclaradasTest` cobre permissão, o gate da CI cobre o build; e o
> teto frágil do Billing acabou na migração pra 9.1.0). Em **2026-09-11** saíram
> `engine/` e `assets/atmosfera/`: o motivo do gate ali era "o Rafael evolui isso
> em paralelo e entrega por snapshot", mas ele passou a só publicar packs e o
> `engine/` virou do Gabriel — sem trabalho paralelo, não há conflito de snapshot
> a evitar, e PR pra si mesmo não revisa nada.
>
> Ficou só o `.github/`: o arquivo que desliga os próprios guardas.

> Julgamento que continua valendo: se a mudança altera **que dado é coletado**
> (mesmo dentro de `weather/`), pare e pense antes — é LGPD. O teste de
> permissões cobre o manifesto, mas não cobre, por exemplo, passar a mandar a
> localização pra um endpoint novo.

## 2. Branch

```
chore/<slug>    # mudanças em .github/ (CI, hook, workflows) — a área que exige PR
front/<slug>    # ui, billing, weather, service
motor/<slug>    # engine/assets (não exige mais PR; o prefixo só ajuda a ler o log)
fix/<slug>      # correção pontual
doc/<slug>      # documentação
```

Sempre saindo da `main` atualizada:

```bash
git switch main && git pull --rebase origin main
git switch -c front/loja-mosaico
```

## 3. Antes de abrir: rode o gate de verdade

```bash
cd android-app && ./gradlew testDebugUnitTest lintDebug assembleDebug
```

A CI (`.github/workflows/build.yml`) roda os três: testes unit, `lintDebug`
(com baseline) e `assembleDebug`, além de uma varredura de segredo (gitleaks).
O `lintDebug` tem um baseline (`app/lint-baseline.xml`) que congela os avisos
pré-existentes — a CI só quebra em erro NOVO. Não tente "consertar" um aviso
antigo dentro de um PR de outro assunto (se for corrigir, regenere o baseline).

**Mudou UI? Anexe screenshot.** O projeto não tem nenhum teste automatizado —
a verificação é visual. Use a skill `run` para instalar, abrir e capturar a tela.
Não escreva "testado" sem ter rodado: coisas que compilam ainda quebram na tela
(já pegamos chips fora do viewport e pilha de imagens invisível só olhando).

## 3.5. Revisão do diff (passo separado, não é o mesmo que "gate verde")

Gate verde (teste+lint+build) prova que o código roda — não prova que é a
decisão certa. Antes de `git push`/`gh pr create`, rode `/code-review` no
diff como passo próprio, especialmente em mudanças de código (não
necessariamente pra doc solta).

**Honestidade sobre o limite disto:** isto é uma revisão do **mesmo modelo**
que escreveu o código — pega inconsistência com convenção do `CLAUDE.md`,
edge case esquecido, lógica capenga, mas **não substitui revisão humana**
(o Gabriel enxerga contexto de produto e domínio que eu não tenho). Como a
revisão é solo, o gate automático (teste+lint+build+gitleaks) é o revisor de
verdade; o `/code-review` só levanta o piso do que chega até você.

## 4. Armadilhas que já morderam este projeto

- **Billing e `targetSdk` têm prazo do Google, não são preferência.** Hoje em
  Billing 9.1.0 e `targetSdk` 36 (mínimos exigidos a partir de 31/ago/2026).
  Antes de baixar qualquer um dos dois, leia a tabela de prazos no `CLAUDE.md` —
  abaixar reprova a publicação.
- **Motor agora é nosso (desde 2026-09-11).** `engine/` e `assets/` viraram do
  Gabriel; edite direto na `main`, como o resto. O Rafael não entrega mais
  snapshot de código — só publica packs de conteúdo.
- **Segredos.** `*.jks`, `*.keystore`, `local.properties` e a chave de licença do
  Billing nunca entram no diff (o `.gitignore` cobre a maioria, não confie nele).
- **Cor/espaçamento fora do tema.** Toda cor vem de `ui/theme`; nada de
  `Color(0xFF…)` ou `dp` solto na tela.

## 5. Abrir o PR

```bash
git push -u origin <branch>
gh pr create --fill    # o template em .github/pull_request_template.md já vem junto
```

Preencha as três seções (**O que muda / Por que agora / Como verificar**) e
marque só os itens que se aplicam — item marcado sem ter sido feito é pior que
item desmarcado.

## 6. Aprovação e merge

**O `.github/CODEOWNERS` NÃO pede revisor sozinho** — o repo é privado no plano
free, e a API responde `403 Upgrade to GitHub Pro`. O arquivo existe como
convenção e fica pronto pro dia que o plano mudar. Quem cobra é você.

Desde 2026-09-11 o projeto é solo: **tudo é do Gabriel** — engine, assets, front,
billing, docs legais, publicação. O Rafael só publica releases de novos packs de
conteúdo; não aprova nem revisa código. Na prática: gate verde e merge.

Os documentos legais tinham revisor próprio (o Willian) até 2026-08-28, quando
ele saiu do projeto. **Não substitua isso por uma cerimônia de PR consigo
mesmo** — não é revisão. O que protege texto legal aqui é o guarda automático:
`PaginasLegaisSincronizadasTest` (as três cópias batem) e
`PoliticaBatecomManifestoTest` (a política não pode listar permissão que o app
não pede, nem omitir uma que pede).

Como a revisão é solo na maior parte do repo, **o gate é o revisor de verdade**:
testes + lint + build + gitleaks. Vale mais investir em guarda automático (como
o `PermissoesDeclaradasTest`) do que em cerimônia de PR que ninguém lê.

Ignorou um aviso do CI de propósito? Escreva o porquê em uma linha no PR.
