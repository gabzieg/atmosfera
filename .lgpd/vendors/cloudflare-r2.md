# Fornecedor — Cloudflare R2 (entrega do acervo)

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-vendor-audit`.
**Estado: INATIVO na 1.0.5.** `Acervo.BASE_PADRAO` está vazio (`engine/Acervo.kt:86`); só o painel de debug baixa. Este registro vale a partir da versão que ativar a Loja de packs.

| Campo | Valor |
|---|---|
| Identificação | Cloudflare, Inc. (EUA) |
| Serviço | R2 (armazenamento de objetos), bucket `terra-acervo`, classe Standard, região Eastern North America |
| Finalidade | Hospedar os pacotes de cenário/arte baixados sob demanda |
| Dados que recebe | O pedido HTTP do arquivo e o **IP** do aparelho (inerente). **Sem localização**, sem identificador. |
| **Papel (proposta)** | **Operador** do Terra (hospedagem de arquivos públicos); também é controlador dos seus próprios registros de acesso. **[JURÍDICO]** |
| Contrato / DPA | Os termos de conta da Cloudflare costumam incorporar um *Data Processing Addendum*. **Não verificado nesta rodada** — confirmar o texto vigente, se inclui cláusulas de transferência internacional e se há as **Cláusulas-Padrão brasileiras da Res. 19/2024**. |
| Titular da conta | Rafael Huppes (conforme o próprio, em 2026-10-04) |
| Quem administra / alertas de consumo | **[DECISÃO]** — pergunta 12 do Rafael aberta; monitor ainda não ativado |
| Tier | **Baixo** (só IP de downloads) |
| Última revisão / próxima | 2026-10-08 / ao ativar o recurso, depois anual |

## Estado do ambiente (segundo o PR #43, ainda em rascunho)

- URL pública de **teste**: `r2.dev` (a Cloudflare reserva esse domínio para desenvolvimento e aplica limites). **Não há URL de produção** (pergunta 10).
- Acesso público ativado pelo titular; **token de upload sem data de expiração**, restrito a este bucket, protegido pelo Windows fora do repositório.
- Cliente padrão do Python recebeu 403 (bloqueio de bot da Cloudflare); o OkHttp, que o app usa, recebe 200 (verificado em 2026-10-05).

## Riscos específicos

- **Integridade do acervo**: o manifesto e os pacotes ficam no **mesmo bucket**. O app confere o hash do pacote com o do manifesto; quem controla o bucket controla os dois. Antes de ativar: considerar manifesto assinado (G14 em [gaps.md](../gaps.md)).
- **Token sem expiração** e URL pública sem controle de acesso: baixar a arte de graça não libera compras, mas a arte é copiável.

## Pendências

- [ ] **[DECISÃO]** URL de produção (domínio próprio) e responsável pela operação e alertas.
- [ ] Confirmar o DPA da Cloudflare e as cláusulas de transferência.
- [ ] Definir validade do token e rotina de rotação.
- [ ] Atualizar a Política §3.8/§6.5 para dizer que o recurso **só vale a partir da versão que o ativar** (G15).
