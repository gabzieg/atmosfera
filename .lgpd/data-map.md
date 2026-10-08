# Mapa de dados — Terra - Live Wallpaper

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **Código de referência**: `main` @ `2d01d16` (app 1.0.5, versionCode 6)
**Método**: engenharia reversa do código (skill `lgpd-data-mapping`, fluxo "legacy").
**Limite**: verificação estática. Não comprova o conteúdo do AAB no Console, a configuração do Play Console nem a infraestrutura do bucket.

Controlador: **Rafael Huppes (pessoa física)** · Canal: suporteterrabr@gmail.com · Operações no Brasil.

## Resumo do que o app faz com dado pessoal

- **Não há** conta, login, backend de aplicação, anúncios, analytics, crash reporting nem identificador de publicidade.
- **Hosts contatados** em produção (varredura de URLs em `android-app/app/src/main/java`): somente `api.met.no` (clima). O host do acervo (`Acervo.BASE_PADRAO`) está **vazio** em `engine/Acervo.kt:86` — nenhum download remoto na 1.0.5 além do painel de debug.
- **Permissões do manifesto do app**: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_COARSE_LOCATION`. O manifesto *mesclado* do release também traz `WAKE_LOCK`, `FOREGROUND_SERVICE` e `RECEIVE_BOOT_COMPLETED`, vindos da biblioteca WorkManager (usada só para cancelar tarefas legadas — `weather/WeatherWorker.kt`).
- **Dado sensível (Art. 5º, II)**: nenhum. Localização aproximada não é dado sensível.
- **Menores**: público declarado 13+ ⇒ adolescentes 13–17 podem ser titulares. Ver [eca-digital.md](eca-digital.md).

## Atividades

### A001 — Localização aproximada para o clima

| Campo | Valor |
|---|---|
| Finalidade | Escolher o clima da região do usuário e desenhar a cena (função central do produto) |
| Dados | Latitude/longitude aproximadas (`PRIORITY_BALANCED_POWER_ACCURACY`, `weather/LocationHelper.kt`), indicador "local padrão" |
| Titulares | Usuários (adultos e adolescentes 13–17) |
| Fonte | Coletado do aparelho via Google Play Services, com permissão `ACCESS_COARSE_LOCATION` |
| Quando | Só com o wallpaper visível na home, aparelho acordado e desbloqueado (`weather/ClimaNaHome.kt`, PR #46). App aberto, bloqueio, tela apagada e prévias **não** consultam. |
| Onde fica | `SharedPreferences` privado `atmosfera_weather_cache` (`cached_lat`, `cached_lon`, `cached_lugar`, `cached_local_padrao`, último clima) — só o registro mais recente |
| Para onde vai | MET Norway, coordenadas arredondadas a 2 casas (~1 km) — `WeatherRepository.coordenada()` |
| Opcional | Sim. Sem permissão: cidade padrão São Paulo (SP) (`LocationHelper.DEFAULT_LAT`) |
| Retenção local | Sobrescrito a cada consulta; apagado ao limpar dados/desinstalar. **Pode ir ao backup do Android** (ver A006). |
| Alto risco? | Não (ver [RIPD/dispensa-2026-10-08.md](RIPD/dispensa-2026-10-08.md)) |
| Operador/destinatário | MET Norway ([vendors/met-norway.md](vendors/met-norway.md)) |
| Transferência internacional | Sim — Noruega ([transfers/README.md](transfers/README.md)) |

### A002 — Exposição do IP e da identificação do app nas requisições de rede

| Campo | Valor |
|---|---|
| O que é | Todo pedido HTTPS expõe o IP do aparelho ao servidor de destino. À MET vai também o `User-Agent` `Terra/<versão> (com.terra.wallpaper; suporteterrabr@gmail.com)` (exigência dos termos deles). |
| Tratado pelo Terra? | **Não.** O Terra não recebe nem registra o IP (sem backend). Quem registra é o destinatário (MET até 90 dias, segundo a política dele). |
| Por que consta | Transparência (Art. 6º, VI). É dado do titular que sai do aparelho. |

### A003 — Preferências e indicadores locais

| Campo | Valor |
|---|---|
| Dados | Cenário, arte, estilo, personalização (brilho), intervalo de clima, flag Premium (`atmosfera_plano`), cenário atual (`atmosfera_cena`), freio da MET (`atmosfera_met_freio`) |
| Onde | Armazenamento privado do app. Nenhum é enviado a terceiros pelo app. |
| Observação | Dado de configuração do aparelho; não identifica a pessoa por si. Mantido no inventário por prudência. |

### A004 — Compras (Google Play Billing)

| Campo | Valor |
|---|---|
| Fluxo | Compra e restauração inteiramente pelo Google. O app recebe estado da compra, ID do produto e o recibo assinado, valida a assinatura localmente (`billing/AssinaturaCompra.kt`, chave pública no build) e guarda um flag local. |
| Dados de pagamento | Nunca chegam ao app. |
| Backend | Não há. Sem validação no servidor. |
| Operador/controlador | Google LLC (Play) — [vendors/google.md](vendors/google.md) |

### A005 — Atendimento por e-mail e "Reportar problema"

| Campo | Valor |
|---|---|
| Fluxo | Ajustes → "Reportar problema" abre o cliente de e-mail do usuário (`ACTION_SENDTO mailto:`) com um relato pré-preenchido: **versão do app, fabricante/modelo do aparelho, versão do Android, cenário/arte/estilo e o sintoma escolhido** (`ui/components/ReportarProblemaDialog.kt`). O usuário revisa e envia por conta própria. O app não envia nada sozinho. |
| Dados recebidos | E-mail e nome do remetente, texto livre, anexos voluntários, e os campos acima. |
| Destinatário no código | **e-mail pessoal do Rafael**, não o canal oficial (`Suporte.EMAIL`) — **lacuna G05**, ver [gaps.md](gaps.md). |
| Provedor | Gmail (Google). |
| Retenção | Proposta: até 60 dias após resolver, salvo obrigação legal/defesa — ver [retention.md](retention.md). **[DECISÃO]** |
| Descrita na política vigente? | **Não** (v1.4 não tem seção de atendimento; ela diz que o app não coleta e-mail) — **lacuna G06**. |

### A006 — Backup do Android

| Campo | Valor |
|---|---|
| Configuração | `allowBackup="true"` (`AndroidManifest.xml:20`). Regras (`res/xml/backup_rules.xml`, `data_extraction_rules.xml`) excluem **apenas** `atmosfera_plano.xml`. |
| Consequência | `atmosfera_weather_cache.xml` (que contém lat/lon) e as demais preferências podem entrar no backup do Google do usuário e em transferência entre aparelhos. |
| Responsável | Mecanismo do sistema operacional, na conta Google do usuário; o Terra não acessa. |
| Recomendação | Excluir `atmosfera_weather_cache.xml` e `atmosfera_met_freio.xml` das duas regras — **lacuna G08** (mudança de 2 linhas). |

### A007 — Download de conteúdo (acervo R2) — **planejado, inativo na 1.0.5**

| Campo | Valor |
|---|---|
| Estado | Código existe (`engine/Acervo.kt`), URL base vazia; só o painel de debug baixa. Bucket de teste existe (r2.dev), sem URL de produção. |
| Quando ativar | Esta atividade só passa a valer na versão que habilitar a Loja de packs. A política v1.4 já a descreve (§3.8, §6.5) — **descreve algo que a 1.0.5 ainda não faz**. |
| Dados | Pedido HTTP do arquivo + IP (inerente). Sem localização. |
| Operador | Cloudflare — [vendors/cloudflare-r2.md](vendors/cloudflare-r2.md) |

### A008 — Site de apresentação (landing) — **indefinida**

Domínio e hospedagem ainda não decididos (Willian). Não há analytics nos arquivos examinados do repositório do site. Registrar fornecedor, logs e cookies **quando houver implantação**. Não inferir ausência de logs pela ausência de analytics.

## Código morto com relevância de privacidade

- `LocationHelper.nomeDoLugar()` (Geocoder do Android) **não é chamado em nenhum lugar** desde o PR #46. Se for reativado, o Geocoder pode usar um provedor de rede do aparelho e **enviar as coordenadas a ele** — a política teria de dizer isso (hoje afirma que as coordenadas vão "apenas" à MET). Recomendação: remover o método ou documentar.
- `Log.d("Localização obtida: lat, lon")` em `LocationHelper.kt:72` permanece no release (não há regra R8 de remoção). Fica só no log do aparelho; ainda assim, **lacuna G09**.

## Checklist de qualidade (skill)

- [x] Toda integração de terceiro em produção listada (MET; Google Play Services/Billing/Backup; Gmail no atendimento)
- [x] Atividade planejada separada da ativa (A007)
- [x] Atividades com menores sinalizadas
- [x] Retenção definida (sem "indefinido") — A005 pendente de **[DECISÃO]**
- [ ] A008 depende de decisão de hospedagem
