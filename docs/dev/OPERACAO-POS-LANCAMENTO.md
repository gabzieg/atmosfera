# Operação após publicar o Terra — primeiros 90 dias

**Preparado em 17/09/2026; execução começa na data real de publicação (D0).** Este roteiro já define responsáveis, indicadores, critérios de ação e próximas entregas. Não pressupõe app publicado nem dados de usuários disponíveis.

Responsável operacional inicial: Gabriel, conforme a organização documentada; pedidos financeiros e declarações ficam com o titular da conta. Confirmar o responsável pelos packs antes do lançamento. Manter um único registro de incidentes e de versões.

## D0 a D2 — Conferir o produto disponível ao público

- [ ] Instalar pela listagem pública em aparelho limpo e registrar versão recebida.
- [ ] Conferir política, suporte, preços, amostras, compra/restauração, download e aplicação.
- [ ] Verificar duas vezes ao dia: Android vitals, falhas relatadas, pedidos/reembolsos, disponibilidade e erros de clima/CDN. Dados do Console podem chegar com atraso e ser limitados por volume.
- [ ] Acompanhar e-mails e avaliações; nunca solicitar senha, cartão ou coordenada exata para diagnosticar.
- [ ] Registrar qualquer discrepância entre produto anunciado e entregue como prioridade de correção.

**Aceite:** nenhuma falha conhecida que impeça usar o gratuito ou receber conteúdo comprado; serviços acessíveis; responsável de suporte disponível.

## D3 a D7 — Estabilizar a versão 1.0.x

- [ ] Reproduzir os problemas mais frequentes por aparelho/API, incluindo bateria e wallpaper desaparecendo.
- [ ] Priorizar pagamento/conteúdo perdido, crash/ANR e consumo excessivo antes de novas cenas.
- [ ] Publicar correção com `versionCode` maior, validada no teste interno e na jornada afetada.
- [ ] Para atualizações, usar distribuição gradual quando disponível e observar antes de ampliar.
- [ ] Revisar dúvidas recorrentes e ajustar tutorial, ficha e respostas de suporte para corresponder ao comportamento real.

**Aceite:** incidentes críticos encerrados, problemas restantes com reprodução e prioridade definidas.

## D8 a D30 — Validar uso e sustentabilidade

| Indicador | Fonte inicial | Como interpretar / agir |
|---|---|---|
| Crashes e ANRs percebidos pelo usuário | Android vitals | Comparar versões/aparelhos e limites atuais mostrados pelo Console; qualquer regressão grave impede ampliar update. |
| Instalações, desinstalações e aquisição | Estatísticas Play Console | Ver tendência e países; não confundir instalação com uso efetivo de wallpaper. |
| Receita, pedidos, reembolsos por produto | Relatórios de monetização | Separar Premium e cenas, descontar taxas/impostos/custos antes de concluir lucratividade. |
| Erros e latência de entrega | Métricas agregadas do CDN/provedor | Investigar 404, falha de hash, indisponibilidade e crescimento de tráfego. |
| Problemas de bateria e aplicação | Testes controlados e suporte | Categorizar por fabricante, Android e cena; comparar antes/depois da correção. |
| Avaliações e tempo de resposta | Console + suporte | Agrupar causa, resolver e acompanhar recorrência; não comprar avaliações nem condicionar suporte à nota. |
| Custo por usuário / margem | Faturas + relatórios financeiros | Considerar clima, CDN, armazenamento, suporte e câmbio; compra única gera custo recorrente. |

O app não tem analytics próprio hoje. **Não é possível afirmar conversão por tela, taxa de aplicação de wallpaper, funil de download ou retenção D1/D7/D30 por evento** apenas com o código atual. Usar dados agregados disponíveis e testes qualitativos inicialmente. Se essa medição se tornar necessária, especificar eventos mínimos, retenção e privacidade antes de introduzir SDK ou backend.

- [ ] Realizar revisão semanal de métricas e custos, registrando decisões e denominadores usados nas taxas.
- [ ] Escolher até três melhorias com base em defeitos e dúvidas recorrentes.
- [ ] Validar se pessoas distinguem compra de cenário de Premium; corrigir comunicação antes de mudar preços.
- [ ] Fazer uma alteração por vez na ficha para avaliar resultado sem confundir causas.

## D31 a D60 — Evoluir com evidência

- [ ] Priorizar versão 1.1: seleção manual de cidade se a falta de permissão/fallback for dor recorrente; gestão de artes baixadas se armazenamento aparecer no suporte; perfis de desempenho se os testes indicarem necessidade.
- [ ] Ampliar catálogo apenas com conteúdo revisado, direitos definidos, miniaturas, preços e manifesto compatíveis.
- [ ] Testar recuperação de CDN e restauração de backup dos packs; confirmar que URLs antigas continuam atendendo versões em uso.
- [ ] Avaliar verificação de compras no servidor e confirmação mais robusta conforme receita/fraude observada.
- [ ] Reavaliar textos legais e Data Safety sempre que adicionar servidor, SDK ou tipo de dado.

Favoritos, busca ampliada, novos idiomas e packs são candidatos; não compromissos automáticos. Packs exigem regra para quem já comprou parte do conteúdo e para eventual sobreposição com Premium.

## D61 a D90 — Planejar expansão

- [ ] Comparar receita líquida com custo recorrente e esforço de suporte.
- [ ] Decidir países/idiomas seguintes a partir de demanda e capacidade de atendimento.
- [ ] Só aumentar divulgação paga após estabilidade técnica e entendimento do custo de aquisição e receita.
- [ ] Revisar calendário de Android/targetSdk/Billing, bibliotecas e atualizações de políticas.
- [ ] Definir prioridades do trimestre seguinte com evidência de uso; preservar o lançamento sem anúncios/assinatura, salvo decisão explícita posterior.

## Procedimento de incidente

| Gravidade | Exemplos | Resposta proposta |
|---|---|---|
| Crítica | Compra sem entrega para vários usuários, perda de direitos, crash generalizado, exposição de dados | Investigar imediatamente; interromper ampliação de update, avaliar suspensão de nova oferta/distribuição, preservar evidência mínima e preparar correção. |
| Alta | Uma família de aparelhos sem wallpaper, falha de download recorrente, consumo anormal | Reproduzir no mesmo dia útil, oferecer orientação segura e priorizar patch. |
| Normal | Erro visual isolado, dúvida, sugestão | Responder idealmente em até dois dias úteis e entrar no backlog. |

Prazos são metas internas propostas, não promessa jurídica a publicar automaticamente. Solicitações de direitos de dados seguem o procedimento e os prazos aplicáveis definidos na política aprovada.

**Recuperação de app:** parar distribuição gradual reduz novos usuários afetados; não restaura automaticamente a versão de quem já atualizou. Disponibilizar correção com `versionCode` superior. Despublicar também não remove o app já instalado.

**Recuperação de conteúdo:** manter packs imutáveis/versionados e manifesto anterior. Publicar manifesto só após upload e validação dos arquivos. O cache atual de `Acervo` dura seis horas e conteúdo instalado não atualiza por hash: fechar essa implementação antes de depender de rollback remoto.

**Reembolso:** conferir pedido na Play e direito associado; o titular pode processar reembolso no Console. Não pedir dados de cartão. [Procedimento Google](https://support.google.com/googleplay/android-developer/answer/2741495?hl=en).

## Modelos prontos para registro

### Incidente

```text
ID / abertura / responsável:
Versão e versionCode / aparelho / Android:
Impacto e quantidade estimada de afetados:
Cenário / arte / estilo (sem localização pessoal):
Passos e resultado esperado/observado:
Evidência com dados sensíveis removidos:
Contenção / correção / versão corrigida:
Verificação / encerramento / prevenção:
```

### Revisão semanal

```text
Semana / versões ativas:
Aquisição e desinstalações (fonte e período):
Crashes / ANRs / aparelhos afetados:
Pedidos / reembolsos / receita líquida estimada:
Custos de clima e CDN:
Três dúvidas ou defeitos mais recorrentes:
Decisões / responsável / prazo / evidência de conclusão:
```

### Checklist para cada atualização

- [ ] Problema e critério de aceite definidos; testes adequados e lint aprovados.
- [ ] Release minificado validado, versão incrementada e mapping/artefatos guardados.
- [ ] Compras, restauração, amostras, download e wallpaper atual preservados.
- [ ] Manifesto/packs compatíveis com versões ainda instaladas.
- [ ] Textos/ficha/Data Safety revisados se comportamento ou dados mudaram.
- [ ] Distribuição, acompanhamento e correção de emergência preparados.

## Fontes operacionais

- [Android vitals](https://developer.android.com/topic/performance/vitals): qualidade percebida pelo usuário.
- [Preparar e publicar releases](https://support.google.com/googleplay/android-developer/answer/9859348): primeira publicação e atualizações.
- [Segurança dos dados](https://support.google.com/googleplay/android-developer/answer/10787469): revisar declarações quando a arquitetura mudar.

As rotinas acima estão preparadas; nenhum monitoramento, e-mail, publicação ou transação foi executado em nome do titular nesta auditoria.
