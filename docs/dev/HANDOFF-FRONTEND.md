# Atmosfera — Handoff do FRONT (motor congelado)

> Ver também: [README.md](../../README.md) (visão geral + como rodar) ·
> [CLAUDE.md](../../CLAUDE.md) (contexto/comandos para o Claude Code).

> **Para quem recebe este documento:** você vai tocar o **front** do app Atmosfera em
> paralelo, enquanto o **motor de efeitos** (a parte que desenha o wallpaper) continua
> sendo evoluído por outra frente. Este documento é o seu ponto de partida: explica o
> produto, a arquitetura, **a fronteira do que é seu e do que é congelado**, e a
> interface estável entre os dois. Leia inteiro antes de codar.

---

## 1. O que é o Atmosfera

Live wallpaper para Android que **reage ao clima real** do lugar do usuário
(Open-Meteo), à **hora do dia**, à **temperatura** e ao **vento**. A cena é uma
arte pintada (fundo fixo) e todos os efeitos (chuva, neve, nuvens, sol/lua,
estrelas, névoa, vento, poças, raios…) são desenhados por cima com sprites, em
tempo real (~30 fps, Canvas nativo).

**Visão de produto:**
- Vários **cenários** (wallpapers): hoje uma *cabana na floresta* (grátis) e um
  *tanque em campo de batalha* (em produção). No futuro, vários estilos de arte
  (pixel art, pintura a óleo, clay/Wallace&Gromit…), com efeitos mixáveis.
- **Monetização (decidida): SEM assinatura.**
  - **Premium** = **compra única global**. Destrava os efeitos "vivos" em TODOS os
    cenários (raios, vento, vagalumes, estrela cadente, lampiões, fumaça, fases da
    lua, acúmulo de neve, etc.). SKU: `atmosfera_premium` (INAPP não-consumível).
  - Cada **cenário extra** = **compra avulsa** ("básico"). Quem tem Premium recebe
    a versão "viva" do cenário automaticamente.
  - A **cabana é grátis** (versão lite).

---

## 2. A DIVISÃO — o que é seu, o que é congelado

```
┌─────────────────────────────────────────────────────────────┐
│  MOTOR (CONGELADO — não editar)          FRONT (SEU)         │
│  ─────────────────────────────           ───────────         │
│  com.atmosfera.wallpaper.engine.*        ui.*  (telas)       │
│  assets/atmosfera/**  (arte/sprites)     billing.*  (loja)   │
│                                          service.*  (a cola) │
│  Desenha o wallpaper.                    weather.*  (clima)  │
│  Evolui do NOSSO lado; você             Publicação na Play  │
│  recebe snapshots novos.                 Onboarding/permissões│
└─────────────────────────────────────────────────────────────┘
```

- **MOTOR (congelado, NÃO mexer):** pacote `com.atmosfera.wallpaper.engine`
  (`EffectEngine`, `SceneConfig`/`SceneState`, `Atlas`, `Particles`, `Catalogo`,
  `Cena`) **e** a pasta `app/src/main/assets/atmosfera/**` (fundos, frentes, zonas,
  sprites de cada cenário). É o coração do render. Nós evoluímos isso (novos
  cenários, novos efeitos) e te entregamos snapshots. **Se você editar aqui, dá
  conflito no merge.**
- **FRONT (seu):** todo o resto —
  - `com.atmosfera.wallpaper.ui.*` — telas do app companheiro (home, loja,
    onboarding, settings).
  - `com.atmosfera.wallpaper.billing.*` — `Plano` (flag Premium) + `BillingManager`
    (Google Play Billing). **Estender** para compras avulsas de cenário.
  - `com.atmosfera.wallpaper.service.*` — o `WallpaperService` (a "cola" que
    hospeda o motor). Você pode editar; só respeite a interface do motor (seção 3).
  - `com.atmosfera.wallpaper.weather.*` — localização + Open-Meteo + cache. Já
    existe e funciona; alimenta o motor.
  - Manifest, Gradle, ícones, ficha da Play Store, screenshots, teste fechado.
  - `com.atmosfera.wallpaper.debug.*` — painel de teste; é NOSSO (para calibrar
    efeitos), mas pode ler para entender como forçar clima.

**Merge depois:** como as fronteiras são por pacote/pasta, o merge é limpo se você
não tocar em `engine/**` nem `assets/atmosfera/**`. Quando entregarmos um motor
novo, você só substitui esses dois caminhos.

---

## 3. A INTERFACE CONGELADA (como o front fala com o motor)

Todo o contato passa por **poucos pontos estáveis**. Assinaturas que NÃO vão mudar:

### 3.1 Render — `EffectEngine`
```kotlin
class EffectEngine(val estado: SceneState = SceneState()) {
    var pronto: Boolean          // true depois de carregar()
    fun carregar(assets: AssetManager)   // decodifica os bitmaps do cenário ativo
    fun draw(canvas: Canvas, cw: Float, ch: Float, tsMs: Long)  // 1 frame
    fun aoMudarClima()           // chamar quando o SceneState mudou de clima
    fun liberar()                // recicla bitmaps (onDestroy)
}
```
> O `WallpaperService` cria um `EffectEngine`, chama `carregar()` uma vez, e num
> loop de ~33 ms faz `lockHardwareCanvas()` → `draw(...)` → `unlockCanvasAndPost()`.
> Já está implementado em `AtmosferaWallpaperService`; use como referência.

### 3.2 Estado que dirige o motor — `SceneState`
O motor lê um `SceneState`. Você o preenche a partir do **clima real** + **plano**:
```kotlin
SceneState.aplicarClima(estado: SceneState, w: WeatherState, premium: Boolean)
SceneState.horaAtual(): Float            // hora do relógio como fração (14.5 = 14h30)
```
Campos que você pode setar direto se precisar (o resto o `aplicarClima` cuida):
`hora`, `temp`, `vento`, `clima` ("chuva"|"nublado"|"seco"), `premium`.

### 3.3 Clima — `WeatherState` (pacote weather, você já tem)
`condition` (enum WMO→cena), `period`, `temperatureCelsius`, `windspeedKmh`,
`sunriseHour`, `sunsetHour`, `weatherCode`. Vem do `WeatherRepository`.

### 3.4 Plano Premium — `Plano`
```kotlin
Plano.isPremium(context): Boolean
Plano.setPremium(context, valor: Boolean)   // chamar após confirmar compra
```

### 3.5 Cenário ativo + catálogo — `Catalogo` / `Cena`  ← **use isto para a LOJA**
```kotlin
// lista dos wallpapers (você monta a loja/seletor a partir daqui)
Catalogo.cenarios: List<Cenario>       // id, nome, descricao, gratis, productId
Catalogo.por(id): Cenario?
Catalogo.padrao: Cenario               // cabana

// qual cenário está ativo (você grava quando o usuário troca)
Cena.atual(context): String            // id do cenário
Cena.definir(context, id: String)      // troca o wallpaper ativo
```
> **Fluxo da loja:** liste `Catalogo.cenarios`. Grátis (`gratis==true`) = aplicar
> direto. Pago = comprar `productId` via Billing, e só então `Cena.definir(...)`.
> Premium (`atmosfera_premium`) é global e destrava os efeitos vivos de todos.
>
> **Estado atual do motor:** hoje o serviço ainda carrega só a *cabana*. A troca de
> cenário no motor (ler `Cena.atual` e carregar os assets certos) chega no próximo
> snapshot nosso. **Pode construir a loja/seletor já contra `Catalogo`/`Cena`** — a
> interface não muda; quando o motor novo entrar, a troca passa a funcionar sozinha.

---

## 4. ESCOPO DO FRONT (suas tarefas)

1. **Loja / catálogo de cenários** — tela que lista `Catalogo.cenarios`, mostra
   grátis vs pago (preço via Billing), permite comprar e **aplicar** (`Cena.definir`).
2. **Billing** — estender `BillingManager` para os produtos avulsos de cenário
   (`productId` do `Catalogo`), além do `atmosfera_premium` que já existe. Ao
   confirmar compra: liberar o cenário; em Premium: `Plano.setPremium(true)`.
3. **Seletor de cenário** na home + preview.
4. **Onboarding / permissões** — fluxo de permissão de localização e o "definir como
   wallpaper" (`WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`, já tem exemplo em
   `MainActivity`).
5. **Home / companion app** — clima atual, status do plano, atalhos.
6. **Settings** (opcional) — unidades, etc.
7. **Publicação Play Store** — ficha, screenshots, criar produtos no Play Console
   (`atmosfera_premium` + `cenario_tanque`), teste fechado → revisão.

**Não faça** (é nosso): mexer no render, criar/editar sprites e artes, tunar
efeitos, mudar as assinaturas da seção 3.

---

## 5. Estado atual do código

- **Cabana**: publicável, roda no aparelho (build verde na CI). Tier grátis mostra
  clima real + dia/noite + estrelas + janelas acesas + neve caindo + lua cheia;
  Premium liga os efeitos vivos.
- **Tanque**: cenário #2, mapeado e funcionando no **protótipo web** (nosso
  laboratório). Porte pro motor Android vem por snapshot.
- **Billing/Premium**: `Plano` + `BillingManager` (Play Billing 6.2.1, produto
  `atmosfera_premium`) já existem.
- **Clima**: `weather.*` (Open-Meteo, localização, cache 30 min) funcionando.
- **CI**: GitHub Actions (`.github/workflows/build.yml`) compila `assembleDebug` e
  publica o APK como artifact. Use para validar (não há emulador do nosso lado).

## 6. Stack técnica

Kotlin 1.9.23 · AGP 8.3.0 · minSdk 26 · JDK 17 · Gradle (setup-gradle 8.6) ·
Google Play Billing 6.2.1 · Gson (pacote weather) · `buildConfig true`.
Coordenadas de cena em espaço lógico (a cabana 688×1538; o tanque 688×1536) —
o motor faz o "cover" para a tela; **o front não precisa saber disso**.

> ⚠️ **Billing preso em 6.2.1 de propósito:** 7.0.0+ é compilado com metadata do
> Kotlin 2.x, incompatível com o compilador Kotlin 1.9.23 deste projeto (erro
> real de build, confirmado rodando `gradlew assembleDebug`, não suposição). Só
> suba a versão do Billing junto com uma atualização do plugin Kotlin — os dois
> andam juntos.

## 7. Como trabalhar sem colisão

- Trabalhe numa **branch/fork** própria. Não edite `engine/**` nem `assets/atmosfera/**`.
- Programe **contra a seção 3** (as assinaturas são estáveis).
- Quando entregarmos um motor novo, é só substituir `engine/**` + `assets/atmosfera/**`
  e recompilar — sua parte continua igual.
- Dúvida sobre a interface? Pergunte antes de contornar por dentro do motor.
