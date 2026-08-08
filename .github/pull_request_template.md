<!--
Título em português, no imperativo: "Corrige vazamento ao trocar de cenário".
Quando o PR é obrigatório, o que pode ir direto na main e os escape hatches:
.claude/skills/abrir-pr/SKILL.md
-->

## O que muda


## Por que agora


## Como verificar


---

- [ ] Branch saiu da `main` atualizada
- [ ] Sem segredo no diff: `*.jks`, `*.keystore`, `local.properties`, chave de licença do Billing
- [ ] Gate da CI passa: `./gradlew testDebugUnitTest lintDebug assembleDebug` (lint roda com baseline — só quebra em erro NOVO)
- [ ] Tocou `engine/**` ou `assets/atmosfera/**`? Combinou com o Rafael antes — é a fronteira do motor (ver [HANDOFF-FRONTEND.md](../docs/dev/HANDOFF-FRONTEND.md))
- [ ] Mudou UI? Tem **screenshot** no PR (o projeto não tem teste automatizado — a verificação é visual)
- [ ] Cor/espaçamento novo? Saiu de `ui/theme` (nada de `Color(0xFF…)` ou `dp` solto na tela)
- [ ] Permissão nova no `AndroidManifest`? Justificada aqui e refletida em [CHECKLIST_PUBLICACAO.md](../docs/dev/CHECKLIST_PUBLICACAO.md) (Data Safety Form)
- [ ] Mexeu na versão do Billing ou no `targetSdk`? Os dois têm **mínimo exigido pelo Google** (ver tabela de prazos no CLAUDE.md) — abaixar reprova a publicação
- [ ] Asset novo pesado? Conferiu o tamanho (o repo já carrega PNGs de ~2 MB)
- [ ] Doc afetada (`README` / `CLAUDE.md` / `docs/dev/HANDOFF-FRONTEND.md`) continua verdadeira

<!--
Aprovação por área — o CODEOWNERS pede o revisor sozinho:
  engine/ · assets/atmosfera/           → Rafael (dono do motor)
  billing/ · docs/legal/                → Willian
  ui/ · weather/ · service/ · docs/dev/ → Gabriel (front)
  doc, texto, protótipo                 → CI verde: pode mergear você mesmo

Ignorou algum aviso do CI de propósito? Escreva o porquê em uma linha aqui,
pra não reabrir a discussão depois.
-->
