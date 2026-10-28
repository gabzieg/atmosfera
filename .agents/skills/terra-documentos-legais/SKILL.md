---
name: terra-documentos-legais
description: Consultar, revisar e atualizar a Política de Privacidade, os Termos de Uso e o Contato do Terra, sincronizando automaticamente os Markdown com o manifesto e as cópias HTML do site e do app. Use para pedidos sobre esses documentos legais, inclusive quando o usuário não mencionar exportação ou HTML.
---

# Documentos legais do Terra

Origem: pedido do usuário em 28/10/2026 para que atualizar os textos inclua automaticamente todas as cópias derivadas, sem uma solicitação adicional.

## Fonte e escopo

Trabalhe na raiz do workspace atual. Leia `docs/legal/README.md` e as fontes necessárias à consulta: `PRIVACIDADE.md`, `TERMOS.md` e `CONTATO.md`, dentro de `docs/legal/`. Metadados editoriais estão em `docs/legal/revisao.json`.

Use as fontes atuais e evidências atuais do produto. `docs/legal/historico/` e `docs/legal/revisao-2026-10-05/` são históricos, não base para uma nova revisão. Não adote conclusões de pareceres anteriores como fatos sem conferir sua evidência. Para continuar uma revisão em andamento, consulte seu registro quando necessário.

Consulta sem pedido de atualização é somente leitura. Em consulta com atualização, retificação ou incorporação de respostas do usuário, a autorização para editar as fontes inclui gerar e conferir suas cópias locais: execute a sincronização na mesma tarefa, sem pedir confirmação para exportar.

## Atualização completa

Edite somente os Markdown canônicos e, quando afetado, `revisao.json`. Leia os documentos relacionados para manter coerência entre suas afirmações. Preserve os históricos e acrescente a nota da revisão; atualize versão e data dos documentos alterados e as referências às versões no README. Não altere numeração de documentos cujo conteúdo permaneça igual.

Não edite manualmente o manifesto ou os HTMLs derivados. O gerador existente lê as versões dos Markdown e os metadados do JSON. Depois de concluir as alterações editoriais, execute na raiz:

```bash
python3 scripts/sincronizar_documentos_legais.py
python3 scripts/sincronizar_documentos_legais.py --check
git diff --check
```

No ambiente em que Python 3 se chama `python`, use esse executável. Não substitua o gerador por cópias ou conversões manuais.

Isso atualiza `docs/legal/manifesto-documentos.json` e as nove páginas, combinando as três pastas `privacidade/`, `termos/` e `contato/` com:

- `docs/{pasta}/index.html`
- `public_html/{pasta}/index.html`
- `android-app/app/src/main/assets/legal/{pasta}/index.html`

Mesmo que faltem respostas para outros pontos da revisão, sincronize as alterações já concluídas antes de entregar a tarefa. Se a geração ou a conferência falhar, corrija a causa ou relate a falha; não declare as cópias atualizadas sem essas verificações. Inspecione o diff das fontes e dos metadados; o `--check` confere os bytes de todas as exportações e do manifesto. A CI já executa essa mesma conferência.

## Limites da sincronização

Gerar HTML não aprova a redação nem define vigência. Preserve o estado editorial salvo se o usuário tiver tomado e registrado a decisão correspondente. Não presuma envio, aprovação ou distribuição no Console.

O Google Sites não é atualizado pelo gerador: publicar lá depende de uma ação separada e autorizada. A cópia embarcada só chega a instalações existentes por uma nova versão do app. `docs/build-legal-pages.sh` apenas prepara as pastas exportadas para hospedagem; não converte Markdown.

Ao entregar, informe as versões alteradas, o resultado da sincronização e das verificações, e eventual publicação ainda pendente. Não faça commit, push ou publicação apenas por esta skill.
