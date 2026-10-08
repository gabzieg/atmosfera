# .lgpd — registros vivos de conformidade (LGPD) do Terra

**Estado: RASCUNHO para revisão (2026-10-08).** Nada aqui é parecer jurídico nem
está aprovado por encarregado ou advogado. Os pontos que dependem de decisão
humana ou de revisão jurídica estão marcados **[DECISÃO]** e **[JURÍDICO]**.

Estrutura definida pela skill `lgpd-audit` (github.com/goul4rt/lgpd-skills,
commit `d85d79a`, licença MIT), adaptada a um app Android **sem backend próprio,
sem conta de usuário e sem anúncios/analytics**. O que não se aplica ao Terra
(schema de consentimento em banco, endpoints de DSAR, log encadeado, KMS) **não
foi criado** — ver "Não se aplica" abaixo.

## O que cada arquivo registra

| Arquivo | Conteúdo | Skill de origem |
|---|---|---|
| [STATUS.md](STATUS.md) | Onde a auditoria está, checklist com evidência | `lgpd-audit` |
| [gaps.md](gaps.md) | Lacunas priorizadas, com evidência e responsável | `lgpd-legacy-retrofit` |
| [data-map.md](data-map.md) | Inventário dos fluxos de dado, ligado ao código | `lgpd-data-mapping` |
| [legal-basis.md](legal-basis.md) | Base legal por atividade | `lgpd-legal-basis` |
| [ROPA.md](ROPA.md) | Registro de operações (Art. 37), modelo simplificado ANPD | `lgpd-ropa` |
| [encarregado.md](encarregado.md) | Designação e canal do titular | `lgpd-dpo-encarregado` |
| [eca-digital.md](eca-digital.md) | Aplicabilidade da Lei 15.211/2025 | `lgpd-eca-digital-minors` |
| [retention.md](retention.md) | Prazos de guarda e descarte | `lgpd-retention-erasure` |
| [RIPD/](RIPD/) | Avaliação de alto risco e dispensa de RIPD | `lgpd-ripd` |
| [dsar/workflow.md](dsar/workflow.md) | Como responder a pedidos de titular | `lgpd-dsar` |
| [incidents/](incidents/) | Plano de incidente e registro de 5 anos | `lgpd-incident-response` |
| [vendors/](vendors/) | Fornecedores e papéis | `lgpd-vendor-audit`, `lgpd-dpa` |
| [transfers/](transfers/) | Transferências internacionais | `lgpd-international-transfer` |
| [policies/](policies/) | Como a política pública é versionada | `lgpd-privacy-policy` |

## Relação com `docs/`

- **Textos públicos** (Política, Termos, Contato) continuam em
  [`docs/legal/`](../docs/legal/) — **não são duplicados aqui**. `policies/`
  só descreve o processo e lista as correções pendentes.
- **Este diretório é o registro interno** (quem trata o quê, com base em quê, por
  quanto tempo, com quem). Se o app mudar, atualize aqui **e** em `docs/legal/` no
  mesmo PR (a regra já existente em `CLAUDE.md` e no cabeçalho da política).
- O índice geral de documentação está em [`docs/README.md`](../docs/README.md).

## Não se aplica ao Terra (e por quê)

| Skill | Motivo |
|---|---|
| `lgpd-consent-schema` | Não há banco, conta nem ledger. O consentimento é a permissão de localização do Android, revogável nas configurações. |
| `lgpd-audit-logging`, `lgpd-encryption-keys` | Sem servidor próprio. Dados locais ficam em armazenamento privado do app. |
| `lgpd-anonymization` | Sem pipeline de analytics. |
| `lgpd-escritorio` | Skill para escritório de advocacia; o Terra não é um. Usada só pelo **formato de checklist com evidência**. |

## Revisão

Reabrir a cada **6 meses**, ou quando mudar: SDK/fornecedor, finalidade, dado
coletado, país de armazenamento, público-alvo, ou quando o app passar de
**100 mil instalações** (reavaliar "larga escala" e RIPD).
