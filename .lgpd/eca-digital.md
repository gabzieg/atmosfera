# Aderência à Lei nº 15.211/2025 (ECA Digital)

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **Próxima revisão**: 2027-04-08 · **[JURÍDICO]** ponto central
Fonte das obrigações: skill `lgpd-eca-digital-minors` + `lgpd-audit/references/normative-reference.md`. **As duas fontes são resumos; o texto da lei e as orientações da ANPD precisam ser conferidos antes de afirmar qualquer obrigação como certa.**

## Aplicabilidade

| Pergunta da skill | Resposta para o Terra |
|---|---|
| Há expectativa razoável de menores entre os usuários? | **Sim.** Papel de parede com arte estilizada e distribuição na Google Play. |
| A faixa etária mínima nos termos é menor que 18? | **Sim.** Público declarado **13+** (decisão de 2026-10-04); Termos e Política já dizem que 13–17 dependem de autorização de responsável. |
| Há estatística de base? | Não (app não publicado). |

**Conclusão provisória: o ECA Digital provavelmente se aplica** ("acesso provável" por menores). O que **não está claro** é quais obrigações específicas recaem sobre um app de papel de parede **sem conteúdo restrito a adultos, sem conta, sem conteúdo gerado por usuário e sem anúncios**. A skill descreve a verificação de idade como exigência ampla; é preciso confirmar o alcance real. **[JURÍDICO]**

## Estado de cada obrigação (conforme a skill)

| Obrigação | Estado no Terra | Lacuna? |
|---|---|---|
| Verificação confiável de idade (Art. 9º, §1º); autodeclaração vedada | **O app não tem nenhuma tela ou lógica de idade.** O 13+ é só declaração nos textos e no Console. | **Possível lacuna** — depende do alcance real. Caminho citado pela skill: validação feita pelo sistema operacional ou pela loja de aplicativos. Verificar se a Google Play oferece sinal de idade ao app e se isso basta. |
| Vinculação a responsável até 16 anos (Art. 24) | Não implementada. Os Termos exigem a autorização, mas nada no app a opera. As compras passam pelo Google Play (aprovação de compra nos controles familiares, quando o responsável os configurou). | **Possível lacuna** |
| Vedação de perfilamento publicitário (Arts. 22 e 26) | Sem anúncios, sem analytics, sem perfil. | Não |
| Vedação de loot boxes (Art. 20) | Compras determinísticas (Premium e cenários); nenhum item aleatório. | Não |
| Supervisão parental (Arts. 17, §4º e 18) | Delegada ao Google (Family Link). O app não oferece controles próprios. Geolocalização é aproximada e opcional. | A confirmar |
| Relatórios de transparência (Art. 31) | Aplica-se a plataformas com mais de 1 milhão de usuários menores. | Não aplicável |
| Remoção de conteúdo ilícito (Art. 27) | Sem conteúdo gerado por usuário. | Não aplicável |

## Riscos residuais e decisão

- **Risco**: sanção de até 10% do faturamento do grupo no Brasil, ou R$ 10 a R$ 1.000 por usuário, limitada a R$ 50 milhões por infração (Art. 35, II, conforme a skill). Para um app de venda única e de baixa receita o teto é teórico, mas a **crianças e adolescentes no ambiente digital são o 2º eixo prioritário de fiscalização da ANPD em 2026–2027** (Res. CD/ANPD nº 30/2025).
- **Opção A — declarar 18+** no Console e nos Termos: elimina a questão de menores, ao custo de excluir o público adolescente (que provavelmente é relevante para um papel de parede). **[DECISÃO]**
- **Opção B — manter 13+** e adotar o que for aplicável (sinal de idade da loja, texto claro de autorização, registro desta avaliação). **[DECISÃO + JURÍDICO]**

## Pendências

- [ ] **[JURÍDICO]** Confirmar a aplicabilidade e o alcance da verificação de idade para este tipo de produto.
- [ ] **[DECISÃO]** 13+ ou 18+.
- [ ] Conferir no Play Console o que a Google oferece sobre idade/família para o app e documentar.
- [ ] Atualizar a Política §11 e os Termos §2 conforme a decisão.
- [ ] Reavaliar em 6 meses ou ao passar de 100 mil instalações.

**Owner**: Rafael Huppes (encarregado proposto) · **Avaliação realizada em**: 2026-10-08.
