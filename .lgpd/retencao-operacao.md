# Retenção — rotina de execução (rascunho)

**Versão**: 0.1 · **Data**: 2026-10-16 · **Estado: rascunho; falta executar e comprovar (D06)**.
A **tabela de prazos públicos** está na Política 1.12, seção 9. Este arquivo define a **rotina** que faz esses prazos acontecer. Prazo escrito sem rotina é promessa sem execução.

## 1. E-mails e anexos de suporte — 60 dias após o encerramento

**Regra (Política 1.12 §9):** eliminados da caixa em até **60 dias após o encerramento do chamado**, salvo conservação restrita e justificada por obrigação legal ou defesa em processo (LGPD, art. 16).

**Rotina proposta**

1. **Ao encerrar** um chamado: aplicar o rótulo `encerrado/AAAA-MM-DD` na conversa.
2. **Todo mês** (dia fixo): buscar `label:encerrado older_than:60d`, **revisar** se há motivo de conservação e **excluir** as conversas restantes.
3. **Esvaziar a lixeira** ao final (o Gmail mantém itens excluídos por um período próprio; o prazo efetivo de eliminação inclui isso).
4. **Registrar a execução** (data, quantidade de conversas eliminadas, **sem conteúdo**) numa planilha de retenção.
5. **Conservação além do prazo:** registrar **o motivo, a base e a data de revisão**; manter só o necessário e restringir o acesso.

**Quem executa:** a pessoa que responde o suporte, com revisão do controlador. **[DECISÃO]**

## 2. Lista do teste fechado (A07)

Os 60 dias do atendimento **não** se estendem à lista de participantes (data-map A07).

- [ ] **[DECISÃO]** Quando apagar a lista ao fim do teste e onde ela fica (Console da Play).
- [ ] Quem tem acesso, registrado fora do Git.
- [ ] Não copiar e-mails de participantes para o repositório nem para documentos.

## 3. Dados no aparelho (A01–A04)

Não há rotina do controlador: o usuário apaga limpando os dados do app ou desinstalando. O **cache de prévias** tem política de cerca de 5 minutos sem uso e limite nominal de 24 MiB; a execução da limpeza pode ocorrer depois, inclusive na próxima abertura do app (Política §9). **Prazo de cache não é prova de eliminação física.**

## 4. Registros de terceiros

MET, Google e Cloudflare mantêm logs e pedidos conforme seus próprios termos; o Terra não controla esses prazos. A evidência de configuração e retenção está em [vendors.md](vendors.md) (pendente).

## 5. Registros de conformidade

| Registro | Guarda proposta | Justificativa |
|---|---|---|
| Incidentes ([incidentes-registro.md](incidentes-registro.md)) | Mínimo de 5 anos | Res. CD/ANPD 15/2024, art. 10 |
| Planilha de pedidos de titulares | 5 anos **[DECISÃO]** | Prova de que o pedido foi atendido |
| Planilha de execução da retenção | 5 anos **[DECISÃO]** | Prova de que a rotina rodou |

Esses registros guardam **datas e contagens, sem conteúdo** nem dado pessoal.

## Pendências

- [ ] **[DECISÃO]** Executor, dia do mês e ferramenta da planilha.
- [ ] **[JURÍDICO]** Hipóteses de conservação para atendimento de consumo e reembolso.
- [ ] Rodar a rotina uma vez e anexar o registro, para fechar D06.
