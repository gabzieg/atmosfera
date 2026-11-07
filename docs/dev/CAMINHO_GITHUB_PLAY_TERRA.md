# Como esta atualização chega ao aplicativo

Estado verificado em 04/10/2026, tomando como base a main d3b0cc2 (PR #41).

## Três publicações diferentes

1. **R2:** armazena os arquivos do acervo. O catálogo de testes já foi enviado: 74 cenas, 389 artes, 1242 arquivos referenciados, 440.656.483 bytes. Os nomes contêm hashes para evitar que uma arte nova seja confundida com uma versão antiga em cache.
2. **GitHub:** guarda código, documentação e ferramentas. Esta atualização registra as informações técnicas verificadas e a ferramenta de upload reutilizável. Não altera o aplicativo Android. As alterações antigas da pasta local foram preservadas e não foram transportadas automaticamente sobre a main recente.
3. **Play Console:** recebe um novo Android App Bundle assinado (.aab). Uma branch ou PR no GitHub não atualiza o app instalado. O upload de imagens para o R2 também não atualiza sozinho o catálogo e os produtos da loja.

## Antes de enviar um novo AAB

- Concluir a integração de download na versão atual: engine/Acervo.kt ainda tem BASE_PADRAO vazio, e o acesso ao acervo remoto está nas ferramentas de debug. Validar o caminho completo de compra, download, cancelamento, integridade e aplicação no motor.
- Usar com.terra.wallpaper e terra_premium. Não empacotar automaticamente a cópia local antiga com com.atmosfera.wallpaper.
- Resolver os textos/links legais e testar o cliente Android contra o bucket público. A verificação HTTP identificada funcionou, mas o cliente Python padrão recebeu 403; isso não comprova falha nem sucesso no Android.
- Gerar a versão na máquina que possui a chave de upload e a configuração privada de assinatura. Esta máquina não possui keystore.properties nem keystore encontrado no workspace. CLAUDE.md registra a chave na máquina do responsável pelo build. Não criar uma nova chave para substituir a identidade do app sem necessidade; não enviar chave privada ou senhas por Git/chat.
- A versão interna verificada é 1.0.4, código 5. O próximo upload exige código de versão disponível e maior que os já usados; conferir o Console imediatamente antes do build.
- Rodar os checks do projeto e os testes reais necessários. Então gerar bundleRelease assinado e verificar pacote, assinatura, versão e configuração de compra.

## Envio pelo Console

Destino inicial sugerido: **Teste interno**. Testar e lançar → Teste → Teste interno → Criar nova versão → enviar AAB → informar notas → revisar os avisos → salvar/publicar na faixa de testes. Os nomes dos controles podem variar; conferir o estado real antes de enviar.

Esta preparação não enviou um AAB novo. Para fornecer o artefato: **qual é o caminho ou link do AAB assinado, gerado com as correções atuais, e qual o versionCode?** Alternativamente, o responsável pelo build pode gerar na máquina que já possui a assinatura. A assinatura não é requisito para publicar apenas documentos no GitHub.

O teste fechado está separado do interno. O Console verificado exige concluir a configuração do app, reunir pelo menos 12 participantes por 14 dias contínuos e solicitar acesso de produção. Não há autorização de produção obtida nesta revisão.

## Acervo de testes e produção

URL de testes: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/

Manifesto: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/manifest.json

A Cloudflare destina r2.dev a desenvolvimento e aplica limites próprios. Para o lançamento público, preparar domínio personalizado ou uma solução de entrega de produção adequada. A compra controla o uso no app, mas os arquivos públicos podem ser copiados fora dele.
