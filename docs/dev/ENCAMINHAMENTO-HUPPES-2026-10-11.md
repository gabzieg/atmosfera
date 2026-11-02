# Encaminhamento ao Rafael — atualizado em 30/10/2026

Esta passagem substitui o estado de 11/10 deste arquivo; o histórico permanece no Git. A fonte canônica dos critérios é o [checklist de finalização](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md), com acompanhamento em [TASKS](TASKS.md). O projeto ainda não está liberado para publicação.

## Entrega desta sessão

- Base de código: `7b9c68a`, já integrada à `main`. Branch desta passagem: `gabzieg/projeto-conclus-o`; não é um novo candidato Android.
- Cadastro efetivo na Play concluído: 351 artes criadas; total de **361 artes, 353 ACTIVE e oito ocultas em DRAFT**, todas com preço BR R$ 1,99 e disponibilidade somente Brasil, sem novas regiões habilitadas. Piloto corrigido; dez textos existentes e Premium preservados.
- O comando de aplicação terminou com saída 0. Sua releitura final e uma segunda simulação autenticada confirmaram **zero criações, ajustes ou divergências restantes**. Inventário conferido e 18 testes de produtos aprovados antes da operação; [resultado e procedimento](../loja/produtos-play-2026-10-27/README.md#cadastro-efetivo-concluído-em-30102026).
- CI da base `7b9c68a` aprovada em [41059030629](https://github.com/gabzieg/atmosfera/actions/runs/41059030629): testes unitários, sincronização legal, lint, builds debug/release e secret-scan. Isso não comprova execução em aparelho nem valida automaticamente o commit documental desta passagem.
- Nenhuma mudança de código Android, nova compra, AAB gerado/enviado, publicação do site ou início do teste fechado nesta sessão. Credenciais, pacotes e logs de operação permanecem fora do Git.

## Primeiro passo do Rafael — ambiente e checkout

O retorno do Windows encaminhado pelo usuário em 30/10 informa: nenhum aparelho físico conectado; Pixel_Tablet apenas com APK debug do ensaio de idade; Pixel_8 com Terra 1.0.4 por sideload; ambos sem conta Google. Loja e checkout não foram abertos. **Não existe resultado de preço/checkout desta conferência.** Esta sessão no Mac não inspecionou os emuladores diretamente.

1. Disponibilizar aparelho/emulador com Play Store, conta adulta testadora de licença, acesso ao teste interno e Terra instalado pela Play. A licença de compras e a adesão à faixa são verificações distintas. Preservar instalações/dados atuais; desinstalar ou apagar dados exige autorização explícita, ainda não concedida.
2. Identificar versão/código instalados. Reabrir o app para renovar a consulta e conferir **Aldeia japonesa — Pixel Art** e **Casa da bruxa — Pixel Art**, ambas a R$ 1,99. Se já adquiridas, selecionar outra arte nova e registrar seu ID.
3. Abrir checkout sem confirmar, conferir produto/preço e indicação inequívoca de instrumento de teste sem cobrança. Registrar resultado sanitizado, sem conta, recibo ou token. Se a indicação de teste não aparecer, cancelar.
4. Com autorização correspondente para concluir a compra de teste, executar compra → download → aplicação → reinício e o roteiro de restauração/revogação/offline de B2. O cadastro está encerrado; A7/A8/B2 só fecham com as evidências dos respectivos critérios.

## Demais pendências até finalizar

| Frente do checklist | Responsabilidade e próximo resultado necessário |
|---|---|
| A4 — público/idade, bloqueante | Gabriel decide por fase, com evidências de Rafael/controlador e validação jurídica necessária. A ferramenta debug integrada não encerra a avaliação nem implementa controles no release. |
| A3/A5 — dados e documentos | Gabriel valida/aprova; Rafael comprova práticas das contas/fornecedores. Fechar vigência, republicação e correspondência app/site/Console. Revisões locais registradas para pacote 8: Privacidade 1.19, Termos 1.10 e Contato 1.9, ainda sem aprovação/vigência. Segurança dos dados salva não equivale a aprovação final. |
| A2 — candidato atualizado | Pacote 8 decidido em 28/10; `versionName` ainda pendente e fonte permanece 7/1.0.5. Reconferir códigos usados antes de gerar a revisão final aprovada; registrar configuração, hash, certificados e arquivos R8 pelo roteiro de release. Não enviar o rascunho com o pacote 7, conforme TASKS. |
| A6/A7 — configuração e acesso | Rafael confere Brasil, licenças e ambiente; Gabriel/Rafael resolvem os demais itens do painel/ficha conforme acesso. O procedimento de acesso pago do revisor permanece pendente no checklist; conta testadora de licença não comprova acesso do revisor. |
| B — testes e participantes | Rafael executa aceites de catálogo, Premium separado das artes, clima/home, permissões, backup, suporte, estabilidade e desempenho. Último registro do fechado em 28/10: sete contas na lista e zero participantes; confirmar requisito efetivo da conta, completar adesões e acompanhar período contínuo. |
| C — produção | Gabriel e Rafael consolidam aceites, origem/licenças das artes, endpoint e continuidade do acervo, fornecedores/bases/governança e operação de suporte/dados. Obter acesso à produção e autorização antes de distribuir ou cobrar de verdade. |

O Rafael pode executar as conferências das contas e dos aparelhos; decisões e aprovações atribuídas ao Gabriel e a validação jurídica continuam pendentes com seus responsáveis. Não repetir cadastro nem tratar uma simulação, CI ou prazo cumprido como aceite desses itens.

## Retomada pelo Git

```bash
git fetch origin
git switch --track origin/gabzieg/projeto-conclus-o
```

Se a branch já existir localmente, selecione-a e atualize com `git pull --ff-only`. Preserve alterações locais antes de trocar de branch. Depois da integração desta passagem, retome pela `main` atualizada. A leitura inicial necessária é este arquivo e o checklist canônico; os roteiros detalhados estão ligados pelos itens correspondentes.
