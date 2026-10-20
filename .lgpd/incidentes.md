# Plano de resposta a incidente de segurança — rascunho

**Versão**: 0.1 · **Data**: 2026-10-16 · **Estado: rascunho; contatos e responsáveis a preencher fora do Git**.
Fonte: LGPD art. 48 e **Resolução CD/ANPD nº 15/2024** (texto conferido no DOU em 2026-10-16, arts. 5º, 6º, 9º e 10). Em caso de dúvida, vale o texto oficial.

A ausência de servidor próprio com base de usuários não quantifica nem elimina riscos: considerar todos os fluxos A01–A08, contas administrativas, pedidos, participantes e anexos. Os incidentes plausíveis estão nas **contas que guardam dado** (suporte, lista de testadores, Cloudflare, Google Sites) e na **integridade do que é distribuído**.

## Quem faz o quê (preencher; **não** colocar telefones no Git)

| Papel | Quem | Contato |
|---|---|---|
| Decisão de comunicar | Controlador (Rafael Huppes) ou encarregado, se designado ([governanca-encarregado.md](governanca-encarregado.md)) | fora do Git |
| Resposta técnica | Gabriel | fora do Git |
| Substituto | A definir | — |

## Cenários do Terra

| # | Cenário | Dado pessoal em risco | Primeiros passos |
|---|---|---|---|
| S1 | **Caixa de suporte (Gmail) comprometida** | E-mails, nomes, relatos e anexos (A05) | Trocar senha, encerrar sessões, revisar filtros e encaminhamentos, ativar/verificar verificação em duas etapas, estimar quantas pessoas escreveram |
| S2 | **Lista de participantes do teste exposta** (A07) | E-mails/contas dos participantes | Remover acesso indevido, identificar quem viu, preservar o registro de acesso do Console |
| S3 | **Conta ou credencial do Cloudflare R2 comprometida** (A04) | Logs de IP; principalmente **integridade** dos pacotes | Revogar credencial, conferir objetos, restaurar o último estado conhecido. O índice e as miniaturas são **embarcados** (data-map A04), o que reduz o risco de troca silenciosa; **confirmar que o hash vem do índice embarcado, e não do bucket** |
| S4 | **Chave ou senhas de assinatura expostas** | Nenhum diretamente; risco de atualização maliciosa | Registrar **fora do Git** quem tem cópia; pedir ao Google a troca da chave de envio; pausar publicação |
| S5 | **Conta do Google Sites alterada** (A06) | Visitantes (dados técnicos) e texto legal adulterado | Retomar a conta, restaurar versão anterior, conferir o que foi publicado |
| S6 | **Mensagem enviada ao destinatário errado** | Dado do titular | Conter, avisar o destinatário, registrar |
| S7 | MET bloqueia o app (403) ou limita (429) | Nenhum | Não é incidente de dado pessoal; o app já tem freio (`weather/FreioMet.kt`). Registrar como falha operacional |

## Critério de comunicação à ANPD (Res. 15/2024, art. 5º)

O incidente pode acarretar risco ou dano relevante quando puder **afetar significativamente interesses e direitos fundamentais** **e, cumulativamente**, envolver ao menos um destes: dados sensíveis; dados de crianças, adolescentes ou idosos; dados financeiros; dados de autenticação em sistemas; dados protegidos por sigilo legal, judicial ou profissional; dados em larga escala. A decisão é **caso a caso e deve ser escrita**. Incidente **não comunicado também é registrado**.

## Prazos (Res. 15/2024)

| Ação | Prazo | Marco |
|---|---|---|
| Comunicar a ANPD (art. 6º) | **3 dias úteis**, pelo controlador | Conhecimento de que o incidente **afetou dados pessoais** |
| Comunicar o titular (art. 9º) | **3 dias úteis** | Mesmo marco |
| Registrar o incidente (art. 10) | Guardar por **mínimo de 5 anos** | Data do registro |

**Prazo em dobro** (Res. 2/2022, art. 14, II) só se o enquadramento como agente de pequeno porte for confirmado e **não** se houver risco à integridade física ou moral ([governanca-encarregado.md](governanca-encarregado.md)). **Meta interna: 3 dias úteis.**

## Conteúdo das comunicações

- **À ANPD** (art. 6º, §2º, entre outros itens, usar o texto oficial): natureza e categoria dos dados; número de titulares, discriminando crianças, adolescentes e idosos; medidas técnicas e de segurança antes e depois; riscos e impactos; motivos da demora; medidas de reversão ou mitigação.
- **Ao titular** (art. 9º, 7 itens): natureza e categoria dos dados; medidas de segurança; riscos e impactos; motivos da demora; medidas adotadas; **data do conhecimento**; contato para informações e, se houver, do encarregado. Linguagem simples e clara.

## Passo a passo

1. **Conter e preservar** (primeiras horas): isolar, trocar credenciais, não apagar evidência. Anotar o **instante exato do conhecimento**.
2. **Avaliar** (até 1 dia): que dados, quantas pessoas, se há adolescentes ou idosos, se estavam protegidos; aplicar o critério do art. 5º.
3. **Decidir e registrar por escrito** quem decidiu e por quê.
4. **Comunicar** no prazo, se couber, a ANPD e os titulares.
5. **Corrigir** a causa e atualizar este plano.

## Registro

O modelo está em [incidentes-registro.md](incidentes-registro.md). Registros reais, detalhes técnicos exploráveis, protocolos e dados de titulares ficam em armazenamento privado com acesso controlado; o repositório público não é a base de incidentes.

## Pendências

- [ ] **[DECISÃO]** Responsáveis, substituto e contatos (fora do Git).
- [ ] **[JURÍDICO]** Enquadramento e prazo aplicável.
- [ ] Simular um cenário (S1 ou S2) uma vez por ano e cronometrar.
- [ ] Conferir a hipótese de S3 sobre a origem do hash.
