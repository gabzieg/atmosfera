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
  visual (skill `run`) quando a tela muda; o check `build` do GitHub é obrigatório no PR (ruleset). Sem a fila de CI da
  máquina.
- **PR e merge:** como na skill `abrir-pr` e em "Regras de PR" do `CLAUDE.md`: toda mudança na `main` por PR, merge
  commit (não squash), 0 aprovações exigidas. A consultora mescla depois do `build` verde e da própria revisão;
  mudança de ruleset ou de configuração do repositório é só do Gabriel.
- **Rafael:** não recebe tarefa nem decisão que o Gabriel não tenha atribuído.
