# Etapa 2 — evidências mínimas de privacidade

Conferência em 15/10/2026. Escopo: teste fechado de adultos no Brasil, sem cobrança real. Fontes públicas consultadas e código local conferido; nenhuma conta externa acessada. O usuário confirmou que **somente Rafael tem acesso às contas**. Esta entrega conclui a preparação técnica/documental disponível, não o aceite administrativo ou jurídico da etapa.

## Matriz de evidências

| ID | Objeto | Evidência encontrada | Limite / próxima ação |
|---|---|---|---|
| E01 | Localização e MET | Manifesto fonte declara COARSE, INTERNET e NETWORK_STATE. `LocationHelper` oferece São Paulo; `WeatherRepository.coordenada` arredonda para duas casas antes da transmissão. Onboarding informa o destinatário e oferece alternativa. | Aceite em aparelho e manifesto mesclado continuam pendentes. Necessidade/base da operação não são comprovadas apenas pela permissão. |
| E02 | Logs MET | Termos oficiais identificam registro de IP/coordenadas e armazenamento dos logs de acesso em Oslo. A política geral trata também de registros de segurança. | Prazo específico dos logs de acesso à API não foi identificado nas páginas consultadas. Não aplicar prazos de outros serviços da MET, como visitantes físicos. |
| E03 | Direitos de compra | `BillingManager` verifica assinatura e usa token no acknowledge; `Plano`/`ComprasArte` persistem direitos locais. Não identificado servidor próprio de recibos. | Não equivale a ausência de pedidos no Console ou de dados tratados pelo SDK. Cadastro de licença ainda depende do Console. |
| E04 | Cloudflare/R2 | Código pede prévias e pacotes por HTTPS; a documentação de Data Access Logs cobre acesso público e campos como IP/caminho/User-Agent. O recurso documentado tem retenção de sete dias e ativação no bucket. | **Não comprovado que esteja habilitado no terra-acervo.** Esse prazo não se estende a Logpush, cópias exportadas, auditoria ou logs próprios da Cloudflare. Rafael deve conferir cada recurso existente. |
| E05 | Contrato Cloudflare | DPA público versão 6.4, efetivo em 03/04/2026, integra o acordo principal nas condições nele descritas; pode estar associado a acordo eletrônico. | Falta identificar os termos efetivamente aplicáveis à conta e o mecanismo de cada fluxo internacional. Não exigir assinatura individual por inferência nem declarar adesão só por existir a página. |
| E06 | Gmail | Ao excluir, a mensagem vai à Lixeira, onde pode permanecer por até 30 dias; existe exclusão definitiva pelo usuário. A política Google distingue exclusão no produto e remoção de seus sistemas/backups. | A regra do Terra de 60 dias exige rotina que inclua a Lixeira e cópias sob controle da equipe. Não prometer eliminação física dos sistemas Google no mesmo instante. |
| E07 | Cache HTTP de clima | `WeatherRepository.apiCompartilhada/criarApi` usa `cacheDir/clima-met`, OkHttp Cache de 512 KiB e regras HTTP. É separado de `atmosfera_weather_cache.xml`. | URLs de consultas incluem coordenadas; respostas e metadados podem persistir no cache de disco. Validade/Expires não é prazo de apagamento. Inventário e rascunho de Privacidade 1.14 atualizados; não houve aprovação ou republicação. |
| E08 | Logs de release | Interceptor HTTP meteorológico fica NONE em release; callbacks Billing registram códigos/mensagens do provedor. Nenhum novo destino remoto de telemetria identificado nesse código. | Não concluir que todos os SDKs/logs são anônimos ou livres de dados pessoais. Conferir candidato e práticas dos SDKs; não coletar logcat de testadores indiscriminadamente. |
| E09 | Backup | Regras XML excluem clima/freio, direitos e `filesDir/acervo`. Cache HTTP e prévias estão em cacheDir. | Backup/transferência por plataforma e cópias antigas precisam de teste; não alegar apagamento de backups antigos. |
| E10 | Site, suporte e participantes | Serviço, canal, responsáveis e propósito constam nos registros. Regra do suporte: 60 dias após encerramento. | Recursos do Sites, acessos Gmail/Console, lista, cópias e retenção administrativa não podem ser demonstrados por código. Conferência fica com Rafael. |

Caminhos Kotlin relativos a `android-app/app/src/main/java/com/terra/wallpaper/`; evidências da implementação em `weather/WeatherRepository.kt`, `LocationHelper.kt`, `billing/BillingManager.kt`, `Plano.kt`, `ComprasArte.kt`, `ui/OnboardingScreen.kt` e regras em `android-app/app/src/main/res/xml/`.

## Bases legais: preparação disponível

As hipóteses continuam em [legal-basis.md](legal-basis.md). Para preferências, validação/entrega e suporte solicitado, a hipótese de execução do serviço tem finalidade e campos identificados. Localização opcional exige avaliar a função solicitada e a alternativa sem localização; não foi adotada base definitiva pelo responsável. Logs para segurança, site e registros administrativos precisam de configuração/finalidade confirmadas antes de concluir o fundamento. Pedidos legais e defesa em processo são operações específicas, não justificativa para guardar todas as mensagens indefinidamente.

Critérios: LGPD arts. 6º–10, 15–19, 33–39 e 46. Não houve LIA concluída, consentimento registrado, ROPA final ou escolha de base pelo usuário nesta execução. Preparar proposta não comprova sua adoção.

## Entregas e aceite

- [x] Evidências de código e fontes públicas consolidadas.
- [x] Inventário atualizado com cache HTTP e distinção entre logs/cópias.
- [x] Rotina proposta de suporte/direitos e retenção: [rotinas-atendimento-retencao.md](rotinas-atendimento-retencao.md).
- [x] Roteiro para o único administrador disponível: [conferencia-administrativa.md](conferencia-administrativa.md).
- [ ] Confirmações de Rafael e avaliação das hipóteses com os dados operacionais reais.
- [ ] Ajuste final dos textos conforme as evidências, aprovação/vigência e candidato da próxima etapa.

## Fontes oficiais consultadas

- [MET — termos, Personal data](https://api.met.no/doc/TermsOfService) e [política geral](https://www.met.no/en/About-us/privacy).
- [Cloudflare — Data Access Logs](https://developers.cloudflare.com/r2/buckets/data-access-logs/) e [DPA](https://www.cloudflare.com/cloudflare-customer-dpa/).
- [Gmail — exclusão e Lixeira](https://support.google.com/mail/answer/7401?hl=pt-BR); [Google — retenção e exclusão](https://policies.google.com/technologies/retention?hl=pt-BR).
- [Play services — divulgação de dados](https://developers.google.com/android/guides/play-data-disclosure) e [Android — uso de dados/Billing](https://developer.android.com/privacy-and-security/declare-data-use).
- [LGPD](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm).

Fontes descrevem serviços em geral, não configurações comprovadas das contas do Terra. Não houve ativação de logs, alteração de retenção ou envio de mensagem.
