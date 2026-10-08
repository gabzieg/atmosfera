# Transferências internacionais

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **[JURÍDICO]** todas as hipóteses abaixo são candidatas, não decisões.
Base: LGPD Arts. 33–36 e Res. CD/ANPD nº 19/2024 (Cláusulas-Padrão Contratuais brasileiras), conforme skill `lgpd-international-transfer`.

## Destinos

| Destino | País | Dados | Atividade | Estado |
|---|---|---|---|---|
| MET Norway | Noruega (Espaço Econômico Europeu) | Coordenadas (~1 km), IP, User-Agent | A001 | **Ativa** |
| Google (Play Services, Billing, Backup, Gmail) | EUA e outros | Conforme [vendors/google.md](../vendors/google.md) | A004, A005, A006 | **Ativa** |
| Cloudflare R2 | EUA | IP e pedido do arquivo | A007 | **Inativa** na 1.0.5 |

## Hipótese do Art. 33 por destino (candidatas)

| Destino | Hipótese candidata | Por quê | Observação |
|---|---|---|---|
| **MET Norway** | **Art. 33, IX** (necessária para executar o serviço pedido pelo titular — combinado com o Art. 7º, V). Alternativa: **Art. 33, VIII** (consentimento específico e em destaque). | A transferência é o que o usuário pede ao ativar o clima. **Não há** como assinar cláusulas-padrão com uma API pública. A ANPD ainda não publicou lista de países adequados (a skill registra isso). | O texto do onboarding precisa ser **específico e em destaque** se a base for o VIII — ver [legal-basis.md](../legal-basis.md) |
| **Google** | A conferir nos termos de cada serviço | Sem contrato individual | **[JURÍDICO]** |
| **Cloudflare** | **Art. 33, II, "b"** (cláusulas-padrão) se o DPA vigente as incluir; senão, IX | Terra é cliente; o DPA faz parte dos termos | Verificar antes de ativar |

## Erro a corrigir na Política

A Política §7 (v1.4) cita **"art. 33, II, alíneas 'a' e 'f', e art. 33, VIII"**. **A alínea "f" não existe**: o inciso II tem só as alíneas **a** a **d** (cláusulas específicas, cláusulas-padrão, normas corporativas globais, selos/certificados/códigos de conduta). O rascunho de revisão de 05/10 já remove a "f", **mas não define a base** — isso depende de decisão jurídica (G03 em [gaps.md](../gaps.md)).

## Salvaguardas já adotadas

Coordenadas arredondadas, nenhum identificador de usuário, HTTPS, acesso à rede só durante o uso (MET), nenhuma localização para a Cloudflare.

## Pendências

- [ ] **[JURÍDICO]** Escolher a hipótese de cada destino e redigir o §7 da Política.
- [ ] Confirmar o DPA da Cloudflare e os termos do Google (Play, Gmail).
- [ ] Revisar anualmente, ou ao mudar de fornecedor/país.
