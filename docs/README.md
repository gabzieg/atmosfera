# Índice da documentação

**Atualizado em 2026-10-08.** Fonte única do que cada documento é e se ainda vale. Se um arquivo contradiz o `git log`, o `git log` ganha.

Legenda: **Vigente** — descreve o estado atual · **Parcial** — vale em parte (ver nota) · **Superado** — mantido como histórico · **Volátil** — muda toda semana · **Rascunho** — não aprovado.

> **Atenção à publicação:** `docs/` pode virar site público (GitHub Pages / Cloudflare Pages). `build-legal-pages.sh` usa **lista de permissão**: só `privacidade/`, `termos/` e `contato/` são publicados. **Não coloque nada sensível fora dessas três pastas esperando que fique privado sem conferir o script.** Registros internos de LGPD ficam em [`/.lgpd`](../.lgpd/README.md), na raiz, **fora** de `docs/`.

## Textos públicos e conformidade

| Arquivo | Papel | Estado |
|---|---|---|
| [legal/PRIVACIDADE.md](legal/PRIVACIDADE.md) | Política — texto **canônico** | **Vigente**, v1.4, mas com `[PREENCHER]` e divergente dos HTML (G01) |
| [legal/TERMOS.md](legal/TERMOS.md) | Termos de Uso — canônico | **Parcial**: §5.1 descreve a oferta antiga do Premium (G04) |
| [legal/CONTATO.md](legal/CONTATO.md) | Contato e uso de dados — canônico | **Vigente**, com `[PREENCHER]` |
| `privacidade/`, `termos/`, `contato/` | HTML publicados (espelho do canônico) | **Vigente**; 3 cópias (`docs/`, `public_html/`, assets do app) |
| [../.lgpd/](../.lgpd/README.md) | **Registros vivos de LGPD** (ROPA, fornecedores, retenção, incidentes…) | **Rascunho** |

## Produto e estratégia

| Arquivo | Papel | Estado |
|---|---|---|
| [dev/SPEC.md](dev/SPEC.md) | O que é "pronto", monetização, requisitos | **Vigente** (monetização de 29/09) |
| [dev/PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md](dev/PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md) | Decisões de 29/09 sobre acervo remoto e Premium | **Vigente** (§5.1) |
| [dev/PLANO-LANCAMENTO.md](dev/PLANO-LANCAMENTO.md) | Plano até a Play | **Parcial**: o aviso do topo diz o que foi superado |
| [dev/ROADMAP.md](dev/ROADMAP.md) | Fases até publicar | **Parcial**: status de 28/08 |
| [dev/TASKS.md](dev/TASKS.md) | Trabalho em andamento | **Volátil** |
| [dev/OPERACAO-POS-LANCAMENTO.md](dev/OPERACAO-POS-LANCAMENTO.md) | Primeiros 90 dias | **Parcial**: ainda diz "conteúdo embarcado, sem CDN" (18/09), anterior à decisão de 29/09 (G18) |

## Publicação (Play Store)

| Arquivo | Papel | Estado |
|---|---|---|
| [dev/CHECKLIST_PUBLICACAO.md](dev/CHECKLIST_PUBLICACAO.md) | Pendências de Play Store | **Vigente** |
| [dev/GUIA_PLAY_CONSOLE.md](dev/GUIA_PLAY_CONSOLE.md) | Respostas do Data Safety e do IARC | **Vigente** (revisar com o resultado de G06/G11) |
| [dev/PREMIUM-PLAY-CONSOLE.md](dev/PREMIUM-PLAY-CONSOLE.md) | Fechamento do Premium no Console | **Parcial**: estado de 20/09 |
| [loja/FICHA.md](loja/FICHA.md) | Textos da ficha | **Rascunho** (descreve o catálogo remoto de forma incompleta) |
| [loja/README.md](loja/README.md) | Material gráfico | **Vigente** |

## Engenharia

| Arquivo | Papel | Estado |
|---|---|---|
| [dev/CLIMA-SOMENTE-HOME.md](dev/CLIMA-SOMENTE-HOME.md) | Clima só na home (PR #46) e roteiro de aceite em aparelho | **Vigente** |
| [dev/HANDOFF-FRONTEND.md](dev/HANDOFF-FRONTEND.md) | Contrato motor ↔ front | **Vigente** como referência (a divisão de times acabou em 2026-09-11) |
| [dev/ENTREGA-DE-ARTE.md](dev/ENTREGA-DE-ARTE.md) | Como o download do acervo funciona | **Vigente** (referência técnica) |
| [dev/BUCKET-R2.md](dev/BUCKET-R2.md) | Operação do bucket | **Parcial**: a versão nova está no PR #43 (rascunho) |
| [dev/DECISAO-ENTREGA-DE-ARTE.md](dev/DECISAO-ENTREGA-DE-ARTE.md) | Decisão em camadas de 11/09 | **Superado** em 28–29/09 |
| [dev/DECISAO-AGP-9-MIGRACAO.md](dev/DECISAO-AGP-9-MIGRACAO.md) | Por que AGP 9 foi revertido | **Vigente** como histórico de decisão |
| [dev/AUDITORIA-PUBLICACAO-2026-09-17.md](dev/AUDITORIA-PUBLICACAO-2026-09-17.md) | Diagnóstico pontual | **Superado** (histórico) |
| [dev/analise-concorrentes-atmosfera.pdf](dev/analise-concorrentes-atmosfera.pdf) | Pesquisa de mercado | Referência |

## Regra para manter este índice honesto

Ao criar, aposentar ou reescrever um documento, **mude a linha dele aqui no mesmo PR**. Documentos de análise pontual (auditorias, diagnósticos) ficam fora do Git, a menos que se decida o contrário; o que precisa viver é registrado em `.lgpd/` ou nos documentos acima.
