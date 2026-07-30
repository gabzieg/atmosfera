# Checklist de publicação — Google Play

> Ver também: [README.md](README.md) (visão geral + build de release).
>
> Este arquivo substitui o antigo `estrutura.md`, que era uma resposta
> genérica de IA sem nenhum dado real do projeto. Os itens abaixo são
> específicos do Atmosfera, levantados a partir do manifesto e do código atuais
> em `android-app/`.

## Permissões declaradas hoje (`AndroidManifest.xml`)

| Permissão | Usada onde | Justificativa pro Data Safety Form |
|---|---|---|
| `INTERNET` / `ACCESS_NETWORK_STATE` | `weather/WeatherRepository.kt` | Buscar o clima na Open-Meteo |
| `ACCESS_COARSE_LOCATION` / `ACCESS_FINE_LOCATION` | `weather/LocationHelper.kt` | O wallpaper reage ao clima da região do usuário — é a funcionalidade principal do app |
| `RECEIVE_BOOT_COMPLETED` | `weather/BootReceiver.kt` | Reagendar a atualização periódica de clima após reiniciar o aparelho |

Nenhuma outra permissão deveria existir — se aparecer uma nova no manifesto,
confirme que tem uso real antes de publicar (o Google compara o Data Safety
Form declarado com o que o APK realmente pede).

## Dados coletados

> O texto público disso está em [PRIVACIDADE.md](PRIVACIDADE.md) — que é o
> documento que vai valer juridicamente. O resumo abaixo é a versão interna;
> se um mudar, mude o outro (ver "Política de privacidade" no fim deste arquivo).

- **Localização aproximada/precisa**: usada só localmente para consultar a
  Open-Meteo e cacheada em `SharedPreferences` (`WeatherCache`) — não é
  enviada a nenhum servidor próprio (o projeto não tem backend).
- **Nenhuma conta de usuário, nenhum identificador pessoal coletado.**
- **Compras**: processadas pelo Google Play Billing — o app não vê nem
  armazena dados de pagamento, só o resultado da compra.
- **Anúncios**: `AdMobBannerPlaceholder` na UI é só um placeholder visual —
  **nenhum SDK de ads/analytics está integrado ainda**. No dia em que isso
  mudar, os itens abaixo (Data Safety Form, consentimento, política de
  privacidade) precisam ser atualizados antes do próximo release.

## Antes de publicar (fazer, não só declarar)

- [ ] **Keystore de release + `keystore.properties`** — gerar a keystore de
  produção com `keytool` e preencher `android-app/keystore.properties` (ver
  README.md → "Build de release"). Sem isso `assembleRelease` builda sem
  assinar. Guarde a keystore em local seguro fora do repo.
- [x] **Política de privacidade escrita** — [PRIVACIDADE.md](PRIVACIDADE.md)
  (texto canônico) + [`docs/privacidade/index.html`](docs/privacidade/index.html)
  (mesma coisa em HTML, pronta pra hospedar). Cobre coleta, finalidade, base
  legal LGPD, terceiros, transferência internacional, retenção, direitos do
  titular e contato.
- [ ] **Preencher os `[PREENCHER: …]`** da política (controlador, e-mail de
  contato, encarregado/DPO, URL, data de vigência) — ver "Política de
  privacidade" no fim deste arquivo. Sem isso ela não pode ser publicada.
- [ ] **Hospedar a política e ter a URL** pra colar no Play Console (Política do
  app → Privacidade) e na ficha da loja.
- [ ] **Link pra política dentro do app** (`ui/SettingsTab.kt`, seção "Sobre") —
  boa prática na Play e obrigatório se um dia houver versão iOS.
- [ ] **Data Safety Form** (Play Console) — declarar coleta de **localização
  precisa e aproximada** (o manifesto declara `ACCESS_FINE_LOCATION`, e o
  Google compara com o APK — declarar só "aproximada" é inconsistência, que é
  a causa nº 1 de rejeição). Finalidade "funcionalidade do app", **não**
  compartilhada com terceiros para publicidade, compartilhada com a Open-Meteo
  para a funcionalidade, criptografada em trânsito, coleta **opcional** (o app
  funciona sem permissão, com fallback pra Guarapuava/PR). Alternativa mais
  limpa: **remover `ACCESS_FINE_LOCATION` do manifesto** — o código só checa
  `ACCESS_COARSE_LOCATION` (`LocationHelper.hasPermission()`) e pede
  `PRIORITY_BALANCED_POWER_ACCURACY`, então a permissão fine não tem uso real.
  Isso mexe no manifesto → área de risco, exige PR (ver `.claude/skills/abrir-pr`).
- [ ] **Content Rating Questionnaire** (IARC) — preencher no Play Console.
- [ ] **Ficha da loja**: título, descrição, screenshots (usar o app real, não
  só o `wallpaper_thumbnail.png`), ícone.
- [ ] **Produtos no Play Console**: criar `atmosfera_premium` e
  `cenario_tanque` (INAPP, não-consumíveis) antes de testar compras — ver
  `Catalogo.kt` para os IDs valendo.
- [ ] **Chave de licenciamento**: colar a chave pública Base64 (Play Console →
  Monetizar → Configuração de monetização → Chave de licença) em
  `BillingManager.LICENSE_PUBLIC_KEY_BASE64` — sem ela, a verificação de
  assinatura das compras fica desligada.
- [ ] **Teste fechado** antes de produção — Google exige um período de teste
  fechado com testers reais para apps novos.

## Antes de cada release (recorrente)

- [ ] `./gradlew assembleDebug` limpo (mínimo — é o que a CI valida).
- [ ] Testar o fluxo completo num emulador/aparelho: permissão de localização,
  "Definir papel de parede", troca de cenário na Loja, restaurar compras.
- [ ] Conferir se alguma permissão nova foi introduzida sem necessidade.
- [ ] Se a versão do Billing Library mudar, checar a nota no
  `HANDOFF-FRONTEND.md` sobre o teto do Kotlin 1.9.23.
- [ ] Se houve mudança em `weather/`, `billing/`, no manifesto ou entrou um SDK
  novo: revisar [PRIVACIDADE.md](PRIVACIDADE.md) contra a tabela de rastreio
  abaixo, subir a versão da política e atualizar o Data Safety Form.

## Política de privacidade

### Placeholders a preencher antes de publicar

Aparecem nos dois arquivos (`PRIVACIDADE.md` e `docs/privacidade/index.html`,
onde estão destacados em amarelo):

| Placeholder | O que entra |
|---|---|
| Controlador | Nome completo do desenvolvedor (pessoa física) ou razão social + CNPJ, se for empresa |
| E-mail de contato | Caixa que alguém realmente lê — aparece em 4 lugares (seções 1, 10, 11 e 14). Prefira um endereço do produto (ex. `privacidade@…`) a um pessoal |
| Encarregado (DPO) | Nome + e-mail. Pode ser a mesma pessoa; a LGPD (art. 41) exige o canal, não um cargo dedicado |
| URL desta política | A URL pública onde a página for hospedada |
| Data de vigência / histórico | Data da primeira publicação na Play |

### Como hospedar

Duas opções, ambas dão a URL que o Play Console pede:

1. **GitHub Pages neste repo** (mais rápido): Settings → Pages → Source
   "Deploy from a branch", branch `main`, pasta `/docs`. A página sai em
   `https://<user>.github.io/<repo>/privacidade/`.
2. **No site de apresentação** (destino final): copiar
   `docs/privacidade/index.html` pro repo do site quando ele existir, servindo
   em `/privacidade`. Se a URL mudar depois de publicado, atualize o Play
   Console — a política precisa continuar acessível na URL declarada.

O HTML é autocontido (CSS inline, sem fontes/scripts externos), responsivo e
segue claro/escuro, então funciona em qualquer host estático.

### Rastreio código → política

Se qualquer linha da coluna da esquerda mudar, a política mente e precisa ser
corrigida no mesmo PR.

| Código | O que a política afirma |
|---|---|
| `AndroidManifest.xml` (permissões) | Seção 5 (tabela de permissões) e 3.1 |
| `LocationHelper.kt` — `PRIORITY_BALANCED_POWER_ACCURACY`, fallback Guarapuava | 3.1 ("precisão balanceada", "é opcional"), 10 |
| `WeatherRepository.kt` — base URL `api.open-meteo.com`, HTTPS, sem chave de API | 3.1, 6.1, 9 |
| `WeatherCache.kt` — TTL 30 min, delta ~5 km, só o registro mais recente | 3.1, 8 |
| `BootReceiver.kt` / `WeatherWorker` — período de 30 min | 3.1 ("verificação periódica") |
| `BillingManager.kt` — só resultado da compra + verificação de assinatura | 3.5, 9 |
| `Plano.kt`, `Cena.kt`, `Estilo.kt` — prefs locais | 3.4 |
| `AndroidManifest.xml` — `allowBackup="true"` | 3.6 |
| Ausência de SDK de ads/analytics | 4 e 12 — **integrar um SDK invalida a política** |

Nota de debug (não vai na política pública, porque não vale pro APK publicado):
`WeatherRepository` liga o `HttpLoggingInterceptor` em nível `BASIC` só quando
`BuildConfig.DEBUG`, o que escreve lat/lon no logcat. Em release fica `NONE` —
se isso mudar, a seção 3.7 deixa de ser verdade.

### Revisão jurídica

O texto foi escrito a partir do comportamento real do código e cobre o que a
LGPD e a Play exigem, mas **não é parecer jurídico**. Antes de publicar, vale a
leitura de um advogado — principalmente sobre a identificação do controlador
(pessoa física vs. empresa) e a base legal escolhida para cada dado
(consentimento para localização, legítimo interesse para IP e backup).
