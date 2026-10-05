# Guia — Data Safety Form e Content Rating (IARC)

> Respostas derivadas do **código atual**, não de suposição. Cada resposta
> abaixo tem a linha de código que a sustenta. Se o código mudar, esta folha
> mente — a tabela de rastreio em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md#rastreio-código--política)
> diz o que revisar.
>
> **Divisão de responsabilidade** (ver [SPEC.md](SPEC.md)): o Data Safety é
> declaração sobre o que o código faz, e é do Gabriel — junto com os textos
> legais, desde a saída do Willian em 2026-08-28. A conferência que era dele
> agora é feita por teste: `PoliticaBatecomManifestoTest` quebra o gate se a
> política divergir das permissões do manifesto.

**Última atualização:** 3 de outubro de 2026 · confere com o código da `main` após a troca para a MET Norway

> **Mudança de 03/10:** o clima saiu da Open-Meteo (API gratuita só para uso
> não comercial) e foi para a MET Norway. **Nenhuma resposta do formulário
> muda de categoria**: continua só "Localização aproximada", compartilhada com
> o serviço de clima para funcionalidade do app. Muda apenas o nome do
> destinatário na política (§6.1) e as coordenadas, que agora saem
> arredondadas para ~1 km. O cabeçalho User-Agent que a MET exige leva o nome
> do app e o e-mail de suporte — dado nosso, não do usuário.

> **Mudança de 29/09:** o app passou a baixar alguns cenários/artes sob
> demanda de um servidor de arquivos (Cloudflare R2) — ver
> `docs/legal/PRIVACIDADE.md` §3.8/§6.5. Isso **não adiciona nenhum tipo de
> dado novo** ao formulário (ver "Download de conteúdo (R2)" abaixo) — é a
> mesma categoria de tráfego que o serviço de clima já tinha (endereço IP inerente à
> requisição, não coletado nem usado pra identificar ninguém).

---

## Antes de começar

O Data Safety só aparece depois que o app existe no Play Console. Ordem:

1. Conta de desenvolvedor criada (US$ 25, uma vez — pode levar dias pra aprovar)
2. App criado no console (`com.terra.wallpaper`)
3. **URL da política de privacidade já no ar** — o formulário a exige
4. Aí sim: Política do app → Segurança dos dados

---

## Data Safety Form

### Passo 1 — Visão geral

| Pergunta | Resposta | Base no código |
|---|---|---|
| O app coleta ou compartilha algum dos tipos de dados exigidos? | **Sim** | Localização é enviada à MET Norway |
| Todos os dados são criptografados em trânsito? | **Sim** | `WeatherRepository` usa `https://api.met.no/`; o download de conteúdo (`Acervo.kt`) usa HTTPS contra o bucket R2 |
| Você fornece um meio de o usuário pedir exclusão dos dados? | **Sim** | Nenhum servidor nosso guarda dado pessoal — limpar dados do app ou desinstalar apaga tudo do lado do usuário, descrito na política §10 |

### Passo 2 — Tipos de dados

Marque **apenas** o que está abaixo. Marcar a mais é tão problemático quanto a
menos.

#### Localização → Localização aproximada — **SIM**

| Campo | Resposta |
|---|---|
| Coletado | Sim |
| Compartilhado | **Sim** — enviado à MET Norway (arredondado para ~1 km) |
| Obrigatório? | **Opcional** (o app funciona sem; cai no fallback de São Paulo/SP) |
| Finalidade | **Funcionalidade do app** |
| Processado de forma efêmera? | Não (fica em cache local) |

#### Localização → Localização precisa — **NÃO**

Era **SIM** até 2026-08-08, porque o manifesto declarava `ACCESS_FINE_LOCATION`
e o Google compara a declaração com o que o APK pede. A permissão foi
**removida** naquela data: nenhum caminho do código exigia precisão fina (o
portão é `LocationHelper.hasPermission()`, que só checa COARSE, e a busca pede
`PRIORITY_BALANCED_POWER_ACCURACY`).

Agora a resposta consistente é **não coletar localização precisa**. Marcar
"sim" passaria a ser a inconsistência — e inconsistência entre Data Safety e
APK é a **causa nº1 de rejeição**. Se alguém readicionar `ACCESS_FINE_LOCATION`
ao manifesto, este item volta a ser **SIM** e a política §5 precisa acompanhar.

#### Compras no app — **NÃO marcar como coletado por você**

O Google Play Billing processa tudo. O app recebe só o resultado da compra e
grava um booleano local (`Plano`). Você não coleta histórico de compras — o
Google coleta, e isso é declarado por ele, não por você.

#### Download de conteúdo (R2) — **NÃO marcar como tipo de dado novo**

`Acervo.kt` baixa arquivo de cenário/arte de um bucket Cloudflare R2 quando o
usuário toca em aplicar (política §3.8). A requisição HTTP carrega, de forma
inerente, o endereço IP do aparelho — exatamente a mesma situação que a
consulta de clima já tinha, e que este guia nunca tratou como "Device or
other IDs" coletado. Não há usuário, sessão, token nem qualquer identificador
enviado junto do pedido: `Acervo.baixarArte()` recebe só `cena`/`arte`/URL, sem
parâmetro de conta, e a posse do conteúdo é validada pelo `Plano`/`Catalogo`
**antes** de chamar o download, não pelo servidor (o R2 nem sabe que existe
uma compra). Se algum dia o bucket passar a exigir token de compra (ver
`docs/dev/PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md` §3, "Proteção do
bucket"), esta resposta precisa ser revisada — um backend que valida token de
usuário passa a ser um identificador associável a você.

#### Tudo o mais — **NÃO**

Nome, e-mail, telefone, ID de usuário, contatos, fotos, arquivos, mensagens,
áudio, calendário, atividade no app, histórico de navegação, desempenho,
diagnósticos, ID de dispositivo, ID de publicidade: **nenhum**. Não há conta,
analytics, crash reporting ou SDK de anúncios — política §4 e §12.

### Passo 3 — Práticas de segurança

| Pergunta | Resposta |
|---|---|
| Dados criptografados em trânsito | **Sim** (HTTPS/TLS) |
| Usuário pode pedir exclusão | **Sim** |
| Comprometido com a Play Families Policy | Não (o app não é direcionado a crianças) |
| Passou por avaliação de segurança independente | Não |

### Armadilha do backup

`AndroidManifest.xml` tem `allowBackup="true"`: preferências comuns podem ir
pro backup do Android, na conta Google **do usuário**. O arquivo de posse
`atmosfera_plano.xml` está excluído do backup e da transferência entre aparelhos;
o Premium é restaurado pela compra na Play. Não é coleta sua (é mecanismo do
sistema) e **não** precisa ser declarado como compartilhamento — mas está
documentado na política §3.6 para ser honesto.

---

## Público-alvo e conteúdo (decidido em 2026-10-04)

Declare **13 anos ou mais** (faixas 13–15, 16–17 e 18+). **Não** marque as
faixas abaixo de 13: isso colocaria o app na Política de Famílias do Google, com
regras bem mais duras, e contradiria a política de privacidade (§11). Não é
preciso marcar só 18+: a autorização de responsável para menores de 18 anos já
está nos Termos (seção 2), e a Google Play tem controles parentais próprios para
aprovar compras (Family Link), que não dependem do app.

Pergunta "o app atrai crianças?": **não** — são paisagens, mas sem personagem,
mascote ou linguagem voltada a criança pequena. Se a arte mudar nesse sentido,
refaça esta declaração.

A classificação indicativa (IARC, abaixo) é **separada** do público-alvo: o app
pode ser "Livre" e mesmo assim declarar 13+.

---

## Content Rating (IARC)

Questionário rápido. O Terra é um papel de parede sem conteúdo gerado por
usuário, sem interação social e sem compras aleatórias.

| Pergunta | Resposta |
|---|---|
| Categoria do app | **Utilitário / Personalização** (não é jogo) |
| Violência, sangue, conteúdo sexual, linguagem imprópria, drogas | **Não** para todas |
| Jogos de azar / simulação de apostas | **Não** |
| Usuários interagem ou trocam conteúdo entre si | **Não** |
| Compartilha localização com outros usuários | **Não** — a localização vai só ao serviço de meteorologia, nunca a outro usuário |
| Permite compras digitais | **Sim** — Premium e cenários avulsos |
| Contém anúncios | **Não** — nenhum SDK de anúncios integrado |
| Conteúdo gerado por usuário | **Não** |

Resultado esperado: **Livre / L (todas as idades)** nas classificações
brasileira e internacional.

> O cenário de tanque foi retirado da versão inicial e está preservado entre os
> packs futuros. Se ele voltar ao aplicativo, refaça a classificação indicativa
> antes de publicar a atualização.

---

## Depois de enviar

- **Data Safety e política precisam concordar.** Se você marcar algo aqui que a
  política não menciona (ou o contrário), é rejeição. As duas fontes são este
  guia e `PRIVACIDADE.md`.
- **Toda mudança em `weather/`, `billing/` ou no manifesto** obriga a revisar os
  dois. Ver a tabela de rastreio no `CHECKLIST_PUBLICACAO.md`.
- Reenviar o formulário é gratuito e rápido — errar por omissão e corrigir é
  melhor que declarar a mais "por segurança".
