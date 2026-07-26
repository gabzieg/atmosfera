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
- [ ] `./gradlew assembleDebug` passa (é o **único** gate da CI — `lintDebug` não roda e tem 2 erros pré-existentes conhecidos)
- [ ] Tocou `engine/**` ou `assets/atmosfera/**`? Combinou com o Rafael antes — é a fronteira do motor (ver [HANDOFF-FRONTEND.md](../HANDOFF-FRONTEND.md))
- [ ] Mudou UI? Tem **screenshot** no PR (o projeto não tem teste automatizado — a verificação é visual)
- [ ] Cor/espaçamento novo? Saiu de `ui/theme` (nada de `Color(0xFF…)` ou `dp` solto na tela)
- [ ] Permissão nova no `AndroidManifest`? Justificada aqui e refletida em [CHECKLIST_PUBLICACAO.md](../CHECKLIST_PUBLICACAO.md) (Data Safety Form)
- [ ] Subiu a versão do Billing? Confirmou o teto do Kotlin 1.9.23 — 7.0.0+ **quebra o build** (ver CLAUDE.md)
- [ ] Asset novo pesado? Conferiu o tamanho (o repo já carrega PNGs de ~2 MB)
- [ ] Doc afetada (`README` / `CLAUDE.md` / `HANDOFF-FRONTEND.md`) continua verdadeira

<!--
Aprovação por área — o CODEOWNERS pede o revisor sozinho:
  engine/ · assets/atmosfera/           → Rafael (dono do motor)
  ui/ · billing/ · weather/ · service/  → Gabriel (front)
  doc, texto, protótipo                 → CI verde: pode mergear você mesmo

Ignorou algum aviso do CI de propósito? Escreva o porquê em uma linha aqui,
pra não reabrir a discussão depois.
-->
