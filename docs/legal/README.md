# Fonte editorial dos documentos do Terra

Revisão de trabalho de 11/10/2026. Fontes únicas: `TERMOS.md` (1.4), `PRIVACIDADE.md` (1.7) e `CONTATO.md` (1.1). Os HTMLs são exportações; não editar cada cópia separadamente.

```powershell
python scripts/sincronizar_documentos_legais.py
python scripts/sincronizar_documentos_legais.py --check
```

O gerador usa somente a biblioteca padrão Python e o subconjunto Markdown dos três documentos. Preserva os estilos das páginas locais no modelo; não altera a apresentação da landing. Produz as nove cópias em `docs/`, `public_html/` e assets do app, e `manifesto-documentos.json` com versões e hashes. `--check` falha se fonte, modelo, manifesto ou uma cópia divergirem. A CI executa essa conferência; o teste Android também compara `public_html`.

Estado **rascunho para revisão**, sem vigência, domínio ou versão de distribuição inventados. Não houve publicação na web, envio ao Console ou aprovação jurídica. A configuração editorial `revisao.json` mantém `aprovado_para_publicacao=false`; o manifesto é gerado a partir dela. As versões são lidas dos textos, sem manter outra lista no código.

Depois da avaliação e decisão do responsável, atualizar textos e `revisao.json`: registro de aprovação, vigência ISO, versão do app, domínio HTTPS e estado aprovado. O gerador recusa aprovação sem esses campos ou com metadados de rascunho ainda no texto. Preencher o JSON não executa revisão jurídica nem publica páginas; o registro deve corresponder a uma aprovação real.

## Histórico

`historico/anterior-a-2026-10-11/` conserva as três fontes e três exportações anteriores exatamente como encontradas. Elas podem conter campos pendentes e afirmações corrigidas na revisão atual; não são fontes vigentes para nova exportação. Seu arquivamento não comprova que tenham sido publicadas ou aceitas por usuários. O Git preserva também o histórico das cópias do app.

Os rascunhos locais de 05/10 não foram apagados ou publicados. A fonte desta revisão é a pasta canônica acima. Não distribuir referências que dependam de arquivos privados locais.

Para aceite e entrega ao site, consultar [ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md](../dev/ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md).
