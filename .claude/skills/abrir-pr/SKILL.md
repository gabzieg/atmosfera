---
name: abrir-pr
description: Fluxo de pull request do Atmosfera — decide se a mudança exige PR ou pode ir direto na main, nomeia a branch, roda o gate de build, revisa o diff (/code-review) e abre o PR já preenchido. Use ao abrir PR, preparar branch, ou antes de commitar/pushar qualquer coisa que toque motor, billing, manifesto, build.gradle ou CI.
---

# Abrir PR no Atmosfera

Projeto de 2 pessoas: **Rafael** (motor) e **Gabriel** (front). Historicamente
todo mundo commitava direto na `main` — isso já custou um merge conflitado no
`EffectEngine`. A regra abaixo existe para evitar exatamente isso, sem virar
burocracia no resto.

## 1. Precisa de PR?

**SIM — abra branch + PR** se o diff toca qualquer um destes:

| Caminho | Por quê |
|---|---|
| `engine/**` · `assets/atmosfera/**` | Fronteira do motor. O Rafael evolui isso em paralelo e entrega por snapshot — editar aqui sem avisar gera conflito de merge (já aconteceu, e é o incidente que originou esta regra). |
| `.github/**` | É o meta-guarda: quebrar aqui desliga todos os outros. |

**NÃO precisa** — pode commitar direto na `main`: todo o resto. Documentação,
texto, `ui/`, `weather/`, `service/`, `billing/`, manifesto, `build.gradle`.

> **A lista encolheu em 2026-08-09.** Antes incluía `billing/`,
> `AndroidManifest.xml` e `build.gradle`. Saíram porque a proteção virou
> automática e a revisão é solo — PR pra si mesmo não revisa nada, só adia:
>
> - `AndroidManifest.xml` → `PermissoesDeclaradasTest` quebra o gate em qualquer
>   mudança de permissão, que era o risco real (Data Safety divergente do APK).
>   Guarda mais forte que auto-revisão, e roda na CI.
> - `build.gradle` → "quebra o build de todo mundo" é exatamente o que o gate
>   pega, antes do merge.
> - `billing/` → o motivo era "dinheiro + teto frágil do Billing". O teto acabou
>   na migração pra 9.1.0, e a área voltou pro Gabriel.
>
> Ficou o que **nenhum teste cobre**: trabalho paralelo de outra pessoa, e o
> arquivo que desliga os testes.

> Julgamento que continua valendo: se a mudança altera **que dado é coletado**
> (mesmo dentro de `weather/`), pare e pense antes — é LGPD. O teste de
> permissões cobre o manifesto, mas não cobre, por exemplo, passar a mandar a
> localização pra um endpoint novo.

## 2. Branch

```
motor/<slug>    # mudanças no engine/assets (combinar com o Rafael antes)
front/<slug>    # ui, billing, weather, service
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
(o Gabriel/Rafael enxergam contexto de produto e domínio que eu não tenho).
Em área de risco (§1) o revisor humano continua sendo o gate real; isto só
levanta o piso do que chega até ele.

## 4. Armadilhas que já morderam este projeto

- **Billing e `targetSdk` têm prazo do Google, não são preferência.** Hoje em
  Billing 9.1.0 e `targetSdk` 36 (mínimos exigidos a partir de 31/ago/2026).
  Antes de baixar qualquer um dos dois, leia a tabela de prazos no `CLAUDE.md` —
  abaixar reprova a publicação.
- **Motor "congelado".** Se precisar mesmo mexer, avise o Rafael e registre no PR
  — o snapshot dele pode sobrescrever sua correção num merge futuro.
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

| Área | Aprova |
|---|---|
| `engine/` · `assets/atmosfera/` | Rafael — combine ANTES, o snapshot dele pode sobrescrever sua correção |
| Documentos legais (`docs/legal/`, espelhos HTML) | Willian — é texto que vale juridicamente |
| Todo o resto (`ui/`, `weather/`, `service/`, `billing/`, build, manifesto) | Gabriel — na prática, gate verde e merge |

Como a revisão é solo na maior parte do repo, **o gate é o revisor de verdade**:
testes + lint + build + gitleaks. Vale mais investir em guarda automático (como
o `PermissoesDeclaradasTest`) do que em cerimônia de PR que ninguém lê.

Ignorou um aviso do CI de propósito? Escreva o porquê em uma linha no PR.
