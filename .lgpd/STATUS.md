# Análise documental LGPD — Terra

**Atualização de 19/10:** registros da PR #52 (`c746d19`) incorporados em `1f21454` e ajustados: retenção diária por data de encerramento, direitos abrangentes, evidência privada de incidentes e risco sem dispensa presumida. [Diagnóstico do teste](../docs/dev/DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md) e [Segurança dos dados](seguranca-dados-play-2026-10-19.md) preparados. API de sinais de idade confirmada, com análise de integração pendente. Sete contas de licença informadas por Rafael, sem contagem auditada de adesões/checkout. AAB local de 06/10 reprovado por HTMLs desatualizados; candidato e maior versionCode do Console não identificados. Não houve mudança no app, novo AAB, formulário enviado ou publicação. **22/10:** candidato 7 (`c1848a9`) gerado em 20/10 e mesclado à `main`; ver [diagnóstico](../docs/dev/DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado). Na mesma data conferiu-se leitura direta do Play Console (conta pessoal de Rafael Huppes, titular e controlador) também pelo Gabriel, superando quanto ao Console o registro de 15/10 abaixo; Cloudflare R2 e Gmail de suporte seguem só com o Rafael — ver [SPEC.md](../docs/dev/SPEC.md#publicação-na-play-store--quem-faz-o-quê) → "Acesso às contas".

**Etapa 2 — preparação técnica/documental concluída; aceite pendente:** evidências em [evidencias-privacidade-etapa2.md](evidencias-privacidade-etapa2.md), rotina de suporte/direitos/retenção proposta e roteiro de conferência para Rafael. Só ele tem acesso às contas, conforme confirmação do usuário *(registro de 15/10 superado em 22/10 quanto ao Play Console; ver nota de 22/10 acima e [SPEC.md](../docs/dev/SPEC.md#publicação-na-play-store--quem-faz-o-quê) → "Acesso às contas")*; não foram presumidas configurações. MET/Oslo, diferença entre Lixeira e exclusão e recurso Data Access Logs do R2 conferidos nas fontes oficiais. Cache HTTP adicional do clima incorporado ao inventário e ao rascunho de Privacidade 1.14; Termos 1.8 e Contato 1.6 preservados. Nenhuma base legal aprovada, configuração alterada ou ação de descarte executada.

Conciliação em 15/10/2026. Base remota incorporada: `0d36b4a`, com decisões de Rafael. Preservado o refinamento local dos relatos, direitos e oferta. Fontes resultantes: **Privacidade 1.13, Termos 1.8 e Contato 1.6**. São rascunhos, sem aprovação, vigência ou candidato identificado.

**Etapa 1 da preparação para o teste:** concluídos os três ajustes documentais de alcance dos direitos, cache de clima após revogação e limpeza conjunta de downloads. Código conferido estaticamente; sem alteração funcional. Próxima etapa: evidências mínimas de privacidade, conforme [gaps.md](gaps.md). A etapa 5 do checklist geral ainda não está encerrada.

## Decisões confirmadas e limites

- Teste fechado: somente adultos no Brasil, compras com contas de licença/instrumentos de teste. Configuração efetiva e quantidade de contas ainda não comprovadas; não há início confirmado.
- Futuros efeitos e estilos disponibilizados no app estão incluídos no Premium, sem nova compra desses itens. Artes continuam separadas; sem promessa de calendário/quantidade ou conversão de compras simuladas em comerciais.
- Gabriel indicado para revisão/aprovação; indicação não é aprovação já realizada nem parecer profissional.
- Intenção de preservar o acervo; plano de exceção e continuidade ainda em estudo, necessário antes de cobrança real.
- Responsável/canal: Rafael Huppes e `suporteterrabr@gmail.com`. Não comprovadas designação formal de encarregado ou dispensa.

## Evidência e estado

- [x] Inventário preliminar de oito atividades: [data-map.md](data-map.md).
- [x] Hipóteses de bases legais organizadas: [legal-basis.md](legal-basis.md). Não aprovadas por inferência.
- [x] Relato técnico e direitos detalhados nos textos; identificação do responsável preservada.
- [x] Decisões remotas conciliadas e revisões distintas para evitar colisão de números.
- [ ] Configurações, contratos aplicáveis, retenção e acesso: [vendors.md](vendors.md).
- [ ] Avaliação de bases, governança e proteção de adolescentes para eventual ampliação; adultos no teste não comprovam dispensa da lei.
- [ ] Plano de continuidade; candidato/Console; aprovação/vigência; republicação e acesso público.

Ver [gaps.md](gaps.md) para distinguir redação corrigida de decisão confirmada ou evidência externa pendente. Fonte de decisões: [respostas de Rafael](../docs/dev/RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).

## Método e limites

Skills selecionadas do repositório solicitado, revisão fixada em `d85d79a`, aplicadas ao diagnóstico documental e à correção autorizada dos rascunhos. Referências e ressalvas em [fontes.md](fontes.md). Não foram criadas contas, backend, biometria ou infraestrutura por modelos das skills. Não houve delegação, instalação global, acesso a segredos, parecer jurídico, ROPA final, RIPD, aprovação/publicação ou envio de formulários.

A análise do código é estática: coordenadas enviadas à MET são arredondadas (não truncadas), a limpeza de prévias é uma política com limite nominal e pode ser postergada, e dependências fonte não comprovam todo o comportamento do candidato de release. Não usar “reflete exatamente” ou “conformidade integral” para esses limites.

## Registros operacionais (16/10/2026)

Rascunhos que acompanham as pendências D04–D13, sem declarar conformidade nem execução:

- [x] [ROPA](ropa.md) — v0.1, derivado de [data-map.md](data-map.md).
- [x] [Enquadramento e encarregado](governanca-encarregado.md) — opções e fontes (Res. 2/2022); decisão pendente.
- [x] [Avaliação de alto risco / RIPD](avaliacao-risco.md) — critérios organizados, conclusão não fechada; sem limiar numérico inventado.
- [x] [ECA Digital](adolescentes-eca-digital.md) — com o texto da lei conferido; aplicabilidade em aberto.
- [x] [Direitos do titular](direitos-operacao.md) e [retenção](retencao-operacao.md) — procedimentos propostos.
- [x] [Plano de incidente](incidentes.md) e [registro](incidentes-registro.md) — Res. 15/2024 conferida no DOU.
- [ ] Comprovar execução de cada procedimento, preencher responsáveis (fora do Git) e obter revisão jurídica. Ver D11–D13 em [gaps.md](gaps.md).
