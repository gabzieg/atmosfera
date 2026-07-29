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
- [ ] **Política de privacidade** publicada e acessível por URL — obrigatória
  por causa da coleta de localização. Precisa cobrir: que dado é coletado
  (localização aproximada), pra que (clima do wallpaper), que não é
  compartilhado com terceiros hoje, e como contatar o desenvolvedor.
- [ ] **Data Safety Form** (Play Console) — declarar coleta de localização
  aproximada, finalidade "funcionalidade do app", não compartilhada com
  terceiros, coleta opcional (o app funciona sem permissão, com fallback pra
  Guarapuava/PR).
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
