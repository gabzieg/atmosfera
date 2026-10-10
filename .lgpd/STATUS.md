# LGPD Audit Status

**Projeto**: Terra - Live Wallpaper (`com.terra.wallpaper`)
**Cenário**: B/C adaptado (app não publicado, com documentação existente)
**Início**: 2026-10-08 · **Última atualização**: 2026-10-08
**Código de referência**: `main` @ `2d01d16` — app 1.0.5, versionCode 6
**Encarregado**: proposta Rafael Huppes — pendente de designação ([encarregado.md](encarregado.md))
**Skills**: `lgpd-skills` (goul4rt) @ `d85d79a`, MIT

> Rascunho. Nada aqui foi aprovado por encarregado ou advogado. Os itens com
> **[JURÍDICO]** e **[DECISÃO]** espelham os checkpoints obrigatórios da skill
> `lgpd-audit` (ela proíbe publicar política, ROPA final ou comunicação à ANPD sem revisão humana).

## Pipeline

- [x] L0 — Estrutura `.lgpd/` ([README.md](README.md))
- [x] L1 — Gap analysis → [gaps.md](gaps.md) ⏸ **CHECKPOINT: aguardando sua revisão**
- [x] L2 — Mapa de dados → [data-map.md](data-map.md)
- [x] L3 — Base legal → [legal-basis.md](legal-basis.md) *(rascunho)*
- [x] L4 — Fornecedores → [vendors/](vendors/) · Transferências → [transfers/README.md](transfers/README.md) *(rascunho)*
- [x] L5 — Retenção → [retention.md](retention.md) *(rascunho; prazo do atendimento pendente)*
- [ ] L6 — Anonimização: **não se aplica** (sem pipeline de analytics)
- [x] L7 — Direitos do titular → [dsar/workflow.md](dsar/workflow.md) *(adaptado: sem conta nem endpoints)*
- [x] L8 — Plano de incidente → [incidents/](incidents/)
- [ ] L9 — Política de privacidade → lista de correções em [policies/README.md](policies/README.md) ⏸ **CHECKPOINT: v1.5 ainda não redigida**
- [x] L10 — ECA Digital → [eca-digital.md](eca-digital.md) *(provavelmente aplicável; alcance a validar)*
- [x] L11 — RIPD → **dispensa documentada** ([RIPD/dispensa-2026-10-08.md](RIPD/dispensa-2026-10-08.md))
- [x] L12 — Encarregado → [encarregado.md](encarregado.md) *(proposta)*
- [x] ROPA → [ROPA.md](ROPA.md) *(v0.1)*

## Checklist de auditoria com evidência

Formato emprestado da skill `lgpd-escritorio`: **cada item precisa de evidência guardável**; sem evidência, é "não conforme".

| Item | Evidência esperada | Estado |
|---|---|---|
| Controlador identificado na política | Texto canônico preenchido | **Não conforme** (G01) |
| Canal do titular publicado e funcionando | URL no ar + e-mail testado | **Não conforme** (G02) |
| Encarregado designado e divulgado | Declaração assinada + texto publicado | **Pendente** (G10) |
| Registro de operações (Art. 37) | [ROPA.md](ROPA.md) aprovado | **Rascunho** (G13) |
| Base legal registrada por atividade | [legal-basis.md](legal-basis.md) | **Rascunho** |
| Teste de legítimo interesse onde usado | LIA | **N/A** após G07 |
| Política publicada com versão e histórico | URL + histórico | **Parcial** (versão e histórico existem; sem URL) |
| Política acessível no app | Ajustes → Política (WebView offline) | **Conforme** (`ui/SettingsTab.kt`) |
| Tabela de retenção aprovada | [retention.md](retention.md) | **Rascunho** |
| Fluxo de pedido de titular | [dsar/workflow.md](dsar/workflow.md) + planilha | **Rascunho** |
| Plano de incidente com nomes e telefones | [incidents/runbook.md](incidents/runbook.md) com contatos | **Rascunho** (contatos vazios) |
| Registro de incidentes (5 anos) | [incidents/log.md](incidents/log.md) | **Conforme** (vazio, sem incidentes conhecidos) |
| Contrato/cláusulas com cada operador | DPA/termos arquivados | **Não conforme** (G12) |
| Transferência internacional mapeada e fundamentada | [transfers/README.md](transfers/README.md) validado | **Não conforme** (G03) |
| Avaliação de alto risco | [RIPD/dispensa-2026-10-08.md](RIPD/dispensa-2026-10-08.md) | **Rascunho** |
| ECA Digital avaliado | [eca-digital.md](eca-digital.md) | **Rascunho** (G11) |
| Treinamento de equipe | Lista de presença | **N/A** (sem equipe com acesso a dado de usuário além do responsável) |

## Gaps abertos

Ver [gaps.md](gaps.md): 4 críticos · 6 altos · 8 médios · 1 baixo.

## Próximo passo

Revisar [gaps.md](gaps.md) e decidir G04, G10 e G11. Em paralelo: corrigir G05, G07, G08, G09 e G16 (pequenos), iniciar G02 com o Willian e levar G03, G11, G12 e G20 ao advogado.
