# Conteúdo do site oficial — Terra

Referência de publicação originada em04/10/2026, reconciliada em09/11/2026 na base `eb245b4`. Não é uma segunda fila: andamento e checkpoints estão em [TASKS](TASKS.md). Responsável/acesso de hospedagem dependem do registro vigente. A existência de HTML no GitHub não demonstra que um site esteja acessível.

## Páginas e informações

1. **Política de Privacidade**: página HTML pública e estável, sem login, sem restrição geográfica e sem PDF. Link no Play Console e política acessível também no app. Deve identificar app/controlador e contato; explicar localização, coordenadas em cache, fluxo real em background, MET Norway, Cloudflare e Gmail; finalidades, compartilhamento, segurança, retenção, exclusão e direitos. Regra operacional relatada para suporte: até 60 dias após a resolução, com exceções legais e defesa.
2. **Termos de Uso**: identificação do fornecedor, licença das artes, separação entre Premium/efeitos e cenários, compras únicas, preço/composição conforme a oferta, direitos futuros, disponibilidade/download/reinstalação, consumo de dados, suporte e reembolso sem afastar direitos do consumidor. Descrever limitações reais sem isenção absoluta de responsabilidade.
3. **Contato/suporte e privacidade**: usar os canais já definidos em `docs/legal/CONTATO.md`, sem inventar contato pendente. Informar como encaminhar pedido, sem exigir dados excessivos. Publicar identificação e endereço do fornecedor conforme exigências aplicáveis à oferta comercial.
4. **Página inicial/apresentação**, recomendada: nome Terra - Live Wallpaper, função, compatibilidade Android, público adulto no teste atual (futuro13+ bloqueado), link real para a Play quando disponível, explicação curta de compras e links visíveis para as três páginas acima. Não prometer cenas, packs, atualizações ou países que não estejam disponíveis.
5. **Créditos/atribuições**: indicar MET Norway como fonte dos dados, link da licença CC BY 4.0 e transformações realizadas quando aplicável; pode estar nos Termos/Privacidade ou seção de créditos, sem exigir outra página. Créditos no site não substituem atribuição acessível no app. Licenças das artes/sprites devem corresponder à origem real.

Se o site apresentar preços/ofertas, a identificação comercial, condições completas, custos adicionais, disponibilidade e restrições devem estar claras, mesmo que a conclusão da compra aconteça no Google Play. O CPF existente no perfil de pagamentos não é automaticamente divulgado no site: completar a identificação na revisão jurídica, evitando publicar documento de identidade ou detalhes bancários.

Sem criação de conta no Terra, não é necessário inventar uma página de exclusão de conta; explicar limpeza dos dados locais e canal de pedidos de privacidade. Se houver criação de conta futuramente, rever essa conclusão. Um site estático sem analytics/cookies não precisa ganhar um banner de cookies decorativo; avaliar de novo se forem adicionadas ferramentas de rastreamento ou formulários.

## Publicação e consistência

- Remover placeholders, preencher contatos reais, data de revisão/vigência e URLs; vigência/publicação dependem de decisão explícita; a aprovação da redação não autoriza promovê-las.
- Sincronizar texto canônico, HTML web e cópia offline do app correto. Fontes atuais em `docs/legal/`: Privacidade1.22/Termos1.12/Contato1.10, redação aceita com metadados ainda de rascunho. Não publicar versões históricas sobre elas; gerar cópias somente pelo sincronizador do projeto quando autorizado.
- Confirmar cada URL em sessão sem autenticação; rotas indicadas até agora são `/privacidade/`, `/termos/`, `/contato/` no host histórico terra-livewallpaper.pages.dev, que não resolveu na consulta de outubro. Não foi consultado nesta tarefa, nem é disponibilidade atual comprovada.
- Territórios e oferta comercial exigem conferência própria, sem transportar permissões de fotos/relatos antigos; preservar as decisões vigentes R02/A4/R03.
- O plano13+ histórico não está autorizado para a rodada adulta atual; IARC depende do conteúdo real. Aprovação de compras pelo Family Link depende da configuração familiar e não é garantia universal de autorização de um responsável. Conferir conteúdo/marketing antes de declarar que o app não atrai crianças.

## Fontes oficiais

- Google Play, política de dados/privacidade/exclusão de contas: https://support.google.com/googleplay/android-developer/answer/10144311?hl=pt-BR
- Decreto 7.962/2013, identificação e informações da oferta no comércio eletrônico: https://www.planalto.gov.br/ccivil_03/_ato2011-2014/2013/decreto/d7962.htm
- MET, licença e atribuição: https://api.met.no/doc/License
- Cloudflare, r2.dev destinado a desenvolvimento: https://developers.cloudflare.com/r2/buckets/public-buckets/
