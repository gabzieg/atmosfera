# Fornecedor — Google (Play Services, Play Billing, Backup, Gmail, Play Console)

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-vendor-audit`.
O Google aparece em **cinco serviços distintos** no Terra. Os termos de cada um são **não negociáveis** (não há como assinar DPA individual); o tratamento segue as condições públicas do Google.

| Serviço | Uso no Terra | Dados | Papel (proposta) **[JURÍDICO]** |
|---|---|---|---|
| **Google Play Services — Localização** (`FusedLocationProviderClient`) | Obter coordenadas aproximadas no aparelho | Localização processada pelo sistema; o Terra recebe só as coordenadas | Serviço do sistema do aparelho do usuário; o Google é controlador do que processa por conta própria |
| **Google Play Billing / Google Play** | Compra e restauração | Pagamento e histórico ficam com o Google; o Terra recebe estado, ID de produto e recibo assinado | Google como controlador do pagamento; o Terra, do que recebe |
| **Backup do Android** | Cópia automática (`allowBackup="true"`) das preferências e do cache | Preferências e cache de clima (com coordenadas) **se não excluídos** | Mecanismo do sistema, na conta Google do usuário; o Terra não acessa |
| **Gmail** (conta `suporteterrabr@gmail.com`) | Caixa de atendimento | E-mails, nomes, relatos e anexos de quem escreve | Provedor de e-mail do controlador (equivale a operador) |
| **Google Play Console** | Distribuição e métricas do app | Dados agregados de instalações, avaliações e falhas que o usuário optou por compartilhar | Controlador conjunto/independente conforme os termos de distribuição do Google |

| Campo | Valor |
|---|---|
| Contrato | Termos do desenvolvedor do Google Play (aceitos ao criar a conta) e termos de cada serviço |
| Transferência internacional | Os serviços do Google podem tratar dado nos EUA e em outros países — ver [transfers/README.md](../transfers/README.md) |
| Tier | **Alto pela dependência** (sem alternativa), **baixo pelo acesso a dados** pelo Terra |
| Última revisão / próxima | 2026-10-08 / 2027-10-08 |

## Pendências

- [ ] **[JURÍDICO]** Papel de cada serviço (a política v1.4 chama todos de "operadores técnicos"; isso não vale para todos).
- [ ] Excluir o cache de coordenadas do backup (G08).
- [ ] Verificar no Play Console o que a Google oferece sobre idade/família (ver [eca-digital.md](../eca-digital.md)).
- [ ] Ativar verificação em duas etapas e documentar quem acessa a caixa de suporte.
