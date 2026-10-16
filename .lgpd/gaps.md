# Lacunas documentais e fechamento — Terra

**Etapa 2 — evidências preparadas em 15/10:** [matriz E01–E10](evidencias-privacidade-etapa2.md), [rotina proposta](rotinas-atendimento-retencao.md) e [roteiro para Rafael](conferencia-administrativa.md). O usuário confirmou que só Rafael acessa as contas. D02/D03/D05/D06 continuam com avaliação/confirmações pendentes; não marcar preparação como execução. Privacidade atual 1.14 inclui cache HTTP do clima; Termos 1.8 e Contato 1.6 mantidos. Os registros anteriores abaixo descrevem a etapa 1.

Conciliação em 15/10/2026 sobre `0d36b4a`, preservado o refinamento local. Fontes: **Privacidade 1.13, Termos 1.8 e Contato 1.6**. Redação corrigida, decisão confirmada e evidência de execução são estados diferentes. Nenhum item abaixo declara conformidade integral.

## Etapa 1 da preparação para o teste — ajustes documentais concluídos

- [x] Pedidos de direitos abrangem dados identificáveis sob controle do Terra, incluindo participantes e registros administrativos, sem depender de chamado anterior.
- [x] Revogação da localização impede novas leituras, mas não apaga o clima salvo; o serviço pode reutilizar cache e preservar o último clima quando a atualização falhar.
- [x] Limpeza descrita como remoção conjunta dos downloads; exige arte nativa ativa e ausência de download em andamento. Outra arte remota instalada não libera a ação.

Evidência: `service/AtmosferaWallpaperService.kt:carregarClima`, `ui/MainViewModel.kt:removerDownloads`, `engine/Acervo.kt:apagarCena` e inventário A05/A07. Caminhos Kotlin relativos a `android-app/app/src/main/java/com/terra/wallpaper/`. Não houve mudança de código nem aceite de teste em aparelho. Próxima etapa: evidências mínimas de privacidade (bases, fornecedores, retenção/acesso e rotina dos registros de participantes/suporte), ainda pendentes abaixo.

## Elementos do art. 9º da LGPD

| Elemento | Situação da documentação |
|---|---|
| Finalidades | Descritas, incluindo campos automáticos do relato. Bases ainda são propostas. |
| Forma/duração | Local e suporte descritos; logs externos e execução da retenção ainda a comprovar. |
| Identificação/contato | Rafael e canal confirmados; isso não comprova designação/dispensa de encarregado. |
| Compartilhamento/finalidade | Serviços identificados; papéis, instrumentos e fluxos externos ainda a validar. |
| Responsabilidades | Limites explicitados, sem atribuição única por fornecedor. Falta confirmação administrativa. |
| Direitos | Redação detalhada; triagem, atendimento e execução ainda precisam corresponder ao texto. |

## Acompanhamento D01–D10

| ID | Estado | O que foi feito e o que ainda falta |
|---|---|---|
| D01 — relato | Redação corrigida | Campos de `enviarRelato`, revisão e envio pelo cliente externo explicitados. Aceite de interface continua no roteiro do aparelho. |
| D02 — bases legais | Proposta preparada; validação pendente | Tabela por operação mantida como hipótese, alinhada a `legal-basis.md`. Retirada obrigação genérica de guardar transações sem norma identificada. Falta avaliar/adotar a base de cada operação; permissão Android não resolve isso. |
| D03 — fornecedores | Parcial; evidência externa pendente | Noruega coberta pela Resolução 32/2026 registrada. Não se presumem papéis, adesão ao DPA, destinos ou controle de logs Google/Cloudflare. Seguir `vendors.md`; ausência de prova não significa ausência de contrato. |
| D04 — público | Decisão de teste confirmada; avaliação posterior pendente | Adultos/Brasil no teste conforme Rafael; 13+ só como objetivo posterior. Não afirmar que ausência de chat ou controles Google comprovam atendimento integral ao ECA Digital. Recrutamento e configuração precisam corresponder à decisão; avaliar acesso provável e medidas aplicáveis. |
| D05 — governança | Responsável/canal confirmados; enquadramento pendente | Não chamar contato de designação formal nem inferir dispensa por pessoa física. Avaliar porte/risco e documentar enquadramento ou designação. |
| D06 — retenção/suporte | Regra confirmada; execução pendente | Mantidos 60 dias após encerramento e exceções justificadas. Falta rotina, acesso, descarte e tratamento de solicitações; não marcar como executado apenas pela redação. |
| D07 — sincronização/publicação | Geração local preparada; aceite externo pendente | Nove HTMLs e manifesto das versões 1.13/1.8/1.6 preparados para conferência. Google Sites não atualizado automaticamente. Aprovação exige registro real, versão e vigência, não apenas preencher JSON ao gerar AAB. |
| D08 — direitos | Redação detalhada; operação pendente | Incluídos canal, direitos, oposição e prazos; alcance inclui participantes e registros administrativos sob controle do Terra, sem exigir suporte anterior. Falta comprovar triagem e resposta. |
| D09 — continuidade | Intenção confirmada; plano pendente | Preservado funcionamento offline condicionado à instalação/direito. Retiradas promessas não aprovadas de manter servidor enquanto houver distribuição ou aviso prévio garantido. Plano de exceção antes de cobrança real. |
| D10 — candidato | Evidência estática parcial | Código-fonte, dependências e XML conferidos; ainda falta candidato identificado, manifesto mesclado e teste de release/Play. Código atual 6/1.0.5 não prova código disponível no Console. |

Normas e limites de interpretação em [fontes.md](fontes.md); propostas em [legal-basis.md](legal-basis.md). A identificação de pendência não é declaração de infração.

## Decisões já fechadas: não perguntar novamente

Responsável/canal, suporte por 60 dias, Premium separado das artes, cinco amostras, compras individuais, sem packs, novas artes separadas, teste sem cobrança/adultos/Brasil e inclusão dos futuros efeitos/estilos no Premium. Não há promessa de frequência de novos itens ou conversão automática de pedidos de teste em compra comercial.

## Próximas evidências para aprovação

1. Confirmar configurações de fornecedores, logs/acesso/retenção e rotina do suporte/testadores, sem publicar dados pessoais.
2. Avaliar bases e governança e registrar conclusão; avaliar público/medidas antes de ampliar, sem presumir dispensa só por recrutar adultos.
3. Identificar candidato e código disponível no Console, confirmar licenças dos participantes e Brasil/adultos na operação do teste.
4. Registrar aprovação real do texto aplicável e vigência; preencher também `aprovacao_registro` em `revisao.json`, sincronizar e republicar no Sites.
5. Conferir páginas sem login e correspondência com o artefato. Geração de HTML não comprova publicação nem inicia o teste.
