# Preparação do teste fechado — conferência de 19/10/2026

## Base incorporada e escopo

PR #50 permanece em rascunho, com `b85ed0a` publicado e checks build/secret-scan aprovados. PR #52 contém `c746d19`, atribuído no Git a Gabriel, com oito registros operacionais novos de LGPD. O usuário pediu reaproveitar essa entrega; incorporada por cherry-pick como `1f21454`, preservando os documentos mais recentes. Esta revisão ajusta os registros; não é merge da PR no GitHub.

O usuário não tem acesso ao Console nem confirma se a versão contém AAB. Não foi localizado outro commit remoto posterior nas branches conferidas. Se Rafael tiver trabalho só local, ele ainda não integra esta base. Cinco arquivos não versionados de pesquisa/relatório de mercado foram preservados fora desta entrega.

## 1. Versão e AAB

| Item | Evidência local |
|---|---|
| Branch de trabalho | `fix/premium-privacidade-etapas-1-2` |
| Código do app na base publicada | `b85ed0a`; importação documental `1f21454` sem mudança funcional |
| Fonte atual | `com.terra.wallpaper`, `versionName 1.0.5`, `versionCode 6`, minSdk 26, targetSdk 36 |
| AAB encontrado | `android-app/app/build/outputs/bundle/release/app-release.aab`; arquivo antigo, modificado em 06/10 às 21:48 (horário local) |
| SHA-256 do arquivo | `19b88b3d8d4d61906038299d2e001cdc9212eebcc415758c5382e4e93353e6bb` |
| Tamanho | 86.390.764 bytes |
| Manifesto do AAB | Mesmo pacote, nome/código 1.0.5/6 e SDK 26/36 |
| Módulos / ABIs | `base`; arm64-v8a, armeabi-v7a, x86 e x86_64; sem uses-feature explícito no manifesto inspecionado |
| Documentos embutidos | Três HTMLs diferentes das fontes atuais 1.14/1.8/1.6; pacote não atende à entrega atual |
| Certificado público do AAB | SHA-256 `E0:18:F8:A8:DB:9B:8B:D3:C2:07:EA:A4:47:A5:3B:D5:02:F5:F6:0A:D7:22:49:48:C5:32:BA:B0:22:4D:E3:16` |

O certificado acima foi lido do AAB, sem acessar chaves ou senhas. `jarsigner` informou verificação do JAR, com avisos de cadeia autoassinada, ausência de timestamp e leitura inconsistente de entradas JarFile/JarInputStream. Não foi tratado como certificação de aceitação na Play ou como diagnóstico da causa dos erros. Comparar o **certificado de upload** com o Console; ele não precisa ser igual ao certificado com que a Play assina os APKs distribuídos quando Play App Signing está ativo.

**Resultado:** AAB antigo reprovado como candidato desta entrega. Não foi gerado substituto, alterado versionCode ou associado hash de commit ao binário antigo. Data, nome e versão iguais não provam procedência do build. A leitura do binário não revela qual AAB foi efetivamente incluído no rascunho do Console.

### Conferência necessária no Console — Rafael

1. Abrir o app e confirmar nome do pacote `com.terra.wallpaper`.
2. No Explorador de pacotes/versões, registrar maior versionCode em todas as faixas, códigos ativos/retidos e processamento do pacote pretendido. Enviar os números, sem credenciais.
3. Em Teste fechado → Gerenciar faixa → Editar versão, conferir os pacotes **incluídos**. Um upload na biblioteca não comprova inclusão na versão. Se o pacote correto já existe e está processado, verificar a opção de adicionar da biblioteca; não reenviar o mesmo código indiscriminadamente.
4. Se a versão não muda nenhum pacote em relação à composição anterior, registrar a situação antes de gerar outra. Não apagar faixas ou rascunhos para tentar resolver sem entender a composição.
5. Para os usuários elegíveis à atualização, conferir código maior do candidato, identidade e assinatura aplicáveis, SDK/ABI/features e catálogo/exclusões de dispositivos. Não alterar compatibilidade ou chave para contornar o erro.
6. Após composição correta, verificar mensagens completas de erro e diferença de dispositivos suportados; a hipótese de rascunho vazio ainda não foi comprovada. Se houver processamento preso, usar o suporte do Console com os dados do caso.

Fontes: [preparar versões e incluir pacotes](https://support.google.com/googleplay/android-developer/answer/9859348?hl=pt-BR), [versionamento Android](https://developer.android.com/studio/publish/versioning?hl=pt-br). O `versionCode` seguinte não foi deduzido a partir do código 6 do repositório.

### Antes do novo build

- Confirmar maior código do Console, escolher revisão aprovada e atualizar fonte de versão de forma rastreável.
- Conferir endpoint HTTPS de acervo no release: a fonte requer `terraAcervoUrl` explícito; o fallback R2 é de debug. Não concluir que release tem endpoint apenas porque o APK debug funciona.
- Conferir compras/assinatura sem publicar credenciais; preservar configuração R8 versionada e documentos aprovados correspondentes.
- Gerar um único candidato, registrar branch/commit limpo, nome/código, hash, certificado e mapping. Rodar build/checks pertinentes e testar atualização pela Play.

Auditor local reproduzível (Python 3.11+, Java e [bundletool oficial](https://github.com/google/bundletool/releases/tag/1.18.3)):

```powershell
python scripts/auditar_aab_play.py caminho/candidato.aab --bundletool caminho/bundletool-all-1.18.3.jar --maior-version-code NUMERO_CONFERIDO --saida android-app/app/build/auditoria-candidato.json
```

`NUMERO_CONFERIDO` é um marcador, não comando pronto. Use `--java` se Java não estiver no PATH. O auditor não acessa a rede/Console nem lê keystore. Código de saída 1 indica divergência/falha; 2 indica ausência do maior código com demais checks locais passando; 0 não é aprovação para envio. Bloco de assinatura presente não valida certificado/assinatura, e documentos iguais não comprovam todo o código do app.

## 2. Segurança dos dados e documentos

Revisão específica em [matriz de declarações](../../.lgpd/seguranca-dados-play-2026-10-19.md). Fonte atual: Privacidade 1.14, Termos 1.8, Contato 1.6, ainda rascunhos. Nove HTMLs/manifesto devem continuar sincronizados, e o AAB final precisa conter as mesmas versões. Nenhuma página do Sites ou resposta do formulário foi alterada nesta execução.

## 3. ECA Digital e sinais de idade

[Registro atualizado](../../.lgpd/adolescentes-eca-digital.md): acesso adolescente provável e classificação 14 relatada não fecham enquadramento. API Play Age Signals existe; integração ainda não implementada nem declarada desnecessária. Avaliar aplicabilidade, disponibilidade e tratamento de sinal desconhecido, sem acrescentar coleta de identidade por presunção.

## 4. Participantes e prazo

Rafael informou **sete contas selecionadas na lista de licenças**. Registro administrativo não auditado, distinto de adesões contínuas na faixa. Checkout com instrumento de teste, adultos/Brasil e quantidade de aderentes ainda precisam ser confirmados. Não existe data inicial comprovada.

Quando a exigência de nova conta pessoal for aplicável, [Google exige pelo menos 12 participantes inscritos continuamente por 14 dias](https://support.google.com/googleplay/android-developer/answer/14151465?hl=pt-BR). Se essas sete contas vierem a ser sete participantes válidos, ainda faltarão pelo menos cinco; não concluir essa conta antes de conferir as adesões. Teste de licença não inicia os 14 dias, e completar o prazo não garante aprovação de produção.

## Mensagem objetiva para o responsável pelo Console

Confirmar: maior versionCode de todas as faixas; código/estado do AAB incluído no rascunho fechado; certificado de upload e comparação de dispositivos suportados. Informar separadamente quantidade na faixa, quantidade nas licenças e checkout de teste observado. O arquivo local de 06/10, código 6, está desatualizado; não serve como candidato das correções atuais. Sem esses dados não foi escolhido novo código nem gerado AAB para envio.
