> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](docs/dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

Atue como um advogado especialista em Direito Digital, LGPD e nas políticas do Google Play Console.
Preciso que você faça um pente-fino completo e rigoroso na documentação legal do meu aplicativo (Terra - Live Wallpaper), procurando qualquer ressalva, contradição ou risco futuro, para que eu possa prosseguir com segurança no cadastro do Google Play Console.

Abaixo, forneço o contexto atualizado de como o app funciona tecnicamente e qual é o modelo de negócios (validado com os stakeholders). Em seguida, forneço o texto integral das três páginas legais: Termos de Uso, Política de Privacidade e Contato.

---
## 1. CONTEXTO DO APLICATIVO E MODELO DE NEGÓCIOS

**Visão Geral:**
- **Nome do App:** Terra - Live Wallpaper (pacote: com.atmosfera.wallpaper).
- **Funcionalidade:** Papel de parede animado que reage ao clima real usando dados da Open-Meteo.
- **Funcionamento Técnico (Localização):** A localização aproximada é lida no aparelho e enviada à Open-Meteo para buscar o clima. Não temos servidor próprio para armazenar isso. No modo em segundo plano (background worker), o app NÃO busca o GPS novamente; ele apenas reutiliza a última coordenada em cache para atualizar o clima. Se o usuário negar a permissão, usamos uma cidade padrão fixa (Novo Hamburgo, RS).
- **Conteúdo Remoto (Acervo):** O app base é muito leve. Os cenários em alta resolução ficam hospedados em um bucket remoto (Cloudflare R2, público). O aplicativo faz o download do cenário apenas quando o usuário vai aplicá-lo na tela.

**Monetização (Pagamento via Google Play Billing - Sem assinaturas):**
O aplicativo separa estritamente os 'Efeitos' dos 'Cenários':
- **Terra Premium (R$ 49,90 - Compra Única):** Desbloqueia a *capacidade* de usar todos os efeitos climáticos vivos (chuva, neve, sol, lua, estilos visuais) em qualquer cenário que o usuário possua.
- **Cenários Avulsos / Pacotes (Ex: R$ 1,99 - Compra Única):** Desbloqueia *a arte* (o cenário). Comprar um cenário dá acesso ao download daquela arte do bucket remoto, mas NÃO dá o direito ao Premium (efeitos climáticos completos).
- *Nota:* Ter o Premium não dá acesso gratuito aos cenários extras; e comprar um cenário não dá o Premium. São duas coisas independentes.

---
## 2. DOCUMENTAÇÃO PARA REVISÃO

### DOCUMENTO 1: TERMOS DE USO
# Termos de Uso — Terra - Live Wallpaper

> **Texto canônico.** Esta é a fonte da verdade dos termos; a página publicada
> ([`docs/termos/index.html`](docs/termos/index.html)) é um espelho deste
> arquivo. Ao mudar um, mude o outro no mesmo commit.
>
> **Antes de publicar, preencha os `[PREENCHER: …]`** (desenvolvedor, e-mail de
> contato, foro e data de vigência) — os mesmos dados usados em
> [PRIVACIDADE.md](PRIVACIDADE.md).
>
> **Rascunho não revisado juridicamente.** Foi redigido a partir do que o app
> efetivamente faz (compra única via Google Play Billing, sem assinatura, sem
> conta). Se o modelo de monetização mudar — assinatura, anúncios, conteúdo
> gerado por usuário — este texto precisa ser refeito, não emendado.

**Versão dos termos:** 1.0
**Aplica-se a:** Terra - Live Wallpaper (Android, `com.terra.wallpaper`), a partir da versão 1.0.0
**Última atualização:** 30 de julho de 2026
**Vigente desde:** [PREENCHER: data da primeira publicação na Google Play]

---

## Resumo

Este resumo é uma cortesia e não substitui o texto completo abaixo.

- O Terra é um papel de parede animado que reage ao clima real da sua
  região. O uso é pessoal e não comercial.
- Parte do conteúdo é **gratuita**; cenários extras e o Premium são
  **compras únicas** pelo Google Play — **não há assinatura** e não há cobrança
  recorrente.
- Você compra uma **licença de uso** do conteúdo dentro do app, não a
  propriedade da arte. Não pode extrair, revender ou redistribuir os cenários.
- **Reembolso** segue as regras da Google Play — é com o Google, não conosco.
- O app depende de um serviço externo de meteorologia (Open-Meteo). Se ele
  ficar fora do ar, o clima pode não atualizar. Isso não é defeito do app.
- Tratamento de dados pessoais: ver a [Política de Privacidade](PRIVACIDADE.md).

---

## 1. Quem oferece o aplicativo

| | |
|---|---|
| **Desenvolvedor** | Rafael Huppes |
| **Aplicativo** | Terra - Live Wallpaper — `com.terra.wallpaper` |
| **Contato** | [PREENCHER: e-mail de contato] |
| **Endereço destes termos** | [PREENCHER: URL pública desta página] |

## 2. Aceitação

Ao instalar, abrir ou usar o Terra, você concorda com estes Termos de Uso e
com a [Política de Privacidade](PRIVACIDADE.md). Se não concordar, desinstale o
aplicativo — não há outra forma de uso.

Se você tem menos de 18 anos, o uso e qualquer compra dependem de autorização e
supervisão de um responsável legal, que também fica sujeito a estes termos.

## 3. O que o aplicativo faz

O Terra instala um papel de parede animado que desenha uma cena reagindo às
condições meteorológicas atuais da sua localização aproximada — chuva, neve,
nuvens, sol, lua, vento, neblina, ciclo de dia e noite. O aplicativo companion
permite ativar o papel de parede, escolher cenário, variante de arte e estilo
dos efeitos, e adquirir conteúdo adicional.

O clima vem do serviço de terceiros **Open-Meteo**. Não somos autores desses
dados meteorológicos e não garantimos precisão, atualidade ou disponibilidade
deles — o Terra não é, e não deve ser usado como, ferramenta de previsão do
tempo para qualquer decisão que dependa de exatidão meteorológica.

## 4. Licença de uso concedida a você

Concedemos a você uma licença **pessoal, limitada, não exclusiva, intransferível
e revogável** para usar o aplicativo e o conteúdo adquirido dentro dele, em
aparelhos vinculados à sua conta Google, para fins não comerciais.

Esta licença **não transfere propriedade** de nada. Toda a arte, código, sprites,
cenários, texturas, som (se houver), marca e identidade visual continuam sendo
de titularidade do desenvolvedor ou de seus licenciadores.

### 4.1 O que você não pode fazer

- extrair, copiar, descompilar, aplicar engenharia reversa ou modificar o
  aplicativo ou seus recursos, salvo na medida em que a lei expressamente
  permita apesar desta proibição;
- extrair os arquivos de arte dos cenários para uso fora do aplicativo;
- revender, sublicenciar, alugar, redistribuir ou disponibilizar publicamente o
  conteúdo do aplicativo;
- usar o conteúdo em produto, serviço, material promocional ou obra derivada de
  terceiros;
- contornar mecanismos de verificação de compra ou usar versões modificadas do
  aplicativo;
- usar o aplicativo para qualquer finalidade ilícita.

## 5. Compras dentro do aplicativo

### 5.1 Modelo

O Terra oferece conteúdo gratuito e conteúdo pago. Todo conteúdo pago é
vendido como **compra única** (não consumível), processada pelo **Google Play
Billing**:

- **Premium** — libera todas as artes dos cinco cenários, todos os estilos de
  efeito e os oito efeitos vivos.

**Não há assinatura, mensalidade, renovação automática ou cobrança recorrente de
nenhuma espécie.** Se isso mudar em versão futura, estes termos serão atualizados
antes, e a mudança não afetará o que você já comprou.

### 5.2 Preço e cobrança

Preços são exibidos dentro do aplicativo, na moeda e com os impostos aplicáveis
à sua conta Google Play. A cobrança é feita pelo Google, pelo meio de pagamento
cadastrado na sua conta. Não recebemos, não vemos e não armazenamos dados de
pagamento (ver seção 3.5 da [Política de Privacidade](PRIVACIDADE.md)).

Podemos alterar preços a qualquer momento. Alteração de preço **nunca** afeta
compra já concluída.

### 5.3 Restauração de compras

Compras ficam vinculadas à sua conta Google, não ao aparelho. Ao reinstalar o
aplicativo ou usar outro aparelho com a mesma conta, use **Ajustes → Restaurar
compras**. Não é preciso comprar de novo.

### 5.4 Reembolso e direito de arrependimento

Reembolsos são processados pelo **Google Play**, conforme as políticas dele —
não temos acesso ao seu pagamento e, por isso, não conseguimos processar
reembolso diretamente. Solicite em <https://play.google.com/store/account> ou
pelo suporte do Google Play.

Consumidores no Brasil têm o direito de arrependimento previsto no art. 49 do
Código de Defesa do Consumidor (7 dias contados da compra, para contratação
fora do estabelecimento comercial); esse direito é exercido pelo mesmo canal de
reembolso da Google Play. Consumidores no Espaço Econômico Europeu e no Reino
Unido têm direitos equivalentes de desistência sob a legislação local.

Nada nestes termos afeta direitos que a legislação de consumo aplicável garanta
a você e que não possam ser afastados por contrato.

## 6. Conteúdo futuro e disponibilidade

Podemos adicionar, alterar ou remover cenários, estilos e funcionalidades ao
longo do tempo. Nos comprometemos a **não remover o acesso a conteúdo que você
já comprou**, salvo se for tecnicamente impossível mantê-lo (por exemplo,
alteração de política obrigatória da plataforma) — hipótese em que a remoção
será comunicada.

Não garantimos que o aplicativo estará disponível ininterruptamente, nem que
funcionará em todos os aparelhos, versões de Android ou interfaces de fabricante.
Papel de parede animado depende de recursos que alguns fabricantes restringem
por economia de bateria; isso está fora do nosso controle.

## 7. Serviços de terceiros

O aplicativo depende de:

- **Open-Meteo** (dados meteorológicos) — <https://open-meteo.com/en/terms>
- **Google Play Services** (localização no aparelho) — <https://policies.google.com/terms>
- **Google Play Billing** (pagamentos) — <https://policies.google.com/terms>

O uso desses serviços é regido pelos termos de cada um. Não respondemos pela
disponibilidade, precisão ou práticas deles.

## 8. Privacidade

O tratamento de dados pessoais é descrito na
[Política de Privacidade](PRIVACIDADE.md), que integra estes termos. Em resumo:
o app usa localização aproximada para consultar o clima, não tem conta, não
exibe anúncios e não usa analytics. Alguns cenários são baixados de um
servidor de arquivos (Cloudflare R2) quando você escolhe aplicá-los — esse
servidor não recebe sua localização nem processa dado pessoal (política §3.8).

## 9. Garantias e limitação de responsabilidade

O aplicativo é fornecido **"no estado em que se encontra"**, sem garantias além
das legalmente obrigatórias. Não garantimos que estará livre de erros, nem que
os dados meteorológicos exibidos serão exatos.

Na máxima extensão permitida pela lei aplicável, não respondemos por danos
indiretos, lucros cessantes, perda de dados ou consumo de bateria/dados móveis
decorrentes do uso do aplicativo. **Esta limitação não se aplica** a dolo, culpa
grave, danos à vida, integridade física ou saúde, nem a qualquer
responsabilidade que a legislação de consumo aplicável não permita excluir ou
limitar — em especial as garantias legais do Código de Defesa do Consumidor
brasileiro.

## 10. Suspensão e encerramento

Você pode encerrar o uso a qualquer momento desinstalando o aplicativo.
Desinstalar não cancela nem reembolsa compras já feitas — elas permanecem
vinculadas à sua conta Google e podem ser restauradas.

Podemos encerrar ou suspender o acesso em caso de violação destes termos, em
especial das vedações da seção 4.1.

## 11. Alterações destes termos

Podemos alterar estes termos para refletir mudanças no aplicativo, na
legislação ou nas políticas das plataformas. A versão vigente é sempre a
publicada no endereço indicado na seção 1, com data de última atualização no
topo. Alterações materiais serão comunicadas nas notas de versão da Google Play
e, quando a lei exigir, dentro do aplicativo.

Continuar usando o aplicativo após a entrada em vigor de uma alteração significa
que você tomou conhecimento dela.

## 12. Lei aplicável e foro

Estes termos são regidos pelas leis da **República Federativa do Brasil**.

Fica eleito o foro de **Novo Hamburgo (RS)** para
dirimir controvérsias, ressalvado o direito do consumidor de demandar no foro do
seu próprio domicílio (art. 101, I do Código de Defesa do Consumidor) e as
regras de competência que a lei do país de residência do usuário torne
obrigatórias.

## 13. Contato

| | |
|---|---|
| **Suporte, dúvidas e reclamações** | [PREENCHER: e-mail de contato] |
| **Assuntos de privacidade** | [PREENCHER: e-mail de contato] — ver [PRIVACIDADE.md](PRIVACIDADE.md) |
| **Reembolso** | Google Play — <https://play.google.com/store/account> |

## 14. Histórico de versões

| Versão | Data | Mudança |
|---|---|---|
| 1.0 | [PREENCHER: data da primeira publicação] | Versão inicial, referente ao Terra 1.0.0 |


### DOCUMENTO 2: POLÍTICA DE PRIVACIDADE
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

**Versão da política:** 1.1
**Aplica-se a:** Terra - Live Wallpaper (Android, `com.terra.wallpaper`), a partir da versão 1.0.0
**Última atualização:** 29 de setembro de 2026
**Vigente desde:** [PREENCHER: data da primeira publicação na Google Play]

---

## Resumo

Este resumo é uma cortesia e não substitui o texto completo abaixo.

- O Terra usa a **localização aproximada** do seu aparelho para descobrir o
  clima da sua região e desenhar o papel de parede de acordo (chuva, neve, sol,
  vento, neblina).
- Essas coordenadas são enviadas **apenas** ao serviço de meteorologia
  **Open-Meteo**, por conexão criptografada, para consultar a previsão. Sua
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
  fixas de São Paulo (RS, Brasil) e continua funcionando normalmente. Nenhuma
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
  (seção 6.5), do mesmo jeito que já descrevemos para a Open-Meteo na
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
| `INTERNET`, `ACCESS_NETWORK_STATE` | Consultar a previsão do tempo na Open-Meteo e checar se há rede | Sem previsão; o app usa o último clima em cache ou o estado padrão |
| `ACCESS_COARSE_LOCATION` | Obter as coordenadas para a consulta de clima (seção 3.1). O app pede apenas **localização aproximada** — precisão balanceada, nunca GPS de alta precisão | O app usa São Paulo (SP) como local padrão e segue funcionando |
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

As coordenadas trafegam para a Open-Meteo, cujo operador está na **Suíça**; os
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
| Coordenadas + último clima (cache) | Seu aparelho | Apenas o registro mais recente; sobrescrito a cada nova consulta (no máximo uma vez por intervalo escolhido em Ajustes — 15, 30 ou 60 min). Apagado ao limpar os dados do app ou desinstalar |
| Preferências (cenário, arte, estilo, Premium) | Seu aparelho | Enquanto o app estiver instalado |
| Backup do sistema | Sua conta Google | Conforme a política de backup do Android/Google, sob seu controle |
| Coordenadas em logs da Open-Meteo | Servidores da Open-Meteo | Até 90 dias, conforme a política deles |
| Logs de acesso ao download de conteúdo (seção 3.8) | Infraestrutura da Cloudflare | Conforme a política deles (seção 6.5) — nunca inclui sua localização |
| Histórico de compras | Sua conta Google | Conforme a política do Google |

Do nosso lado não há retenção de dado pessoal: o servidor de conteúdo (3.8)
entrega arquivos públicos e não registra quem pediu o quê de forma que nos
identifique você.

## 9. Segurança

- Toda comunicação com a Open-Meteo usa **HTTPS/TLS**.
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

O Terra não é direcionado a crianças e não coleta dados com o objetivo de
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
| 1.1 | 29 de setembro de 2026 | Adiciona a entrega de conteúdo via Cloudflare R2 (novo servidor, seções 3.8 e 6.5) — cenários e artes fora do catálogo grátis passam a ser baixados sob demanda. A localização nunca é enviada a esse servidor. Revisadas as seções 4, 7, 8 e 9, que afirmavam categoricamente a ausência de qualquer servidor. |
| 1.0 | [PREENCHER: data da primeira publicação] | Versão inicial, referente ao Terra 1.0.0 |


### DOCUMENTO 3: CONTATO
# Contato e uso de dados — Terra - Live Wallpaper

> **Texto canônico.** A página publicada
> ([`docs/contato/index.html`](docs/contato/index.html)) é um espelho deste
> arquivo. Ao mudar um, mude o outro no mesmo commit.
>
> **Antes de publicar, preencha os `[PREENCHER: …]`** — os mesmos dados de
> [PRIVACIDADE.md](PRIVACIDADE.md) e [TERMOS.md](TERMOS.md).
>
> Esta página existe porque a tela de Ajustes do app abre um HTML local em vez
> de um `mailto:` — decisão do usuário: além do endereço de contato, a página
> precisa dizer **o que acontece com os dados** de quem escreve.

**Última atualização:** 16 de setembro de 2026

---

## Como falar com a gente

| Assunto | Canal |
|---|---|
| **Suporte, bug, sugestão** | [PREENCHER: e-mail de contato] |
| **Privacidade e direitos do titular (LGPD)** | [PREENCHER: e-mail de contato] |
| **Reembolso de compra** | Google Play — <https://play.google.com/store/account> (não conseguimos processar reembolso, é com o Google) |
| **Autoridade brasileira de proteção de dados** | ANPD — <https://www.gov.br/anpd> |

Respondemos pedidos relativos a dados pessoais em até **15 dias**, prazo do
art. 19, II da LGPD.

## O que informar num pedido de suporte

Quanto mais específico, mais rápido resolvemos. Ajuda muito:

- modelo do aparelho e versão do Android;
- versão do app (aparece em **Ajustes → Sobre**);
- qual cenário e estilo estavam ativos;
- o que você esperava que acontecesse e o que aconteceu;
- se der, um print ou vídeo curto da tela.

**Não envie** senha, dados de cartão, CPF ou documento. Nunca pedimos isso, e
não temos como usá-los — pagamentos são processados inteiramente pelo Google.

### Identificação do controlador
Para os fins da LGPD, o controlador de dados é **Rafael Huppes**.

## O que fazemos com os dados do seu contato

Esta é a parte que a Política de Privacidade não cobre, porque trata do app —
aqui trata-se do **e-mail que você nos envia**.

| | |
|---|---|
| **O que recebemos** | O que você escrever: seu endereço de e-mail, o conteúdo da mensagem e qualquer anexo que você decidir mandar (print, vídeo). |
| **Para quê** | Responder você e corrigir o problema relatado. Nada mais. |
| **Base legal** | Execução de procedimento preliminar a pedido do titular (art. 7º, V da LGPD) e, para pedidos de direitos, cumprimento de obrigação legal (art. 7º, II). |
| **Com quem é compartilhado** | Ninguém. A mensagem fica na caixa de e-mail do desenvolvedor, no provedor de e-mail utilizado. Não repassamos a terceiros, não usamos para marketing e não alimentamos lista de contatos. |
| **Por quanto tempo** | Enquanto for útil para o atendimento e o histórico do caso; pedidos de direitos do titular ficam registrados pelo prazo necessário para comprovar o atendimento. Você pode pedir a exclusão a qualquer momento. |
| **Seus direitos** | Os mesmos do art. 18 da LGPD — acesso, correção, eliminação, portabilidade e revogação. Escreva para o mesmo endereço pedindo. |

Importante: **o app em si continua não coletando nada disso.** Ele não tem
formulário, não envia mensagem em segundo plano e não sabe o seu e-mail. O
único dado pessoal tratado pelo aplicativo é a localização aproximada, para
consultar o clima — detalhes em [PRIVACIDADE.md](PRIVACIDADE.md).

## Resumo do que o app trata (atalho)

Para não obrigar você a abrir a política inteira:

- **Localização aproximada** → enviada só à Open-Meteo, para saber o clima.
  Sem ela, o app usa São Paulo (SP) e funciona igual. Nunca vai pro servidor
  de conteúdo abaixo.
- **Preferências** (cenário, arte, estilo, Premium) → ficam só no aparelho.
- **Último clima consultado** → cache local, sobrescrito a cada consulta.
- **Compras** → processadas pelo Google Play; não vemos dados de pagamento.
- **Download de cenários** → alguns vêm de um servidor de arquivos (Cloudflare
  R2), baixado só quando você escolhe aplicar. Esse servidor não recebe sua
  localização nem processa dado pessoal.
- **Não existe** conta, login, anúncio, analytics, rastreamento ou banco de
  dados de usuários do nosso lado.

Texto completo: [Política de Privacidade](PRIVACIDADE.md) ·
[Termos de Uso](TERMOS.md)


---
## 3. O QUE ESPERO DA SUA ANÁLISE:

1. **Adequação ao Modelo de Negócios:** Os Termos de Uso estão claros o suficiente sobre a separação entre a compra do Premium (apenas efeitos) e a compra dos Cenários (download da arte)?
2. **Download de Acervo Remoto:** O fato das imagens ficarem em um bucket Cloudflare e serem baixadas sob demanda está devidamente coberto, ou precisamos de alguma isenção de responsabilidade sobre tráfego de dados do usuário?
3. **Google Play e LGPD:** Há alguma contradição nas políticas de privacidade, especialmente sobre como o app trabalha em segundo plano (reutilizando cache sem bater no GPS)?
4. **Proteção Geral:** Encontra alguma brecha que possa me dar dor de cabeça com estornos, pirataria ou bloqueio na loja do Google?

Se encontrar problemas, reescreva o parágrafo problemático sugerindo a correção.
