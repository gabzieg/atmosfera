# Preparação do teste fechado — conferência de 19/10/2026

## Atualização em 22/10/2026 — candidato 7 identificado

Supera a seção 1 abaixo quanto ao candidato: o AAB de 06/10 (código 6) continua reprovado, e o substituto foi gerado em 20/10 no Windows, em checkout isolado e limpo, a partir do commit abaixo, mesclado à `main` pelo PR #53 (`7463558`). `android-app/` e `docs/legal/` da `main` nesse merge são idênticos aos do commit do candidato.

| Item | Registro |
|---|---|
| Commit | `c1848a997c9d0bf49b4829659886b6294c4574df` (branch de origem `gabzieg/rafael-commit-test-project`) |
| Identidade | `com.terra.wallpaper`, `versionName 1.0.5`, `versionCode 7`, minSdk 26, targetSdk 36 |
| AAB | SHA-256 `0ed276df2a2d0d6f3610849a685b34d071a7cd5b4b4ec6f79520a40e50345280`, 94.604.586 bytes; `bundletool validate` 1.18.3 exit 0 |
| Certificado de upload | SHA-256 `E0:18:F8:A8:DB:9B:8B:D3:C2:07:EA:A4:47:A5:3B:D5:02:F5:F6:0A:D7:22:49:48:C5:32:BA:B0:22:4D:E3:16`, igual ao do AAB 6 e ao da configuração local |
| Assinatura | 1461 entradas de conteúdo cobertas, 0 sem assinatura. `jarsigner -strict` retorna 4 por certificado autoassinado/cadeia PKIX, sem timestamp e manifesto no fim do ZIP; examinado, não é defeito do pacote |
| Mapping R8 | 9.1.29, SHA-256 `847cf29437aefdba5cde94948365df43f8623b4cd656dfb4c7b655ed8387c5d6`, idêntico ao embutido no AAB |
| Acervo | `-PterraAcervoUrl=https://pub-722473b758b144279fc34ecb931a6045.r2.dev/` (bucket de testes; não é endpoint de produção) |
| Documentos embutidos | Privacidade 1.14, Termos 1.8, Contato 1.6, com `status: rascunho_para_revisao`, `aprovado_para_publicacao: false` e `vigencia: null`. Aprovar e publicar no Sites não altera essa cópia |
| Gates da geração | 98 testes sem falha, lint sem erros (38 avisos), builds debug/release e `bundleRelease`; JDK Temurin 17.0.20+8, Gradle 8.14.5 |

Artefatos e relatório ficam fora do Git, em `output/terra-release-c1848a9-v7/`. O AAB não embute o hash do commit (`NO_SUPPORTED_VCS_FOUND`); o vínculo é documental.

### Conferência direta no Console — 22/10/2026

Leitura feita no navegador do Gabriel, cuja conta Google abre a conta de desenvolvedor *Rafael Huppes* (tipo **pessoal**). Nada foi salvo, enviado ou alterado.

| Item | Observado |
|---|---|
| Causa dos dois erros | O rascunho do **Teste fechado – Alpha** ("Versão sem título") **não tem nenhum pacote** nem nome de versão. Uma versão vazia não adiciona pacote e não oferece atualização a quem já instalou, o que bate com as duas mensagens |
| Teste interno | Código **7 (1.0.5)**, "Disponível para testadores internos", lançamento completo em 20/10 |
| Biblioteca | Códigos 7, 6, 5, 3 e 1; **maior código: 7**, então o próximo build é 8 |
| Certificado de upload | SHA-256 do Console **igual** ao do AAB 7 (`E0:18:F8:…:4D:E3:16`). Play App Signing ativo; o certificado de distribuição é outro, como esperado |
| Países do fechado | **Somente Brasil** (1 de 178) |
| Testadores do fechado | Lista "Testadores internos Terra", 7 contas; e-mail/URL de feedback vazio; 0 participantes no painel |
| Teste de licença | Mesma lista selecionada e salva, `RESPOND_NORMALLY` |
| Conteúdo do app | 9 declarações concluídas (Segurança dos dados em 18/10; demais em 17/10). **Pendente: ID de publicidade**, e o Console avisa que sem ela não aceita versões para Android 13+ |
| Política de Privacidade | URL salva ainda com `?authuser=1` |
| Público-alvo | 13–15, 16–17 e 18+ |
| Classificação | 14 no Brasil |
| Produtos | Só `terra_premium` (1 opção de compra) |

**Continua pendente:** incluir o código 7 da biblioteca no rascunho do fechado, salvar e reconferir a validação; concluir a declaração de ID de publicidade; corrigir a URL da política. O hash do AAB enviado não aparece no Console; a correspondência com `0ed276df…` continua documental, embora o certificado de upload coincida. Sequência em [A1–A8 do checklist](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md#a--antes-de-iniciar-o-teste-fechado).

**Situação posterior:** o “Continua pendente” acima é histórico: inclusão/salvamento/validação superados em [28/10](#composição-do-teste-fechado--28102026); AD_ID e URL já regularizados conforme [conferência de 27/10](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#11-atualização-em-27102026--conferência-e-salvamento-no-console). O vínculo do hash continua documental; envio e aprovações permanecem pendentes.

### Composição do teste fechado — 28/10/2026

**Fonte e execução:** registro privado `.context/console-a1-a2-2026-10-28.md`, seções 1–4 e limites, sintetizado sem IDs, URLs privadas ou e-mails. O `niemeyer` (Claude Code) operou via `playwright-cli attach --extension`, no Brave do Gabriel, pela extensão Playwright MCP Bridge, apenas na aba do Console, cerca de **07:56–08:05 (Brasília)**. Gabriel autorizou incluir o 7 no rascunho fechado, conferir/corrigir país só Brasil, salvar e validar, **sem enviar**; testadores ficaram fora da autorização. O dev apenas registra essa evidência, sem abrir ou operar o Console.

| Momento | Observado / executado |
|---|---|
| Antes | Painel: 3 de 5 tarefas do fechado; faltavam visualizar/confirmar e enviar. Faixa “Teste fechado - Alpha”: inativa, versão de rascunho, 1 país/região; “Versão sem título”, sem pacote, nome vazio e notas em 0 idioma. Brasil já segmentado, nada a corrigir. Lista “Testadores internos Terra” já marcada, 7 contas; grupos e feedback vazios; nada alterado nos testadores. Painel: **0 participantes**. |
| Composição e salvamento | Biblioteca com códigos 7, 6, 5, 3 e 1; **somente o 7 adicionado**, sem novo upload. Pacote “7 (1.0.5)”, API 26+, SDK alvo 36, 4 layouts, 4 ABIs, 3 recursos obrigatórios. Nome automático “7 (1.0.5)”; notas não preenchidas. “Salvar como rascunho” confirmou alterações salvas; “Avançar” abriu “Visualizar e confirmar”. |
| Validação | **0 erros, 1 aviso** para o código 7: “Este App Bundle contém código nativo, e você não fez upload dos símbolos de depuração. Recomendamos que você faça upload de um arquivo de símbolo para facilitar a análise e a depuração de falhas e ANRs.” Novas instalações: 89.8 MB, download estimado em 52s. |
| Confirmação | “Salvar” confirmou “A alteração foi salva” e ofereceu acesso à Visão geral da publicação para enviar; escolhido **“Agora não”**. Faixa passou a “Ativo · Última versão: 7 (1.0.5) · 1 país / região”; versão com 1 código e **“Ainda não enviado para análise”**. “Ativo” nesse painel não comprova distribuição do fechado. |
| Publicação | “Publicação gerenciada desativada”; mudanças ainda não enviadas, botão **“Enviar 13 mudanças para revisão” habilitado, não clicado**. Supera o bloqueio do botão pelas etapas do painel registrado em 27/10; mantém os bloqueios de aceite/autorização A3–A7. |
| Verificações rápidas | Apareceram ao abrir a Visão geral após salvar, sem clique em enviar: até 14 minutos, ainda em execução às 08:02 (até 12 minutos). Às 08:04, após recarregar: “As mudanças podem ser enviadas para revisão. É possível que outros problemas sejam encontrados durante a análise do app.” Nenhum problema listado; botão habilitado e não clicado. Checagem prévia não equivale à revisão do Google. |
| Envios | Atividades de envio sem envios recentes, reconferidas às 08:04: **nada enviado**. Rascunho salvo não inicia o teste fechado nem a contagem de B1; não há aprovação ou distribuição do fechado comprovada. |

**O envio das 13 mudanças submeteria:**

- Teste fechado - Alpha: 7 (1.0.5), “Iniciar o lançamento completo”; adicionar Brasil; remover sincronização de países com produção; retomar faixa; definir testadores pela lista “Testadores internos Terra”.
- Página padrão pt-BR: idioma adicionado, nome “Terra - Live Wallpaper” e demais informações necessárias fornecidas.
- Conteúdo do app: novo questionário de classificação, público-alvo **13 ou mais**, Política de Privacidade (`/privacidade`), declaração de anúncios, Segurança dos dados e Apps de saúde.
- Configurações da loja: categoria App de Personalização. O bloco “O que você nos informou”, não publicado, também mostra instruções de login (todos ou alguns recursos restritos), ID de publicidade, Apps governamentais e recursos financeiros; não é uma contagem adicional às 13 mudanças.

**Não enviar sem nova autorização e fechamento de A3–A7.** Com publicação gerenciada desativada, a aprovação publica logo em seguida para a lista de testadores. Público 13+ versus teste só com adultos continua decisão pendente; Segurança dos dados e textos legais ainda sem aprovação. Botão habilitado não libera envio, aprovação ou distribuição.

**Testadores e limites:** a lista de 7 contas já está na faixa e é a mesma lista selecionada para licença em 22/10; não se reconferiu em 28/10 se as contas ainda coincidem nas duas configurações (A6). Com 0 participantes no painel, B1 continua aberto: exige ao menos **12 adesões contínuas por 14 dias**; **faltam no mínimo 5 contas além das 7**, todas com licença confirmada conforme A6, e as contas listadas ainda precisam aderir. Não houve cadastro, convite ou mudança de testadores. Conteúdo das instruções de login não foi aberto. A frase automática “As mudanças são enviadas assim que as verificações são concluídas” não comprovou envio: a ausência foi conferida em Atividades de envio, sem consulta a documentação oficial nesta sessão. Hash do AAB no Console e arquivamento completo de A2 não foram reconferidos; nenhum novo AAB, nota ou símbolo de depuração foi produzido.

## Base incorporada e escopo

PR #50 permanece em rascunho, com `b85ed0a` publicado e checks build/secret-scan aprovados. PR #52 contém `c746d19`, atribuído no Git a Gabriel, com oito registros operacionais novos de LGPD. O usuário pediu reaproveitar essa entrega; incorporada por cherry-pick como `1f21454`, preservando os documentos mais recentes. Esta revisão ajusta os registros; não é merge da PR no GitHub.

O usuário não tem acesso ao Console nem confirma se a versão contém AAB *(registro de 19/10 superado em 22/10: o Console da conta pessoal de Rafael Huppes também foi lido diretamente pelo Gabriel e o rascunho do fechado estava sem pacote — ver [conferência direta acima](#conferência-direta-no-console--22102026) e [SPEC.md](SPEC.md#publicação-na-play-store--quem-faz-o-quê) → "Acesso às contas")*. Não foi localizado outro commit remoto posterior nas branches conferidas. Se Rafael tiver trabalho só local, ele ainda não integra esta base. Cinco arquivos não versionados de pesquisa/relatório de mercado foram preservados fora desta entrega.

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
6. Após composição correta, verificar mensagens completas de erro e diferença de dispositivos suportados; a hipótese de rascunho vazio ainda não foi comprovada *(registro de 19/10 superado pela [conferência direta de 22/10](#conferência-direta-no-console--22102026), que confirmou o rascunho do fechado sem pacote; a pendência posterior de incluir o código 7 e reconferir a validação foi superada em [28/10](#composição-do-teste-fechado--28102026), com 0 erros)*. Se houver processamento preso, usar o suporte do Console com os dados do caso.

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
