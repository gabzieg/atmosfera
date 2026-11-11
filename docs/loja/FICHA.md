# Ficha da Play Store — candidato 8

Pacote editorial dos PRs #119/#120 aprovado em 11/11/2026. Estado do cadastro
registrado pelos recibos citados abaixo; esta atualização não opera o Console.
Decisão e limites: [#88/6807363318](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6807363318).
App `e82a0c66e8a747b019fa67cac600b3738610eb8c`, versão 1.0.5/código 8.
Escopo e limites: [#88/6804730825](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6804730825).

## Título — decisão preservada

```text
Terra - Live Wallpaper
```

22 caracteres; limite 30. Nome decidido em 28/08/2026, preservado sem nova escolha.

## Descrição curta

**Histórico: decisão do dono em 28/08/2026, substituída editorialmente em 11/11/2026:**

```text
Papel de parede animado: quando chove lá fora, chove na sua tela.
```

65 caracteres; limite 80. Mantida como histórico, não como texto para o próximo cadastro.

**Descrição aprovada para o pacote final:**

```text
Papel de parede animado com cenas, clima e ciclo de dia e noite.
```

64 caracteres; limite 80. Evita promessa de coincidência instantânea entre
chuva local e wallpaper, especialmente com cache, conexão ausente ou local padrão.
As justificativas e alternativas do dono foram preservadas no
[histórico integral](history/listing-before-candidate-8.md).

## Descrição longa — pacote aprovado com complemento para revisão

```text
Terra é um papel de parede animado com cenas que acompanham o clima e o ciclo de dia e noite.

Escolha um cenário e sua arte, veja a prévia e confirme a aplicação no seletor de papel de parede do Android. O catálogo reúne diferentes cenários e estilos visuais. Há amostras gratuitas em Cabana na floresta, Casa da bruxa, Campo de lavanda, Esfinge e Jardim.

O clima pode trazer chuva, neve, névoa e nuvens à cena. A atualização ocorre com o wallpaper ativo na tela inicial e depende de conexão. Sem conexão, o clima salvo continua disponível. A localização aproximada é opcional: sem permissão, as próximas consultas usam São Paulo, SP. O dado salvo pode ser de outra região e sua procedência aparece no app.

Premium é uma compra única para efeitos e estilos de efeito. As artes pagas são compras individuais e não estão incluídas no Premium. Os preços e a disponibilidade dos produtos aparecem pela Google Play. O download das artes remotas depende de conexão e de acesso à arte.

O app não exige cadastro próprio. Documentos de Privacidade, Termos e Contato ficam disponíveis no app, inclusive offline. Requer Android 8.0 ou mais recente.

Suporte oficial: suporteterrabr@gmail.com
Para restaurar compras existentes, use a mesma conta Google utilizada na compra.
```

1265 caracteres, incluindo quebras de linha LF; limite 4.000.
As duas linhas finais são redação equivalente proposta para revisão; não são
transcrição literal do Console, cujo texto exato não foi consultado nesta tarefa.
O canal é o suporte público já aprovado, não endereço pessoal do perfil.
A restauração se limita a direitos existentes, sem garantir recuperação irrestrita.

Sem preço fixo, garantia de acesso pago ao revisor ou promessa de toda arte incluída no Premium.
O nome “Jardim” se refere à amostra do catálogo; não representa catálogo limitado a cinco cenas.

## Acesso ao app — literal efetivamente salvo em inglês

Registro de 11/11/2026, conforme [recibo da Friday](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6808816898)
e [conferência independente da contagem](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6808828316).
Esta atualização documental reproduz o recibo, sem nova leitura ou gravação do Console:

```text
No in-app account or login is required. Open the app, finish onboarding and go to Loja (Store). Free sample artwork (marked grátis) is available in Cabana na floresta, Casa da bruxa, Campo de lavanda, Esfinge and Jardim; use Aplicar or Definir wallpaper and confirm in the Android picker. Location is optional (default: São Paulo). Premium effects and paid artwork use standard Google Play Billing; there is no reviewer account, access code or unlock. Free samples remain usable without purchases.
```

**497 caracteres / 499 bytes UTF-8**, sem LF final; com uma LF final, 498 caracteres /
500 bytes. Cabe no limite observado de 500 caracteres do campo “outras informações”.
As cercas Markdown e a quebra que encerra o bloco não integram o parágrafo a colar.

**Decisão final do dono em 11/11/2026:** [“É sim, e acabou.”](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6809152318)
Manter o estado relatado: “Alguma parte restrita?” = Sim, entrada acima, caixa marcada
e registro “Conta de administrador” intocado. Assunto encerrado nesse escopo; não
reabrir a escolha, iniciar consulta ou implementar método/código 9 por iniciativa da sessão.
A decisão preserva o acesso padrão sem login próprio, gratuito funcional e Billing padrão;
não demonstra acesso pago irrestrito, compra/restauração testadas ou aprovação do Google.
Não oferece conta, senha, cupom, licença ou destrave especial por inferência.

**Histórico preservado, não substituir o literal salvo por proposta anterior:**

- Bloco de 1.088 caracteres do PR #119, mantido no [registro versionado anterior](https://github.com/gabzieg/atmosfera/blob/69add300040be7ea41eac6ab2ccbb1015c849704/docs/loja/FICHA.md):
  inadequado para colar integralmente nesse campo de 500; não era transcrição do Console.
- [Proposta de 459 caracteres](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6808577134):
  redação distinta; não foi o texto efetivamente salvo. A orientação de restauração permanece
  na descrição longa; sua ausência no literal 497 não cria promessa nova.
- [Relato do cancelamento e estado então incerto](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6808565796):
  não se atribui causa ou perda definitiva por aproximação; o recibo posterior e a decisão
  final acima identificam o estado a preservar.

Amostras grátis foram exercitadas no ensaio; Premium, compras e restauração não foram.
Salvar instruções ou encerrar sua decisão não autoriza envio, publicação ou distribuição.

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
Nenhuma mudança de oferta, preço, app ou documentos legais neste ajuste.
O canal de suporte permanece o aprovado; o delta de contato do perfil está abaixo.
Público adulto/Brasil do teste e obrigações comerciais futuras mantêm suas decisões próprias.

## Material visual e publicação

[README](README.md) identifica os recursos atuais e o histórico.
As propostas visuais usam somente capturas/arte do candidato identificado, sem
fabricar partes da UI ou demonstrar recursos pagos não ensaiados.
SVG é fonte editável de revisão, não arquivo aceito para upload na Play.
Exportações JPEG acompanham as fontes. Pacote final revisado no PR #120;
[editorial salvo como rascunho](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6807783324),
sem equivaler a publicação. O acesso tem seu recibo e decisão final acima.

## Deltas aprovados para o pacote final 4.10

- Curta e longa do PR #119, com as duas linhas finais de suporte/restauração acima.
- Substituir os oito screenshots antigos pelos três JPEG revisados, na ordem do README;
  substituir o banner pelo feature graphic Terra do mesmo pacote. Fontes/imagens preservadas.
- Contato público do desenvolvedor: substituir o endereço pessoal pelo canal oficial
  de suporte já aprovado, conforme [decisão #88/6807347126](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6807347126).
  Conferir o campo público correto; não alterar login, recuperação ou pagamentos.
- Título e ícone sem troca; nenhum outro campo recebe aprovação por inferência.

O subconjunto editorial recebeu [autorização específica de salvamento](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6807653860);
[contato público executado](https://github.com/gabzieg/atmosfera/issues/88#issuecomment-6808684875)
e instruções registradas acima não encerram todas as dependências de 4.10.
A auditoria local 5.1–5.3 foi [aceita pela lead](https://github.com/gabzieg/atmosfera/issues/86#issuecomment-6810026542);
não demonstra compra/instalação/entrega HD ou aprovação Google. Envio/revisão/distribuição
5.4–5.5 continuam separados e exigem autorização específica.
