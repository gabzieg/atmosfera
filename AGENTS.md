# Atmosfera — instruções para agentes

Valem para qualquer agente (Claude Code, Codex, Antigravity). As regras canônicas do projeto (comandos, gate, PR,
time, limites) estão no `CLAUDE.md` da raiz: leia antes de atuar. Idioma: pt-BR.

- dev-methodology: `9d5e7bf`

## Fluxo

O fluxo é o do Friday (`~/Friday`, skill `dev-methodology`): a consultora `atmosfera-lead` escreve a issue de cada
tarefa, revisa o PR e mescla; a desenvolvedora `atmosfera-dev` (no Conductor, `atmosfera-dev-<workspace>`, uma por
workspace) implementa com teste e abre o PR em draft. Qualquer modelo e qualquer ferramenta em qualquer papel; quem
implementou não revisa. Aqui ficam só as decisões deste projeto, que prevalecem sobre a skill.

- **Checklist:** `docs/dev/TASKS.md` (o que está em andamento) e `docs/dev/ROADMAP.md` (fases), não a issue «Plano» da
  skill; só a consultora os edita, no mesmo PR da entrega.
- **Gate:** `cd android-app && ./gradlew testDebugUnitTest lintDebug assembleDebug` no commit final, mais a verificação
  visual quando a tela muda (abaixo); o check `build` do GitHub é obrigatório no PR (ruleset). Sem a fila de CI da
  máquina. O `lintDebug` tem baseline (`app/lint-baseline.xml`): a CI só quebra em erro novo; não conserte aviso antigo
  em PR de outro assunto.
- **PR e merge:** como em "Regras de PR" do `CLAUDE.md`: toda mudança na `main` por PR, branch saindo da `main`
  atualizada (`front/`, `motor/`, `fix/`, `doc/` ou `chore/<slug>`), corpo no template (O que muda / Por que agora /
  Como verificar, só itens feitos marcados), merge commit (não squash), 0 aprovações exigidas. A consultora mescla
  depois do `build` verde e da própria revisão; mudança de ruleset ou de configuração do repositório é só do Gabriel.
- **Rafael:** não recebe tarefa nem decisão que o Gabriel não tenha atribuído.

## Rodar o app e conferir a tela

Mudou UI? A verificação é visual e o PR leva captura de tela; "testado" só depois de ver na tela (já pegamos chips fora
do viewport e pilha de imagens invisível só olhando). Os comandos estão em "Comandos" do `CLAUDE.md`: `adb devices`
(lista vazia: o dono liga um AVD, hoje `Pixel_8`; não suba o emulador sem pedido), `./gradlew installDebug`, abrir a
`MainActivity` ou o painel de debug pelo alias `.debug.DebugLauncher`, e conferir o foco com
`adb shell "dumpsys window | grep -i mCurrentFocus"` antes de dizer que abriu.

## Armadilhas que já morderam este projeto

- Billing e `targetSdk` têm prazo do Google: antes de baixar qualquer um, leia a tabela de prazos do `CLAUDE.md`.
- Puxe a `main` antes de mexer no `EffectEngine`: o Rafael também commita no motor.
- `*.jks`, `*.keystore`, `local.properties` e a chave de licença do Billing nunca entram no diff.
- Toda cor vem de `ui/theme`; nada de `Color(0xFF…)` ou `dp` solto na tela.

## Documentos legais

Política de Privacidade, Termos e Contato: as fontes são `PRIVACIDADE.md`, `TERMOS.md` e `CONTATO.md` em `docs/legal/`
(leia o `docs/legal/README.md`), com metadados em `docs/legal/revisao.json`; `historico/` e `revisao-2026-10-05/` são
histórico, não base. Consulta é só leitura. Ao atualizar (decisão do dono, 28/10/2026), edite só os Markdown e, se
afetado, o `revisao.json` (versão, data, nota da revisão, referências no README), e na mesma tarefa rode na raiz
`python3 scripts/sincronizar_documentos_legais.py`, depois `--check` e `git diff --check`: isso regrava o manifesto e as
nove páginas HTML (`docs/`, `public_html/` e os assets do app). Nunca edite manifesto nem HTML à mão; a CI recusa
cópia divergente. Gerar HTML não aprova a redação; o Google Sites é publicação à parte, só com autorização.
