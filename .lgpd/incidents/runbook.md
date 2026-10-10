# Plano de resposta a incidente de segurança

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-incident-response` (Art. 48 LGPD + Res. CD/ANPD nº 15/2024).
Este plano cobre **incidente que afeta dado pessoal**. Falhas de produto (crash, compra sem entrega) seguem o procedimento de [`docs/dev/OPERACAO-POS-LANCAMENTO.md`](../../docs/dev/OPERACAO-POS-LANCAMENTO.md); se envolverem dado pessoal, **abrem também este plano**.

## Quem faz o quê **[DECISÃO]**

| Papel | Quem | Contato |
|---|---|---|
| Encarregado / decisão de notificar | Rafael Huppes (proposta) | [telefone — preencher fora do Git] |
| Resposta técnica | Gabriel | [telefone — preencher fora do Git] |
| Substituto | A definir | — |

## O que conta como incidente aqui (cenários reais do Terra)

Como não há servidor com dados de usuários, os incidentes plausíveis são poucos e **o impacto em dado pessoal tende a ser baixo**. O risco maior é de **integridade** (alguém fazer o app entregar algo malicioso).

| # | Cenário | Dado pessoal em risco? | O que fazer primeiro |
|---|---|---|---|
| 1 | **Caixa de suporte (Gmail) comprometida** | **Sim**: e-mails, nomes, relatos e anexos de quem escreveu | Trocar senha, encerrar sessões, revisar encaminhamentos e filtros, ativar/verificar verificação em duas etapas, avaliar quantas pessoas escreveram |
| 2 | **Keystore de upload/assinatura vazada** | Não diretamente | Pedir ao Google a troca da chave de envio; pausar publicação; trocar senhas; ver [`docs/dev/CHECKLIST_PUBLICACAO.md`](../../docs/dev/CHECKLIST_PUBLICACAO.md) |
| 3 | **Token do Cloudflare R2 vazado ou bucket alterado** (quando o acervo estiver ativo) | Não diretamente | Revogar o token, conferir objetos e o manifesto, restaurar o último estado conhecido. **O app confere o hash do pacote contra o manifesto do mesmo bucket**; quem controla o bucket controla os dois — ver G14 em [gaps.md](../gaps.md) |
| 4 | **MET Norway bloqueia o app (403) ou limita (429)** | Não | O app já tem freio persistente (`weather/FreioMet.kt`); conferir User-Agent e contato |
| 5 | **Dado pessoal enviado por engano** (ex.: resposta de suporte ao destinatário errado) | **Sim** | Conter, avisar o destinatário, registrar |

## Critério de notificação (Res. 15/2024, art. 5º — **cumulativo**)

Notificar a ANPD **se e somente se** houver **A. risco ou dano relevante** aos titulares **e** **B.** envolver ao menos uma destas categorias: (I) dados sensíveis; (II) **crianças, adolescentes ou idosos**; (III) financeiros; (IV) autenticação; (V) sigilo legal/judicial/profissional; (VI) larga escala.

Nota para o Terra: como o público inclui **adolescentes 13–17**, o item (II) pode ser atendido por um incidente que atinja a caixa de suporte. **Avaliar caso a caso e documentar a decisão.** **[JURÍDICO]**

## Prazos

| Ação | Prazo |
|---|---|
| Notificar a ANPD | **3 dias úteis** desde o conhecimento de que afetou dado pessoal. Para ATPP a skill registra o dobro (6 dias úteis), salvo risco à integridade física/moral — **adotar 3 como meta interna** **[JURÍDICO]** |
| Notificar os titulares | Mesmo prazo e gatilho |
| Complementar a comunicação | Até 20 dias úteis depois |
| **Registrar o incidente (todos, notificados ou não)** | **5 anos** em [log.md](log.md) |

## Passo a passo (hora 0 → 72 h)

**T+0 a 4 h — conter e preservar**
- Registrar o **horário exato do conhecimento** (é o marco do prazo). Acionar o encarregado na hora.
- Isolar (trocar senhas, revogar tokens/sessões, retirar acesso).
- **Preservar evidência** (prints, logs, e-mails). Não apagar nada.

**T+4 a 24 h — avaliar**
- Que dados, quantas pessoas, **há adolescentes?**, o dado estava protegido?
- Aplicar o teste A+B acima e **decidir por escrito** (encarregado).

**T+24 a 72 h — comunicar (se notificável)**
- À ANPD pelo portal do gov.br/anpd, com os **12 itens** do art. 6º, §2º da Res. 15/2024.
- Ao titular, em **linguagem simples**, com os **7 itens** do art. 9º.
- Modelos nos arquivos `assets/notification-anpd.md` e `notification-subject.md` da skill `lgpd-incident-response`.

**Depois**
- Causa raiz, correção, prazo, responsável; atualizar este plano. Registrar em [log.md](log.md).

## Exercício (tabletop)

Uma vez por ano, simular o cenário 1 e cronometrar. Se passar de 3 dias úteis até a decisão, o plano está quebrado. Primeiro exercício: **[DECISÃO: data]**.
