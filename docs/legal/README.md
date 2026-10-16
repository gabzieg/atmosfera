# Fonte editorial dos documentos do Terra

Revisão de trabalho de 15/10/2026. Fontes únicas: `TERMOS.md` (1.7), `PRIVACIDADE.md` (1.11) e `CONTATO.md` (1.3). Os HTMLs são exportações; não editar cada cópia separadamente. O [registro de refinamento](../dev/REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md) reúne instruções internas e decisões pendentes. As versões refinadas não foram republicadas no Google Sites por esta execução.

```powershell
python scripts/sincronizar_documentos_legais.py
python scripts/sincronizar_documentos_legais.py --check
```

O gerador usa somente a biblioteca padrão Python e o subconjunto Markdown dos três documentos. Preserva os estilos das páginas locais no modelo; não altera a apresentação da landing. Produz as nove cópias em `docs/`, `public_html/` e assets do app, e `manifesto-documentos.json` com versões e hashes. `--check` falha se fonte, modelo, manifesto ou uma cópia divergirem. A CI executa essa conferência; o teste Android também compara `public_html`.

Estado editorial **rascunho para revisão**. O titular publicou o site no Google Sites em 15/10/2026. O endereço público foi registrado; vigência e versão de distribuição permanecem sem definição. Publicação web não comprova aprovação jurídica, início do teste ou envio ao Console. A configuração editorial `revisao.json` mantém `aprovado_para_publicacao=false`; o manifesto é gerado a partir dela. As versões são lidas dos textos, sem manter outra lista no código.

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
