# Política de privacidade — versionamento e correções pendentes

**Versão deste documento**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-privacy-policy`.
Os **textos públicos não ficam aqui**: a fonte é [`docs/legal/`](../../docs/legal/). Este arquivo só diz como versionar e lista o que corrigir na próxima versão.

## Regra de versão (da skill)

Mudança **material** (finalidade, base legal, novo compartilhamento, transferência nova, retenção, direitos) → **nova versão** e comunicação nas notas de versão da Play. Mudança cosmética → micro-versão (1.4 → 1.4.1).
**Nunca publicar sem revisão jurídica** — checkpoint obrigatório da skill.

## Os 7 elementos do Art. 9º — conferência da v1.4

| # | Elemento | Estado | Onde |
|---|---|---|---|
| 1 | Finalidade específica | **OK** | §3 |
| 2 | Forma e duração do tratamento | **OK**, exceto atendimento | §3, §8 |
| 3 | Identificação do controlador | **Falha** no texto canônico (`[PREENCHER]`); preenchido só nos HTML | §1 |
| 4 | Contato do controlador/encarregado | **Falha** no canônico; contradição sobre o encarregado | §1, §14 |
| 5 | Uso compartilhado e finalidade | **OK** com ressalvas (classificar os papéis; geocoder fora) | §6 |
| 6 | Responsabilidades dos agentes | **Fraco**: chama todo terceiro de "operador técnico" | §6 |
| 7 | Direitos do titular, citando o Art. 18 | **OK** | §10 |
| — | Versão, vigência e histórico | **OK**, mas "vigente desde" ainda é `[PREENCHER]` | topo, §16 |
| — | Bases legais, transferência, retenção | **Tem erros** (ver abaixo) | §3, §7, §8 |

## Correções para a próxima versão (v1.5) — checklist

Cada item cita a lacuna em [../gaps.md](../gaps.md).

- [ ] **G01** — Preencher controlador (Rafael Huppes, pessoa física), e-mail e URL no **texto canônico**; hoje só os HTML estão preenchidos.
- [ ] **G06** — Acrescentar **seção de atendimento** (e-mail e dados do relato automático: versão do app, modelo, Android, cenário/arte/estilo; provedor Gmail; retenção) e reescrever §4 — hoje diz que o app "não coleta e-mail".
- [ ] **G03** — Reescrever §7 sem a alínea "f" e com a hipótese decidida pelo advogado.
- [ ] **G07** — §3.2 (IP) e §3.6 (backup): remover "legítimo interesse" (o Terra não trata o IP; o backup é do sistema).
- [ ] **G10** — §1 e §14: alinhar a designação do encarregado.
- [ ] **G11** — §11: alinhar ao resultado da análise do ECA Digital (13+ ou 18+).
- [ ] **G15** — §3.8 e §6.5: dizer que o download remoto vale **a partir da versão que o ativar**.
- [ ] **§6** — Substituir "operadores técnicos" por papéis corretos (ver [../vendors/](../vendors/)).
- [ ] **Vigência** — preencher "vigente desde" **só na data real** de entrada em vigor; não inventar data.

## Processo de promoção

1. Atualizar o rascunho para refletir o código atual. O pacote `docs/legal/revisao-2026-10-05/` (hoje **fora do Git**, só na máquina do Gabriel) é anterior ao PR #46 e ainda fala de worker em segundo plano.
2. Revisão jurídica (checkpoint).
3. Promover para `docs/legal/*.md`; **no mesmo commit**, atualizar as 3 cópias HTML (`docs/`, `public_html/`, assets do app) — o teste `PaginasLegaisSincronizadasTest` cobre `docs/` × assets; **não cobre** `public_html/` nem o Markdown.
4. Novo AAB (as cópias do app são fixas por versão) e importação pelo repositório do site do Willian.
