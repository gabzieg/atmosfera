# Política de Privacidade — Atmosfera Live Wallpaper

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

**Versão da política:** 1.0
**Aplica-se a:** Atmosfera Live Wallpaper (Android, `com.atmosfera.wallpaper`), a partir da versão 1.0.0
**Última atualização:** 1º de agosto de 2026
**Vigente desde:** [PREENCHER: data da primeira publicação na Google Play]

---

## Resumo

Este resumo é uma cortesia e não substitui o texto completo abaixo.

- O Atmosfera usa a **localização aproximada** do seu aparelho para descobrir o
  clima da sua região e desenhar o papel de parede de acordo (chuva, neve, sol,
  vento, neblina).
- Essas coordenadas são enviadas **apenas** ao serviço de meteorologia
  **Open-Meteo**, por conexão criptografada, para consultar a previsão. **Não
  temos servidor próprio** — nós, os desenvolvedores, nunca recebemos sua
  localização.
- **Não há conta de usuário, login, cadastro, anúncios, analytics, rastreamento
  entre apps ou venda de dados.** O app não coleta nome, e-mail, telefone,
  contatos, fotos, arquivos, agenda ou identificadores de publicidade.
- Suas preferências (cenário, arte, estilo, se você é Premium) e o último clima
  consultado ficam **só no seu aparelho**.
- O app **funciona sem a permissão de localização**: se você negar, ele usa uma
  cidade padrão (Guarapuava, PR) e nada mais muda.
- Para apagar tudo: revogue a permissão, limpe os dados do app ou desinstale.
  Não sobra cópia em nenhum servidor nosso, porque não existe servidor nosso.

---

## 1. Quem é o controlador dos dados

| | |
|---|---|
| **Controlador** | [PREENCHER: nome completo da pessoa física ou razão social do desenvolvedor] |
| **Aplicativo** | Atmosfera Live Wallpaper — `com.atmosfera.wallpaper` |
| **Contato (privacidade / titular de dados)** | [PREENCHER: e-mail de contato] |
| **Encarregado pelo tratamento de dados pessoais (DPO, art. 41 da LGPD)** | [PREENCHER: nome e e-mail — pode ser a mesma pessoa do desenvolvedor] |
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

O Atmosfera não pede cadastro e não cria identificador de usuário. Tudo abaixo
é o que o app efetivamente faz hoje.

### 3.1 Localização aproximada (coordenadas geográficas)

- **O que é:** latitude e longitude do aparelho, obtidas pelo serviço de
  localização do Android (Google Play Services). O app pede **precisão
  balanceada**, não GPS de alta precisão — o resultado costuma ter resolução de
  bairro/cidade, e não a sua posição exata.
- **Para quê:** consultar a condição meteorológica atual do local (temperatura,
  sensação térmica, código de tempo, vento, umidade, dia/noite, nascer e pôr do
  sol) e traduzir isso na cena animada do papel de parede. É a função central do
  produto: sem clima do lugar certo, o app não faz o que promete.
- **Quando acontece:** ao abrir o app, quando o papel de parede fica visível e
  em uma verificação periódica em segundo plano, apenas com rede disponível. O
  intervalo dessa verificação é **escolhido por você** em Ajustes → Cenário →
  Atualizar clima (15, 30 ou 60 minutos; o padrão é 30). Consultas são evitadas
  se o clima em cache ainda estiver fresco (dentro do intervalo escolhido) e
  você não tiver se deslocado mais de ~5 km.
- **Para onde vai:** as coordenadas são enviadas ao serviço **Open-Meteo**
  (seção 6.1) por HTTPS, como parâmetros da consulta de previsão. Não são
  enviadas a mais ninguém. Não existe servidor do Atmosfera.
- **Onde fica guardada:** no armazenamento privado do app, no seu aparelho
  (`SharedPreferences`, acessível somente ao app), junto do último clima
  recebido — para não repetir consultas à toa. É sobrescrita a cada nova
  consulta; guardamos só a mais recente.
- **Base legal:** consentimento do titular (art. 7º, I da LGPD), manifestado na
  permissão de localização do Android, que você pode revogar a qualquer momento.
  Para usuários no Espaço Econômico Europeu / Reino Unido, o fundamento
  equivalente é o art. 6(1)(a) do GDPR.
- **É opcional:** se você negar ou revogar a permissão, o app usa coordenadas
  fixas de Guarapuava (PR, Brasil) e continua funcionando normalmente. Nenhuma
  funcionalidade é bloqueada; só o clima deixa de ser o seu.

### 3.2 Endereço IP

Toda consulta de previsão é uma requisição HTTPS à Open-Meteo e, como em
qualquer acesso à internet, o endereço IP do seu aparelho é visível para esse
serviço e para a sua operadora. Não coletamos, não recebemos e não registramos
esse IP — ele é tratado pela Open-Meteo conforme a política dela (seção 6.1).
Base legal: legítimo interesse na operação técnica do serviço (art. 7º, IX da
LGPD).

### 3.3 Dados meteorológicos recebidos

Temperatura, sensação térmica, código de condição (WMO), velocidade do vento,
umidade, indicador de dia/noite, horários de nascer e pôr do sol. São dados
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
(`allowBackup="true"`). Isso significa que as preferências e o cache descritos
em 3.1, 3.3 e 3.4 podem ser incluídos no backup do seu aparelho, armazenado na
**sua** conta Google — pelo mecanismo do sistema operacional, sem nossa
intervenção e sem acesso nosso a esse conteúdo. Você controla isso nas
configurações de backup do Android. Base legal: legítimo interesse em preservar
suas preferências entre aparelhos (art. 7º, IX da LGPD).

### 3.7 Diagnóstico e falhas

O app **não usa nenhuma ferramenta de relatório de falhas ou telemetria**
(nada de Firebase Crashlytics, Analytics, Sentry ou equivalente). Mensagens de
diagnóstico são escritas apenas no log do sistema Android, ficam no aparelho e
não são transmitidas a nós. Se o Android enviar um relatório de falha ao Google
depois de perguntar a você, isso é um mecanismo do sistema operacional, coberto
pela política do Google.

## 4. O que o Atmosfera não faz

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
- não tem servidor próprio, banco de dados ou backend — não há onde acumular
  seus dados do nosso lado.

## 5. Permissões do Android e o que cada uma faz

| Permissão | Para que é usada | Se você negar |
|---|---|---|
| `INTERNET`, `ACCESS_NETWORK_STATE` | Consultar a previsão do tempo na Open-Meteo e checar se há rede | Sem previsão; o app usa o último clima em cache ou o estado padrão |
| `ACCESS_COARSE_LOCATION` | Obter as coordenadas para a consulta de clima (seção 3.1). O app pede apenas **localização aproximada** — precisão balanceada, nunca GPS de alta precisão | O app usa Guarapuava (PR) como local padrão e segue funcionando |
| `RECEIVE_BOOT_COMPLETED` | Reagendar a atualização periódica de clima depois de reiniciar o aparelho | A atualização periódica volta a ser agendada na próxima vez que você abrir o app |

Nenhuma outra permissão é declarada pelo app.

## 6. Com quem os dados são compartilhados

Não vendemos nem compartilhamos dados para fins de marketing. Os únicos
terceiros envolvidos são os operadores técnicos abaixo, cada um com finalidade
específica.

### 6.1 Open-Meteo — previsão do tempo

- **Operador:** OpenMeteo GmbH (Suíça).
- **O que recebe:** as coordenadas da consulta e, inerentemente à requisição, o
  endereço IP do aparelho. Nada mais: nenhum identificador de usuário ou de
  aparelho é enviado, e o serviço é consultado sem chave de API vinculada a você.
- **Por quê:** é a fonte dos dados meteorológicos que o papel de parede
  representa.
- **Segundo a política deles:** podem manter arquivos de log de servidor que
  contenham informações como coordenadas geográficas, para diagnóstico, e esses
  logs são **excluídos após 90 dias**.
- **Política:** <https://open-meteo.com/en/terms>

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

Além desses, dados pessoais podem ser divulgados se houver **obrigação legal,
ordem judicial ou requisição de autoridade competente** — o que, na prática,
esbarra no fato de não mantermos base de dados alguma.

## 7. Transferência internacional de dados

As coordenadas trafegam para a Open-Meteo, cujo operador está na **Suíça**, e os
serviços do Google podem tratar dados nos **Estados Unidos** e em outros países.
Essas transferências ocorrem para a execução da finalidade que você solicitou e
para o cumprimento do contrato (art. 33, II, alíneas "a" e "f", e art. 33, VIII
da LGPD), amparadas nas garantias contratuais e políticas de privacidade dos
respectivos operadores.

## 8. Por quanto tempo os dados ficam guardados

| Dado | Onde | Retenção |
|---|---|---|
| Coordenadas + último clima (cache) | Seu aparelho | Apenas o registro mais recente; sobrescrito a cada nova consulta (no máximo uma vez por intervalo escolhido em Ajustes — 15, 30 ou 60 min). Apagado ao limpar os dados do app ou desinstalar |
| Preferências (cenário, arte, estilo, Premium) | Seu aparelho | Enquanto o app estiver instalado |
| Backup do sistema | Sua conta Google | Conforme a política de backup do Android/Google, sob seu controle |
| Coordenadas em logs da Open-Meteo | Servidores da Open-Meteo | Até 90 dias, conforme a política deles |
| Histórico de compras | Sua conta Google | Conforme a política do Google |

Do nosso lado não há retenção: não recebemos e não armazenamos nada.

## 9. Segurança

- Toda comunicação com a Open-Meteo usa **HTTPS/TLS**.
- Cache e preferências ficam no armazenamento privado do app
  (`MODE_PRIVATE`), inacessível a outros aplicativos.
- Compras são validadas por **verificação de assinatura criptográfica** do
  comprovante emitido pelo Google Play antes de liberar conteúdo.
- Praticamos minimização: o app não pede precisão de GPS quando precisão de
  bairro basta, e não coleta nenhum dado que não seja usado.
- A superfície de ataque é pequena por construção: sem conta, sem servidor, sem
  base de dados centralizada, não há repositório de dados de usuários para ser
  vazado.

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
  Atmosfera → Permissões → Localização → Negar. O app passa a usar o local
  padrão imediatamente.
- **Eliminar os dados:** Ajustes do Android → Apps → Atmosfera → Armazenamento →
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

O Atmosfera não é direcionado a crianças e não coleta dados com o objetivo de
criar perfil de ninguém — nem de adultos, nem de menores. Não há conta,
publicidade, conteúdo gerado por usuários ou comunicação entre usuários. O único
dado pessoal tratado é a localização aproximada, para exibir o clima, sob
permissão do sistema operacional que o responsável pelo aparelho pode negar ou
revogar.

Se você é responsável por uma criança e acredita que dados pessoais dela foram
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
deste documento, e mantemos o histórico na seção 15. Alterações materiais — por
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
| **Encarregado (DPO)** | [PREENCHER: nome e e-mail] |
| **Autoridade brasileira** | ANPD — <https://www.gov.br/anpd> |

## 15. Histórico de versões

| Versão | Data | Mudança |
|---|---|---|
| 1.0 | [PREENCHER: data da primeira publicação] | Versão inicial, referente ao Atmosfera 1.0.0 |
