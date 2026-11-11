# Ficha da Play Store — candidato 8

Proposta editorial de 11/11/2026, para revisão; não salva no Console.
App `e82a0c66e8a747b019fa67cac600b3738610eb8c`, versão 1.0.5/código 8.
Escopo e limites: [#88/6804730825](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6804730825).

## Título — decisão preservada

```text
Terra - Live Wallpaper
```

22 caracteres; limite 30. Nome decidido em 28/08/2026, preservado sem nova escolha.

## Descrição curta

**Decisão anterior do dono, 28/08/2026, ainda não substituída:**

```text
Papel de parede animado: quando chove lá fora, chove na sua tela.
```

65 caracteres; limite 80. Não usar a proposta abaixo como se já estivesse aprovada.

**Proposta sujeita a aceite editorial:**

```text
Papel de parede animado com cenas, clima e ciclo de dia e noite.
```

64 caracteres; limite 80. Evita promessa de coincidência instantânea entre
chuva local e wallpaper, especialmente com cache, conexão ausente ou local padrão.
As justificativas e alternativas do dono foram preservadas no
[histórico integral](history/listing-before-candidate-8.md).

## Descrição longa — proposta para revisão

```text
Terra é um papel de parede animado com cenas que acompanham o clima e o ciclo de dia e noite.

Escolha um cenário e sua arte, veja a prévia e confirme a aplicação no seletor de papel de parede do Android. O catálogo reúne diferentes cenários e estilos visuais. Há amostras gratuitas em Cabana na floresta, Casa da bruxa, Campo de lavanda, Esfinge e Jardim.

O clima pode trazer chuva, neve, névoa e nuvens à cena. A atualização ocorre com o wallpaper ativo na tela inicial e depende de conexão. Sem conexão, o clima salvo continua disponível. A localização aproximada é opcional: sem permissão, as próximas consultas usam São Paulo, SP. O dado salvo pode ser de outra região e sua procedência aparece no app.

Premium é uma compra única para efeitos e estilos de efeito. As artes pagas são compras individuais e não estão incluídas no Premium. Os preços e a disponibilidade dos produtos aparecem pela Google Play. O download das artes remotas depende de conexão e de acesso à arte.

O app não exige cadastro próprio. Documentos de Privacidade, Termos e Contato ficam disponíveis no app, inclusive offline. Requer Android 8.0 ou mais recente.
```

1141 caracteres, incluindo quebras de linha LF; limite 4.000.
Sem preço fixo, garantia de acesso pago ao revisor ou promessa de toda arte incluída no Premium.
O nome “Jardim” se refere à amostra do catálogo; não representa catálogo limitado a cinco cenas.

## Acesso ao app — texto efetivo em inglês

Decisão [4.3 de 10/11](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6774962413):
acesso padrão sem login próprio; gratuito funcional; compras no Billing padrão.
Texto pronto para revisão e posterior cadastro autorizado, não acesso irrestrito demonstrado:

```text
Terra does not require an in-app account or login. Open the app, complete onboarding and open Loja (Store). Free sample artwork is available in Cabana na floresta, Casa da bruxa, Campo de lavanda, Esfinge and Jardim. Select artwork marked grátis (free), then use Aplicar or Definir wallpaper and confirm in the Android wallpaper picker. Location permission is optional: without it, future weather requests use São Paulo, Brazil; saved weather may be displayed until a new update is available. Weather requests run while the wallpaper is active on the home screen.

Premium effects and effect styles are a one-time Google Play Billing purchase. Paid artwork is sold separately and is not included in Premium. Paid features use the standard Google Play purchase flow; there is no separate app login or access code. Restoring purchases restores valid existing entitlements and does not grant new purchases. This build does not provide an unrestricted reviewer account or a special unlock mechanism. If paid products are unavailable in the current environment, the free samples remain usable.
```

1088 caracteres, incluindo LF. Limite específico do campo e fragmentação em
instruções devem ser conferidos no cadastro autorizado; nenhum limite foi inventado.
Amostras grátis foram exercitadas no ensaio; Premium, compras e restauração não foram.
Se Google exigir outro método, mantém-se o risco aceito de mudança do APK/código 9.
Não oferecer conta/senha/cupom/licença ou destrave secreto por inferência.

## Evidência e limites da redação

| Afirmação | Fonte no candidato | Limite |
|---|---|---|
| Artes gratuitas separadas do Premium | Catalogo, MainViewModel e ensaio e82a0c6 | Uma amostra aplicada; não atestar todas as artes |
| Premium e artes em compras únicas separadas | BillingManager/Plano/ComprasArte | Preços e disponibilidade dependem Play; compra não ensaiada |
| Clima/cache e localização opcional | LocationHelper/serviço, HomeTab e ensaio | Sem cache e nova região não exercitados no aparelho |
| Consultas na home ativa | AtmosferaWallpaperService | Sem garantia de atualização instantânea |
| Documentos offline | Assets e ensaio | Não significa Console atualizado ou auditoria jurídica nova |
| Android 8.0+ | minSdk 26 | Compatibilidade em todos os aparelhos não demonstrada |

Retiradas do rascunho anterior: internet “só” para clima, ausência universal de
rastreamento, venda por cenário inteiro e garantia de wallpaper na tela de bloqueio.
Nenhuma mudança de oferta, preço, contato, app ou documentos legais nesta proposta.
Público adulto/Brasil do teste e obrigações comerciais futuras mantêm suas decisões próprias.

## Material visual e publicação

[README](README.md) identifica os recursos atuais e o histórico.
As propostas visuais usam somente capturas/arte do candidato identificado, sem
fabricar partes da UI ou demonstrar recursos pagos não ensaiados.
SVG é fonte editável de revisão, não arquivo aceito para upload na Play.
Exportações JPEG acompanham as fontes. Aceite editorial/visual e cadastro externo continuam pendentes.
