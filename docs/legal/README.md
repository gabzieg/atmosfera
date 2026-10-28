# Fonte editorial dos documentos do Terra

Revisão de trabalho de 28/10/2026, iniciada do zero a partir das fontes canônicas e do código atual. Fontes únicas: `TERMOS.md` (1.10), `PRIVACIDADE.md` (1.19) e `CONTATO.md` (1.9). A [nova revisão documental](../../.lgpd/revisoes/2026-10-28-documentos/REVISAO.md) registra achados e decisões pendentes; os registros anteriores abaixo são históricos e não foram usados como base desta rodada. Os HTMLs são exportações; não editar cada cópia separadamente. O [registro de refinamento](../dev/REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md) reúne instruções internas e decisões pendentes. As versões refinadas não foram republicadas no Google Sites por esta execução.

```powershell
python scripts/sincronizar_documentos_legais.py
python scripts/sincronizar_documentos_legais.py --check
```

O gerador usa somente a biblioteca padrão Python e o subconjunto Markdown dos três documentos. Preserva os estilos das páginas locais no modelo; não altera a apresentação da landing. Produz as nove cópias em `docs/`, `public_html/` e assets do app, e `manifesto-documentos.json` com versões e hashes. `--check` falha se fonte, modelo, manifesto ou uma cópia divergirem. A CI executa essa conferência; o teste Android também compara `public_html`.

## Sincronização nos pedidos ao agente

A [skill `terra-documentos-legais`](../../.agents/skills/terra-documentos-legais/SKILL.md) inclui geração e conferência das cópias no próprio pedido de atualização dos documentos. É descoberta pelo Codex em `.agents/skills/` e pelo Claude Code pelo adaptador em `.claude/skills/`; também pode ser invocada pelo nome. Não é necessário pedir a exportação separadamente. A regra foi registrada a pedido do usuário em 28/10/2026.

Ao modificar uma fonte, o agente atualiza a versão/data correspondente, concilia os documentos relacionados, executa o gerador e `--check`, e entrega Markdown, manifesto e HTMLs coerentes. Consultas sem atualização permanecem somente leitura. Edições feitas manualmente fora desse fluxo continuam exigindo os comandos acima; a CI recusa exportações divergentes.

Essa automação cobre os arquivos locais versionados. Não publica no Google Sites nem atualiza aplicativos já instalados.

## Estado editorial

Estado editorial **rascunho para revisão**. O titular publicou o site no Google Sites em 15/10/2026. O endereço público foi registrado; os textos revisados destinam-se ao próximo pacote (código de versão 8), em preparação para teste fechado no Brasil; `versionName` ainda não definido. A vigência permanece a definir. Publicação web não comprova aprovação jurídica, início do teste ou envio ao Console. A configuração editorial `revisao.json` mantém `aprovado_para_publicacao=false`; o manifesto é gerado a partir dela. As versões são lidas dos textos, sem manter outra lista no código.

Depois da avaliação e decisão do responsável, atualizar textos e `revisao.json`: registro de aprovação, vigência ISO, versão do app, domínio HTTPS e estado aprovado. O gerador recusa aprovação sem esses campos ou com metadados de rascunho ainda no texto. Preencher o JSON não executa revisão jurídica nem publica páginas; o registro deve corresponder a uma aprovação real.

## Endereços publicados

- Site: [https://sites.google.com/view/terra-live-wallpaper](https://sites.google.com/view/terra-live-wallpaper).
- Privacidade (URL a usar no campo correspondente do Console): [https://sites.google.com/view/terra-live-wallpaper/privacidade](https://sites.google.com/view/terra-live-wallpaper/privacidade).
- Termos: [https://sites.google.com/view/terra-live-wallpaper/termos](https://sites.google.com/view/terra-live-wallpaper/termos).
- Contato: [https://sites.google.com/view/terra-live-wallpaper/contato](https://sites.google.com/view/terra-live-wallpaper/contato).

Usar URLs sem `authuser`, que apenas seleciona uma conta Google. O nome `dominio_publico` no JSON aceita esta URL-base com caminho do Google Sites; não implica compra de domínio próprio. Fontes/HTMLs locais agora incluem os links públicos. As páginas atualmente publicadas ainda mostram “Documento em revisão” e vigência a definir; mudanças futuras na fonte precisam ser aplicadas/republicadas no Sites manualmente. Não há sincronização automática com o Google Sites ou com apps já instalados.

## Histórico

`historico/anterior-a-2026-10-11/` conserva as três fontes e três exportações anteriores exatamente como encontradas. Elas podem conter campos pendentes e afirmações corrigidas na revisão atual; não são fontes vigentes para nova exportação. Seu arquivamento não comprova que tenham sido publicadas ou aceitas por usuários. O Git preserva também o histórico das cópias do app.

Os rascunhos locais de 05/10 foram preservados e, por solicitação do usuário em 11/10, versionados como registros históricos, sem vigência ou aprovação para uso público no app/site. A fonte desta revisão é a pasta canônica acima. Não distribuir referências que dependam de arquivos privados locais.

Para aceite e entrega ao site, consultar [ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md](../dev/ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md).

## Respostas do titular integradas em 15/10

Preservado o refinamento editorial de Gabriel em `1bd5a70`. As respostas posteriores restringem o teste a adultos no Brasil e definem futuros efeitos/estilos inclusos no Premium. Gabriel é o responsável pela revisão/aprovação final; esse aceite não foi presumido. Detalhes e pendências em [registro de respostas](../dev/RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).
