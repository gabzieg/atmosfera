---
name: abrir-pr
description: Fluxo de pull request do Atmosfera — toda mudança na main entra por PR (ruleset do servidor exige PR + CI `build` verde, 0 aprovações). Nomeia a branch, roda o gate de build, revisa o diff (/code-review), abre o PR já preenchido e mescla com merge commit. Use ao abrir PR, preparar branch ou levar trabalho para a main.
---

# Abrir PR no Atmosfera

Projeto efetivamente solo (**Gabriel** + Claude, dos dois lados) — o **Rafael**
publica packs de conteúdo e, desde 2026-09-23, voltou a escrever código próprio
(ver `CLAUDE.md` → "O time"). Historicamente todo mundo commitava direto na
`main`, o que já custou um merge conflitado no `EffectEngine`. Hoje isso não é
mais possível: o servidor exige PR.

## 1. Precisa de PR?

**Sempre, para qualquer arquivo.** Desde 2026-08-02 existe um ruleset na
`main` (verificado via `gh api` em 2026-09-29/30): PR obrigatório, check
`build` obrigatório, **0 aprovações** (o autor mescla o próprio PR depois da CI
verde), force-push e deleção bloqueados. `git push origin main` é recusado pelo
servidor, e `--no-verify` não contorna — ele só desliga hooks locais.

> **Histórico:** este arquivo dizia que só `.github/` exigia PR e "todo o resto
> pode ir direto na `main`". A lista de áreas de risco encolheu em 2026-08-09 e
> 2026-09-11 — mas o ruleset do servidor já exigia PR para tudo desde 2026-08-02,
> e ninguém tinha conferido. A lista ainda existe no `.githooks/pre-push` e no
> CODEOWNERS e perdeu o efeito prático (o `aviso-push-direto.yml` foi removido
> em 2026-09-30 — só gerava alarme falso).

> Julgamento que continua valendo: se a mudança altera **que dado é coletado**
> (mesmo dentro de `weather/`), pare e pense antes — é LGPD. O teste de
> permissões cobre o manifesto, mas não cobre, por exemplo, passar a mandar a
> localização pra um endpoint novo.

## 2. Branch

```
chore/<slug>    # mudanças em .github/ (CI, hook, workflows) e build
front/<slug>    # ui, billing, weather, service
motor/<slug>    # engine/assets
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
  Gabriel; edite direto (via PR, como o resto). O Rafael não entrega mais
  snapshot de código, mas voltou a commitar fixes próprios em 2026-09-23 — puxe
  a `main` antes de mexer no `EffectEngine`.
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

**Merge (verificado 2026-09-30):**

- Aprovação: **0 exigidas** pelo ruleset — o Gabriel mescla o próprio PR assim
  que o check `build` fica verde. (Era 1 até 2026-09-30; num time solo isso só
  fazia o PR depender de o Rafael clicar em "aprovar".)
- Método: **merge commit** (`gh pr merge <n> --merge`). Preserva o histórico —
  os documentos citam hashes de commit. Squash e rebase também estão habilitados;
  rebase costuma falhar com branch que contém merge commits.
- O repo tem **"Automatically delete head branches" ligado**: ao mesclar, o
  GitHub apaga a branch de origem. Se era a branch de trabalho
  (`integracao/lancamento-teste`), recrie com `git push origin <branch>` depois
  de alinhá-la à `main` (`git merge --ff-only origin/main`).
- O `gh` está instalado e autenticado como `gabzieg` (`gh pr create`,
  `gh pr checks <n> --watch`, `gh pr merge`). Mudar ruleset/configuração do repo
  é do Gabriel, pela interface — o classificador do modo automático do Claude
  Code bloqueia o agente de enfraquecer proteção de branch.
- Uma resposta `502 Bad Gateway` do `gh pr merge` não significa que falhou:
  confira com `gh pr view <n> --json state,mergeCommit` antes de tentar de novo.

Na prática: gate verde local → push → PR → CI verde → `gh pr merge --merge`.

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
