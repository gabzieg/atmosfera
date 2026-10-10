# Bases legais por atividade

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **[JURÍDICO]** todas as bases abaixo são proposta técnica, a validar por profissional de proteção de dados.
Fonte das regras: skill `lgpd-legal-basis` + `lgpd-audit/references/normative-reference.md`. Nenhum dado tratado é sensível (Art. 5º, II), então vale o Art. 7º.

| Atividade | Base proposta | Por quê | Revogável? | Pendência |
|---|---|---|---|---|
| **A001** Localização para o clima | **Consentimento** — Art. 7º, I, manifestado na permissão do Android, precedido da tela de explicação do onboarding (`ui/OnboardingScreen.kt`, passo "Permitir localização") | É livre (o app funciona sem, com cidade padrão), específico (uma finalidade) e revogável nas configurações. Uma base alternativa **concorrente** é a execução do serviço pedido (Art. 7º, V). | Sim — o app volta ao local padrão na hora | Conferir se o texto do onboarding cobre a **transferência para a Noruega** de forma "específica e em destaque" (Art. 33, VIII) — **[JURÍDICO]** |
| **A002** IP / User-Agent nas requisições | **Não é tratamento do Terra** (não coletamos nem registramos). Nenhuma base a declarar pelo Terra. | A política v1.4 §3.2 cita "legítimo interesse (art. 7º, IX)" para isso. Isso **pede LIA** (Art. 10) e declara base para algo que o Terra não faz. | — | **Reescrever §3.2**: informar que o IP é visível ao destinatário e que ele o trata segundo a política dele — sem base própria do Terra. |
| **A003** Preferências e flags locais | **Execução do serviço** solicitado — Art. 7º, V | São o próprio estado do que o usuário escolheu | — | Possivelmente nem seja dado pessoal (não identifica ninguém); manter por prudência |
| **A004** Compras | **Execução de contrato** — Art. 7º, V | O Google processa o pagamento; o Terra só confirma o direito | — | — |
| **A005** Atendimento por e-mail | **Execução de contrato / procedimentos preliminares a pedido do titular** — Art. 7º, V; e **cumprimento de obrigação legal** — Art. 7º, II — para pedidos de direitos (Art. 18) e reclamações de consumo | A pessoa escreve por vontade própria para ser atendida | Não (o pedido já foi atendido); o titular pode pedir eliminação (Art. 18, VI) | Prazo de guarda **[DECISÃO]**; documentar quem acessa a caixa |
| **A006** Backup do Android | **Não é tratamento do Terra** (é o sistema do usuário, na conta Google dele). A política v1.4 §3.6 cita "legítimo interesse" — mesmo problema da A002. | Melhor remover o fundamento: excluir o cache de localização do backup resolve na origem (ver [gaps.md](gaps.md) G08). | — | Decisão sobre a exclusão |
| **A007** Download do acervo (planejado) | **Execução do serviço** — Art. 7º, V (o usuário pede o cenário) | — | — | Só vale quando o recurso for ativado |
| **A008** Site | A definir | Depende da hospedagem | — | Sem implantação ainda |

## LIA (legítimo interesse)

**Nenhuma atividade do Terra precisa de legítimo interesse** se as correções de A002 e A006 forem feitas. É a opção mais simples: nenhum LIA para escrever e nenhum risco de "legítimo interesse sem teste", que é a lacuna que a skill aponta como armadilha. Se algum dia se usar essa base (ex.: métricas), criar `.lgpd/lia/<slug>.md` **antes** da coleta.

## Cláusulas-armadilha conferidas

- Consentimento por caixa pré-marcada: **não há** (a permissão é pedida pelo Android).
- "Concordo com tudo" como única base: **não há**; os Termos não pedem consentimento de dados.
- "Aceitar para usar o serviço" para finalidade não essencial: **não há**; a localização é opcional.
- **Menores 13–17**: a base de consentimento do Art. 14, §1º vale para **criança** (até 12 anos). Para adolescente, aplica-se o Art. 14 (melhor interesse) com as bases do Art. 7º. Como o público é 13+, a leitura é que **não há** consentimento parental específico exigido pela LGPD; a exigência de autorização de responsável vem dos **Termos** (capacidade civil, compras) e possivelmente do **ECA Digital** — ver [eca-digital.md](eca-digital.md). **[JURÍDICO]**
