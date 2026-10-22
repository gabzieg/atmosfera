# Roteiro de confirmação administrativa — Rafael

**Prioridade em 19/10:** confirmar maior versionCode de todas as faixas, código/processamento/inclusão do AAB no rascunho fechado e certificado de upload; seguir o [diagnóstico da versão](../docs/dev/DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md). Rafael informou sete contas selecionadas nas licenças; faltam checkout de teste e contagem separada das adesões na faixa. AAB local de 06/10 é antigo e não deve ser confundido com o candidato atual. **22/10:** o candidato é o código 7 (`c1848a9`, AAB SHA-256 `0ed276df…0345280`); o certificado de upload no Console é igual ao dele, os países já estão só no Brasil e as licenças estão salvas. O rascunho do fechado está vazio: falta incluir o 7 da biblioteca, concluir a declaração de ID de publicidade e tirar o `?authuser=1` da URL da política — ver o [diagnóstico atualizado](../docs/dev/DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado).

15/10/2026. Gabriel informou que somente Rafael tem acesso às contas. Este roteiro foi preparado para encaminhamento pelo usuário; **nenhuma mensagem foi enviada pelo assistente**. Não retornar senhas, tokens, contratos integrais, listas de e-mails ou capturas com dados pessoais. Basta um resumo das configurações e referências não sensíveis.

## 1. Cloudflare — terra-acervo

- [ ] Confirmar quem administra a conta e se outros colaboradores/tokens têm acesso; responder por função/quantidade, sem expor credenciais.
- [ ] Em R2, abrir o bucket e suas configurações; conferir se **Data Access Logs** está Enabled ou Disabled. Não habilitar só para esta conferência. Se indisponível na conta, registrar isso.
- [ ] Verificar separadamente Logpush/exportações, Workers ou outros intermediários e eventuais cópias de logs. Informar recurso, campos, destino e retenção efetivos; “não conferido” é resposta válida, “sem logs” exige essa distinção.
- [ ] Confirmar localização/jurisdição configurada dos objetos, endpoint usado, termos/contrato aplicáveis e instrumentos de tratamento/transferência disponíveis à conta. Não presumir que o local dos objetos determina o de todos os logs.

Referência do recurso: [Data Access Logs](https://developers.cloudflare.com/r2/buckets/data-access-logs/). O prazo documentado de sete dias pertence a esse recurso, não a toda a conta ou a cópias exportadas.

## 2. Gmail e Google Sites

- [ ] Confirmar tipo de conta/serviço: Gmail pessoal ou Workspace, sem publicar dados de faturamento.
- [ ] Conferir acessos/delegações, encaminhamentos, clientes que sincronizam mensagens e cópias de anexos. Informar se existe colaborador autorizado; não listar contatos pessoais no GitHub.
- [ ] Confirmar adoção da [rotina de suporte](rotinas-atendimento-retencao.md): triagem de direitos, encerramento, exclusão em 60 dias, Lixeira/cópias e exceções justificadas. Indicar o que já funciona e o que ainda será implantado.
- [ ] No Sites, conferir recursos incorporados, formulários/Analytics adicionados e acesso público das três páginas. Não presumir ausência de cookies da plataforma por não usar Analytics próprio.

## 3. Play Console e registros de participantes

- [ ] Confirmar Brasil e adultos no recrutamento do teste, sem coletar documentos de identidade apenas para este roteiro.
- [ ] Conferir faixa e teste de licença separadamente; informar quantidade configurada e se o checkout foi visto com instrumento de teste, sem exportar e-mails.
- [ ] Identificar cópias locais de listas/feedback, acesso e retenção a adotar; os dados do Console podem ter controles distintos dos arquivos exportados.
- [ ] Confirmar quem pode consultar pedidos e relatórios de qualidade. Não retornar códigos de pedido, recibos ou tokens nesta resposta.

## Modelo de resposta, sem dados pessoais adicionais

```text
Data da conferência:
Cloudflare: acesso por função/quantidade; Data Access Logs ligado/desligado/não disponível;
outras exportações/intermediários; retenção/destino conhecidos ou ainda não conferidos;
localização/jurisdição e referência dos termos aplicáveis.
Gmail: pessoal/Workspace; acessos/delegações/encaminhamentos/cópias;
rotina de 60 dias e direitos adotada, testada ou pendente.
Sites: incorporações/Analytics/formulários próprios; acesso público conferido ou pendente.
Console: quantidade na faixa e nas licenças; checkout de teste confirmado ou pendente;
cópias administrativas e critério de retenção proposto/adotado.
Pendências que ainda não consegui conferir:
```

As confirmações fecham evidências administrativas; não aprovam automaticamente bases legais, textos, data de vigência, AAB ou publicação. Materiais privados de comprovação permanecem fora do repositório público.
