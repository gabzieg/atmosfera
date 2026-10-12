> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

# Brief — Atmosfera: onboarding e venda do Premium

> Escopo deliberadamente estreito: **duas telas que não existem**. O resto do app
> já funciona e fica fora deste pedido.

## O produto

Live wallpaper Android. Desenha uma cena pintada e **viva** que reage ao clima
real da região do usuário — chuva, neve, névoa, vento, sol e lua, ciclo de dia e
noite. Não é galeria de imagem 4K: é ambiente contínuo, estética aconchegante.

Já existem 3 telas (Início · Loja · Ajustes) e elas funcionam. O que falta são
as duas abaixo.

## Restrições duras

- **Interface estritamente monocromática** — preto, cinzas, branco. Sem matiz.
  Hierarquia por contraste, peso e opacidade. Estado ativo = **inversão**
  (preenchimento claro sobre fundo escuro), nunca cor de acento.
  **A única cor da tela é a arte do cenário.** É a decisão visual central: um
  app colorido mataria o destaque das cenas.
- Tema **sempre escuro**.
- Português do Brasil.
- Android / Jetpack Compose. **Coluna única**, largura máxima 600dp
  centralizada (precisa continuar legível em tablet).
- **Sem padrão escuro**: nada de urgência falsa, contagem regressiva ou botão
  de recusa escondido. O app já trata indisponibilidade com honestidade e isso
  é um ativo, não um detalhe.

---

## Tela 1 — Onboarding (primeira abertura)

**Problema a resolver.** A maior causa de desinstalação em live wallpaper: o
usuário instala, não entende que precisa **aplicar** o wallpaper, conclui que o
app não faz nada e remove. Hoje o primeiro contato do Terra é um pedido de
permissão de localização — pede antes de mostrar qualquer valor.

**O que precisa acontecer, nesta ordem:**

1. Mostrar o que o app é — de preferência com a **cena rodando**, não com
   ilustração explicativa. O motor já desenha ao vivo; é o melhor argumento
   disponível e não custa nada usá-lo.
2. Levar o usuário a **aplicar como papel de parede**.
3. **Só então** pedir a permissão de localização, explicando o ganho concreto:
   sem ela a cena funciona, mas usa uma cidade padrão em vez do clima real.

**Detalhe que o desenho precisa absorver.** O passo 2 termina fora do app: o
Android abre a própria tela de seleção, com "Preview" e "Set wallpaper", e
depois pergunta "Tela inicial" ou "Tela inicial e de bloqueio". Essa passagem
confunde. O onboarding deve **preparar** o usuário para ela — dizer o que vai
aparecer e o que escolher — em vez de largá-lo lá.

**Matéria-prima disponível:** cena ao vivo com o clima real; 2 cenários; 3 artes
de fundo (Pixel Art, Clay, Aquarela); 4 estilos de efeito.

---

## Tela 2 — Venda do Premium

**Problema a resolver.** Hoje é um parágrafo de texto listando efeitos:
*"raios, vento, vagalumes, fumaça, fases da lua e acúmulo de neve"*. É o momento
de conversão inteiro do app, resolvido em prosa — quando existe um motor capaz
de **mostrar** cada um deles em movimento.

**O que o desenho precisa entregar:** uma comparação **antes/depois** entre a
cena sem e com os efeitos vivos. O motor renderiza os dois estados; o desenho
decide como confrontá-los — alternância, divisão da tela, sobreposição.

**O que o Premium liga** (compra única, vale em todos os cenários): raios,
rajadas de vento, vagalumes, fumaça de chaminé, fases da lua, acúmulo de neve,
estrela cadente, lampiões acendendo.

**Modelo de preço:** compra **única**, entre R$ 39,90 e R$ 59,90. **Não é
assinatura** — e isso é diferencial num mercado de assinaturas, então deve ficar
explícito, não nas letras miúdas. Cenários avulsos custam R$ 9,90–19,90 à parte.

**Sem anúncios**, por decisão de posicionamento: a pesquisa de concorrência
apontou fadiga de anúncio como a maior brecha do mercado. Vale comunicar.

**Estado de indisponibilidade.** Quando o Google Play não responde (offline, ou
produto ainda não publicado), o botão precisa ficar desabilitado **com o motivo
visível** — nunca um botão que parece clicável e não faz nada. O app já se
comporta assim; manter.

---

## O que NÃO redesenhar

Início, Loja e Ajustes estão fora deste pedido. Em especial, preservar dois
acertos que um redesign do zero descartaria sem perceber:

- **A prévia ao vivo antes de aplicar** — o usuário vê a cena se mover antes de
  confirmar. Poucos concorrentes fazem.
- **O tratamento honesto de ausência** — "Em breve" para cenário sem arte,
  botão desabilitado com motivo quando a compra não está disponível.
