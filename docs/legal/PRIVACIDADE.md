# Política de Privacidade — Terra - Live Wallpaper

> **Texto canônico.** Esta é a fonte da verdade da política; a página publicada
> ([`docs/privacidade/index.html`](docs/privacidade/index.html)) é um espelho
> deste arquivo. Ao mudar um, mude o outro no mesmo commit.
>
> **Antes de publicar, preencha os `[PREENCHER: …]`** (controlador, e-mail de
> contato, URL e data de vigência) — a lista completa está em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md#política-de-privacidade).
>
> **Se o app mudar, esta política muda.** Qualquer alteração em `weather/`,
> `billing/`, no `AndroidManifest.xml` ou a entrada de um SDK de anúncios/
> analytics invalida o texto abaixo. Ver seção 13.

**Versão da política:** 1.5
**Aplica-se a:** Terra - Live Wallpaper (Android, `com.terra.wallpaper`), a partir da versão 1.0.5
**Última atualização:** 10 de outubro de 2026
**Vigente desde:** [PREENCHER: data da primeira publicação na Google Play]

---

## Resumo

Este resumo é uma cortesia e não substitui o texto completo abaixo.

- O Terra usa a **localização aproximada** do seu aparelho para descobrir o
  clima da sua região e desenhar o papel de parede de acordo (chuva, neve, sol,
  vento, neblina).
- Essas coordenadas são enviadas **apenas** ao serviço de meteorologia do
  **Instituto Meteorológico da Noruega (MET Norway)**, arredondadas para cerca
  de 1 km e por conexão criptografada, para consultar a previsão. Sua
  localização **nunca** passa por nenhum servidor nosso — nem o de clima, nem
  o de entrega de conteúdo descrito abaixo.
- **Não há conta de usuário, login, cadastro, anúncios, analytics, rastreamento
  entre apps ou venda de dados.** O app não coleta nome, e-mail, telefone,
  contatos, fotos, arquivos, agenda ou identificadores de publicidade.
- Suas preferências (cenário, arte, estilo, se você é Premium) e o último clima
  consultado ficam **só no seu aparelho**.
- **Alguns cenários são baixados sob demanda** de um servidor de arquivos
  (Cloudflare R2) quando você escolhe aplicá-los — ver seção 3.8. Esse servidor
  entrega imagens; não recebe sua localização nem sabe quem você é além do que
  qualquer acesso à internet revela (seção 3.2/6.5).
- O app **funciona sem a permissão de localização**: se você negar, ele usa uma
  cidade padrão (São Paulo, SP) e nada mais muda.
- Para apagar tudo: revogue a permissão, limpe os dados do app ou desinstale.
  Não sobra cópia dos seus dados pessoais em servidor nenhum, porque não
  guardamos nada além do que descrevemos aqui, e o que guardamos fica no seu
  aparelho.

---

## 1. Quem é o controlador dos dados

| | |
|---|---|
| **Controlador** | [PREENCHER: nome completo da pessoa física ou razão social do desenvolvedor] |
| **Aplicativo** | Terra - Live Wallpaper — `com.terra.wallpaper` |
| **Contato (privacidade / titular de dados)** | [PREENCHER: e-mail de contato] |
| **Encarregado pelo tratamento de dados pessoais (DPO, art. 41 da LGPD)** | Não exigido — o Terra se qualifica como agente de tratamento de pequeno porte (Resolução CD/ANPD nº 2/2022, art. 11); o canal de comunicação exigido pelo art. 41, §2º, I é o contato de privacidade acima. |
| **Endereço desta política** | [PREENCHER: URL pública desta página] |

Controlador, aqui, tem o sentido do art. 5º, VI da Lei nº 13.709/2018 (LGPD):
quem decide sobre o tratamento dos dados pessoais.

## 2. A que este documento se aplica

A este aplicativo Android e ao papel de parede animado que ele instala. Não se
aplica a:

- serviços de terceiros que o app consulta (ver seção 6), que têm políticas
  próprias;
- a loja onde você baixou o app (Google Play);
- o sistema operacional do seu aparelho.

## 3. Quais dados são tratados, para quê, e com que base legal

O Terra não pede cadastro e não cria identificador de usuário. Tudo abaixo
é o que o app efetivamente faz hoje.

### 3.1 Localização aproximada (coordenadas geográficas)

- **O que é:** latitude e longitude do aparelho, obtidas pelo serviço de
  localização do Android (Google Play Services). O app pede **precisão
  balanceada**, não GPS de alta precisão — o resultado costuma ter resolução de
  bairro/cidade, e não a sua posição exata.
- **Para quê:** consultar a condição meteorológica atual do local (temperatura,
  condição do tempo, chuva, nuvens, vento e umidade), calcular no próprio
  aparelho a sensação térmica e os horários de nascer e pôr do sol, e traduzir
  isso na cena animada do papel de parede. É a função central do
  produto: sem clima do lugar certo, o app não faz o que promete.
- **Quando acontece:** somente enquanto o papel de parede do Terra estiver visível na tela inicial, com o aparelho acordado e desbloqueado. Não são iniciadas novas consultas de clima ou de localização com outro app cobrindo a tela inicial, na tela de bloqueio, com a tela apagada ou nas prévias/configuração. A tela do aplicativo Terra exibe apenas o último clima salvo. Ao voltar à tela inicial, o último clima é exibido imediatamente e atualizado quando necessário. O intervalo escolhido em Ajustes → Cenário → Clima na tela inicial (15, 30 ou 60 minutos; padrão 30) vale durante esse uso, respeitando também o cache HTTP e eventuais restrições do fornecedor. Não há atualização periódica de clima por worker em segundo plano.

- **Para onde vai:** as coordenadas são enviadas ao serviço da **MET Norway**
  (seção 6.1) por HTTPS, como parâmetros da consulta de previsão,
  **arredondadas para duas casas decimais** (cerca de 1 km) antes de sair do
  aparelho. Não são
  enviadas a mais ninguém — nem ao servidor de entrega de conteúdo descrito na
  seção 3.8, que não tem qualquer acesso a essa informação.
- **Onde fica guardada:** no armazenamento privado do app, no seu aparelho
  (`SharedPreferences`, acessível somente ao app), junto do último clima
  recebido — para não repetir consultas à toa. É sobrescrita a cada nova
  consulta; guardamos só a mais recente.
- **Base legal:** consentimento do titular (art. 7º, I da LGPD), manifestado na
  permissão de localização do Android, que você pode revogar a qualquer momento.
  Para usuários no Espaço Econômico Europeu / Reino Unido, o fundamento
  equivalente é o art. 6(1)(a) do GDPR.
- **É opcional:** se você negar ou revogar a permissão, o app usa coordenadas
  fixas de São Paulo (SP, Brasil) e continua funcionando normalmente. Nenhuma
  funcionalidade é bloqueada; só o clima deixa de ser o seu.

### 3.2 Endereço IP

Toda consulta de previsão é uma requisição HTTPS à MET Norway e, como em
qualquer acesso à internet, o endereço IP do seu aparelho é visível para esse
serviço e para a sua operadora. Não coletamos, não recebemos e não registramos
esse IP — ele é tratado pela MET Norway conforme a política dela (seção 6.1).
A requisição também leva o nome e a versão do app e o nosso e-mail de suporte,
como a MET exige de todo aplicativo que usa o serviço; nada disso identifica
você.
Base legal: legítimo interesse na operação técnica do serviço (art. 7º, IX da
LGPD).

### 3.3 Dados meteorológicos recebidos

Temperatura, condição do tempo, quantidade de chuva, cobertura de nuvens,
velocidade do vento e umidade, recebidos da MET Norway; e sensação térmica,
indicador de dia/noite e horários de nascer e pôr do sol, calculados no
aparelho a partir deles. São dados
sobre o ambiente, não sobre você — mas ficam guardados junto das coordenadas no
cache local, e por isso constam aqui. Retenção e descarte: seção 8.

### 3.4 Preferências do app

Cenário escolhido, variante de arte de fundo, estilo de efeito e o indicador
local de plano Premium. Ficam apenas no armazenamento privado do app, no seu
aparelho. Não são enviados a ninguém. Base legal: execução do próprio serviço
solicitado por você (art. 7º, V da LGPD).

### 3.5 Compras dentro do app

Compras (Premium e cenários avulsos) são processadas **inteiramente pelo Google
Play Billing**. O app:

- **não vê e não armazena** dados de cartão, endereço de cobrança, CPF ou
  qualquer dado de pagamento;
- recebe do Google apenas o resultado da compra (identificador do produto,
  estado da compra e o comprovante assinado que confirma sua autenticidade);
- registra localmente um indicador booleano de que o conteúdo está liberado.

O histórico da compra fica na sua conta Google, e é ele que permite restaurar
compras em outro aparelho. Base legal: execução de contrato (art. 7º, V da
LGPD). O tratamento pelo Google segue a política dele (seção 6.3).

### 3.6 Cópia de segurança do Android

O app permite a Cópia de Segurança Automática do Android
(`allowBackup="true"`). Preferências comuns, como cenário e personalização,
podem participar do backup ou da transferência entre aparelhos, conforme as
configurações do sistema. As regras atuais **excluem o cache de clima e
coordenadas**, o estado de pausa de consultas à MET e o arquivo do indicador
Premium, tanto do backup quanto da transferência. Compras são restauradas
pela Google Play; o clima precisa de uma nova consulta quando não houver
cache local. Não temos acesso ao conteúdo do backup por esse mecanismo.
Você controla o backup nas configurações do Android. Essa configuração não
apaga automaticamente backups gerados por versões anteriores do app.

### 3.7 Diagnóstico e falhas

O app **não usa nenhuma ferramenta de relatório de falhas ou telemetria**
(nada de Firebase Crashlytics, Analytics, Sentry ou equivalente). Mensagens de
diagnóstico são escritas apenas no log do sistema Android, ficam no aparelho e
não são transmitidas a nós. Se o Android enviar um relatório de falha ao Google
depois de perguntar a você, isso é um mecanismo do sistema operacional, coberto
pela política do Google.

### 3.8 Download de conteúdo (cenários e artes)

- **O que é:** alguns cenários e artes do catálogo não vêm dentro do
  instalador — ficam num servidor de arquivos estáticos (Cloudflare R2) e são
  baixados sob demanda quando você escolhe aplicá-los.
- **Para quê:** manter o instalador pequeno em vez de embutir todo o acervo.
- **Quando acontece:** só quando você toca em aplicar um cenário/arte que
  ainda não está no seu aparelho. Não há download em segundo plano nem
  antecipado.
- **O que é enviado:** apenas o pedido HTTP do arquivo (ex.: `GET
  pack/<cenario>/<arte>.zip`) — o mesmo tipo de requisição de qualquer
  download de imagem na internet. **Sua localização não é enviada a este
  servidor em nenhuma hipótese**; ele não sabe qual é o clima do seu
  aparelho, nem precisa saber. Como em qualquer requisição HTTP, o endereço
  IP do seu aparelho é inerentemente visível ao operador da infraestrutura
  (seção 6.5), do mesmo jeito que já descrevemos para a MET Norway na
  seção 3.2.
- **O que volta:** o arquivo de imagem/pacote da arte, público para quem tiver
  o endereço — mas isso não libera o cenário no app: a posse é sempre
  verificada pela sua compra na Google Play (seção 3.5), separadamente do
  arquivo baixado. Baixar a arte de graça, por fora do app, não desbloqueia
  nada.
- **Onde fica guardado:** no armazenamento privado do app, no seu aparelho,
  junto das demais artes já baixadas. Você pode apagar o que baixou em
  Ajustes.
- **Base legal:** execução do próprio serviço solicitado por você (art. 7º, V
  da LGPD) — é você quem pede o download ao tocar em aplicar.

## 4. O que o Terra não faz

Declarado de forma explícita, porque a ausência também é informação:

- não cria conta, login ou perfil de usuário;
- não coleta nome, e-mail, telefone, documento, contatos, mensagens, agenda,
  fotos, vídeos, arquivos, microfone ou câmera;
- não usa Identificador de Publicidade (GAID), fingerprint de dispositivo ou
  qualquer identificador persistente para rastreamento;
- não exibe anúncios e não integra nenhum SDK de anúncios ou de analytics (ver
  seção 12);
- não rastreia você entre aplicativos ou sites;
- não vende, aluga, cede nem troca dados pessoais com ninguém;
- não usa seus dados para publicidade, pontuação de crédito, decisão automatizada
  ou treinamento de modelos de inteligência artificial;
- não tem banco de dados de usuários, conta ou backend de aplicação — o único
  servidor que operamos (seção 3.8) entrega arquivos de imagem estáticos, não
  processa nem armazena dado pessoal algum, e não sabe quem está pedindo o
  arquivo além do que qualquer download na internet revela.

## 5. Permissões do Android e o que cada uma faz

| Permissão | Para que é usada | Se você negar |
|---|---|---|
| `INTERNET`, `ACCESS_NETWORK_STATE` | Consultar a previsão do tempo na MET Norway e checar se há rede | Sem previsão; o app usa o último clima em cache ou o estado padrão |
| `ACCESS_COARSE_LOCATION` | Obter as coordenadas para a consulta de clima (seção 3.1). O app pede apenas **localização aproximada** — precisão balanceada, nunca GPS de alta precisão | O app usa São Paulo (SP) como local padrão e segue funcionando |

Não há receiver próprio nem reagendamento de clima após reiniciar o aparelho. Bibliotecas do Android, como WorkManager, podem acrescentar permissões técnicas ao manifesto mesclado; isso não autoriza consultas de clima fora da tela inicial.

## 6. Com quem os dados são compartilhados

Não vendemos nem compartilhamos dados para fins de marketing. Os únicos
terceiros envolvidos são os operadores técnicos abaixo, cada um com finalidade
específica.

### 6.1 MET Norway — previsão do tempo

- **Operador:** Meteorologisk institutt — Instituto Meteorológico da Noruega
  (MET Norway), órgão público norueguês. Servidores próprios na Europa (Oslo,
  Noruega).
- **O que recebe:** as coordenadas da consulta, arredondadas para cerca de
  1 km, e, inerentemente à requisição, o endereço IP do aparelho. Também recebe
  a identificação do app (nome, versão e nosso e-mail de suporte), exigida
  pelos termos de uso do serviço. Nenhum identificador de usuário ou de
  aparelho é enviado, e o serviço é consultado sem chave de API vinculada a você.
- **Por quê:** é a fonte dos dados meteorológicos que o papel de parede
  representa. Os dados são publicados pela MET Norway sob a licença
  [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).
- **Segundo a política deles:** o tráfego é registrado para detectar abusos e
  ataques, resolver problemas e gerar estatísticas anonimizadas de uso; nos
  serviços públicos de dados meteorológicos, os endereços IP são guardados
  **por até 90 dias**. Os logs podem conter as coordenadas consultadas.
- **Política:** <https://www.met.no/en/About-us/privacy> · Termos do serviço:
  <https://api.met.no/doc/TermsOfService>

### 6.2 Google Play Services (Localização) — Google LLC

Fornece a localização no próprio aparelho, através da API do sistema. É o
componente que nos entrega as coordenadas; nós não o alimentamos com dado
nenhum sobre você.
Política: <https://policies.google.com/privacy>

### 6.3 Google Play Billing / Google Play — Google LLC

Processa pagamentos, valida compras e permite restaurá-las. Todos os dados de
pagamento ficam com o Google; nós não os recebemos.
Política: <https://policies.google.com/privacy>

### 6.4 Cópia de Segurança do Android — Google LLC

Mecanismo do sistema descrito em 3.6.
Política: <https://policies.google.com/privacy>

### 6.5 Cloudflare R2 — entrega de conteúdo do acervo

- **Operador:** Cloudflare, Inc. (Estados Unidos).
- **O que recebe:** o pedido HTTP do arquivo de cenário/arte que você optou
  por baixar (seção 3.8) e, inerentemente à requisição, o endereço IP do seu
  aparelho. **Não recebe sua localização, nem qualquer dado da seção 3.1.**
- **Por quê:** é onde ficam hospedadas as imagens dos cenários que não vêm
  dentro do instalador.
- **Segundo a política deles:** a Cloudflare pode manter logs de acesso por
  período limitado para operação e segurança da infraestrutura, conforme a
  política de privacidade dela.
- **Política:** <https://www.cloudflare.com/privacypolicy/>

Além desses, dados pessoais podem ser divulgados se houver **obrigação legal,
ordem judicial ou requisição de autoridade competente** — o que, na prática,
esbarra no fato de não mantermos base de dados alguma.

## 7. Transferência internacional de dados

As coordenadas trafegam para a MET Norway, órgão público com servidores na
**Noruega** (Espaço Econômico Europeu, sujeito ao GDPR); os
serviços do Google podem tratar dados nos **Estados Unidos** e em outros países;
e o download de conteúdo do acervo (seção 3.8) passa pela infraestrutura da
Cloudflare, também com presença nos **Estados Unidos**. Essas transferências
ocorrem para a execução da finalidade que você solicitou e
para o cumprimento do contrato (art. 33, II, alíneas "a" e "f", e art. 33, VIII
da LGPD), amparadas nas garantias contratuais e políticas de privacidade dos
respectivos operadores.

## 8. Por quanto tempo os dados ficam guardados

| Dado | Onde | Retenção |
|---|---|---|
| Coordenadas + último clima (cache) | Seu aparelho | Apenas o registro mais recente; sobrescrito após uma consulta bem-sucedida durante o uso na tela inicial, respeitando o intervalo escolhido e o cache HTTP. Apagado ao limpar os dados do app ou desinstalar |
| Preferências (cenário, arte, estilo, Premium) | Seu aparelho | Enquanto o app estiver instalado |
| Backup do sistema | Sua conta Google | Conforme a política de backup do Android/Google, sob seu controle |
| Coordenadas e IP em logs da MET Norway | Servidores da MET Norway (Noruega) | Até 90 dias, conforme a política deles |
| Logs de acesso ao download de conteúdo (seção 3.8) | Infraestrutura da Cloudflare | Conforme a política deles (seção 6.5) — nunca inclui sua localização |
| Histórico de compras | Sua conta Google | Conforme a política do Google |

Do nosso lado não há retenção de dado pessoal: o servidor de conteúdo (3.8)
entrega arquivos públicos e não registra quem pediu o quê de forma que nos
identifique você.

## 9. Segurança

- Toda comunicação com a MET Norway usa **HTTPS/TLS**, e as coordenadas saem
  do aparelho arredondadas para cerca de 1 km.
- Cache e preferências ficam no armazenamento privado do app
  (`MODE_PRIVATE`), inacessível a outros aplicativos.
- Compras são validadas por **verificação de assinatura criptográfica** do
  comprovante emitido pelo Google Play antes de liberar conteúdo.
- Praticamos minimização: o app não pede precisão de GPS quando precisão de
  bairro basta, e não coleta nenhum dado que não seja usado.
- A superfície de ataque é pequena por construção: sem conta e sem banco de
  dados de usuários — o único servidor que operamos entrega arquivo público
  estático, não guarda cadastro de ninguém, então não há repositório de dados
  de usuários para ser vazado.

Nenhum sistema é perfeitamente seguro, e não podemos garantir segurança
absoluta — mas, na arquitetura atual, os dados que existem estão sob seu
controle, no seu aparelho.

## 10. Seus direitos

A LGPD (art. 18) garante a você, a qualquer momento e gratuitamente:
confirmação da existência de tratamento; acesso aos dados; correção de dados
incompletos, inexatos ou desatualizados; anonimização, bloqueio ou eliminação de
dados desnecessários ou tratados em desconformidade; portabilidade; eliminação
dos dados tratados com base em consentimento; informação sobre
compartilhamento; informação sobre a possibilidade de negar consentimento; e
revogação do consentimento. Usuários no EEE/Reino Unido têm direitos
equivalentes sob os arts. 15 a 22 do GDPR.

Como o app não mantém conta nem base de dados, a maior parte desses direitos se
exerce **diretamente no seu aparelho, sem depender de nós**:

- **Revogar o consentimento de localização:** Ajustes do Android → Apps →
  Terra → Permissões → Localização → Negar. O app passa a usar o local
  padrão imediatamente.
- **Eliminar os dados:** Ajustes do Android → Apps → Terra → Armazenamento →
  Limpar dados. Isso apaga cache de clima, coordenadas guardadas e
  preferências. Desinstalar o app tem o mesmo efeito.
- **Acesso e portabilidade:** os dados existentes são os descritos na seção 3 —
  não há mais nada sobre você além do que está no seu aparelho.
- **Compras:** gerenciadas na sua conta Google Play
  (<https://play.google.com/store/account>).

Para qualquer pedido, dúvida ou reclamação relativa a esta política, escreva
para **[PREENCHER: e-mail de contato]**. Responderemos em até **15 dias**
(prazo do art. 19, II da LGPD para pedidos de acesso). Você também pode
apresentar reclamação à **Autoridade Nacional de Proteção de Dados (ANPD)** —
<https://www.gov.br/anpd> — ou, no EEE/Reino Unido, à autoridade de proteção de
dados do seu país.

## 11. Crianças e adolescentes

O Terra é destinado a pessoas com **13 anos ou mais** e não é direcionado a
crianças menores de 13 anos. Entre 13 e 17 anos, o uso e qualquer compra
dependem de autorização e supervisão de um responsável legal, como dizem os
Termos de Uso, e a compra pode ainda exigir a aprovação prevista nos controles
parentais da Google Play. O Terra não coleta dados com o objetivo de
criar perfil de ninguém — nem de adultos, nem de menores. Não há conta,
publicidade, conteúdo gerado por usuários ou comunicação entre usuários. O único
dado pessoal tratado é a localização aproximada, para exibir o clima, sob
permissão do sistema operacional que o responsável pelo aparelho pode negar ou
revogar.

Se você é responsável por um menor de idade e acredita que dados pessoais dela foram
tratados de forma indevida por este app, escreva para
**[PREENCHER: e-mail de contato]** e agiremos para eliminar o que houver.

## 12. Anúncios e analytics

**Hoje o app não exibe anúncios e não usa analytics.** Nenhum SDK de
publicidade, atribuição ou métricas está integrado.

Se isso mudar em alguma versão futura, assumimos o compromisso de, **antes** de
qualquer coleta:

1. atualizar esta política, com a lista nominal de cada SDK e link para a
   política dele;
2. atualizar o formulário de Segurança dos Dados na Google Play;
3. solicitar **consentimento prévio, granular e revogável** (analytics,
   anúncios personalizados e não personalizados tratados em separado), por
   plataforma de consentimento compatível com as exigências do Google e da
   LGPD, sem disparar nenhum SDK antes do "aceito".

## 13. Alterações nesta política

Mudanças de funcionalidade que afetem dados pessoais serão refletidas aqui
antes ou junto do lançamento da versão correspondente. A cada revisão,
atualizamos a **versão da política** e a data de **última atualização** no topo
deste documento, e mantemos o histórico na seção 16. Alterações materiais — por
exemplo, passar a coletar um novo tipo de dado ou compartilhar dados com um novo
terceiro — serão comunicadas também nas notas de versão da Google Play e, quando
depender de consentimento novo, solicitadas dentro do app. Continuar usando o
app após a entrada em vigor de uma alteração significa que você tomou
conhecimento dela; onde a lei exigir consentimento, ele será pedido
explicitamente.

## 14. Contato

| | |
|---|---|
| **Assuntos de privacidade e direitos do titular** | [PREENCHER: e-mail de contato] |
| **Encarregado (DPO)** | Não exigido — agente de tratamento de pequeno porte (Resolução CD/ANPD nº 2/2022, art. 11); o canal de comunicação é o contato acima. |
| **Autoridade brasileira** | ANPD — <https://www.gov.br/anpd> |

## 15. Idioma e versão prevalente
O texto canônico desta política é o **português do Brasil**, publicado em [https://terra-livewallpaper.pages.dev/privacidade/](https://terra-livewallpaper.pages.dev/privacidade/). Traduções, quando existirem, são cortesia; em caso de divergência de interpretação, prevalece a versão em português.

## 16. Histórico de versões

| Versão | Data | Mudança |
|---|---|---|
| 1.5 | 10 de outubro de 2026 | Exclui cache de clima/localização, pausas da MET e indicador Premium do backup/transferência; preserva preferências comuns e esclarece backups anteriores. |
| 1.4 | 5 de outubro de 2026 | A partir do app 1.0.5, consultas somente com wallpaper visível na tela inicial, aparelho acordado/desbloqueado; companion e prévias usam cache; retirada da atualização periódica e do receiver próprio de boot. |
| 1.3 | 4 de outubro de 2026 | Seção 11: define o público do app como 13 anos ou mais, com autorização de responsável para menores de 18 anos, em linha com a declaração de público-alvo no Google Play Console. |
| 1.2 | 3 de outubro de 2026 | Troca o fornecedor de previsão do tempo: sai a Open-Meteo (Suíça), entra a MET Norway (Noruega) — seções 3.1, 3.2, 3.3, 5, 6.1, 7, 8 e 9. As coordenadas passam a sair do aparelho arredondadas para cerca de 1 km; sensação térmica e nascer/pôr do sol passam a ser calculados no aparelho. Corrigida a sigla do estado de São Paulo na seção 3.1. |
| 1.1 | 29 de setembro de 2026 | Adiciona a entrega de conteúdo via Cloudflare R2 (novo servidor, seções 3.8 e 6.5) — cenários e artes fora do catálogo grátis passam a ser baixados sob demanda. A localização nunca é enviada a esse servidor. Revisadas as seções 4, 7, 8 e 9, que afirmavam categoricamente a ausência de qualquer servidor. |
| 1.0 | [PREENCHER: data da primeira publicação] | Versão inicial, referente ao Terra 1.0.0 |
