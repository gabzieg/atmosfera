# ROPA — Registro de Operações de Tratamento (Art. 37)

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **Revisão**: semestral (próxima: 2027-04-08)
**Formato**: modelo simplificado da ANPD para Agentes de Tratamento de Pequeno Porte (8 campos), conforme skill `lgpd-ropa`. **[JURÍDICO]** confirmar o enquadramento como ATPP (Res. CD/ANPD nº 2/2022): pessoa natural com fins econômicos, tratamento de baixo risco.

## 1. Contato

| Papel | Quem | Contato |
|---|---|---|
| Controlador | Rafael Huppes (pessoa física) | suporteterrabr@gmail.com |
| Encarregado | Rafael Huppes (proposta; ver [encarregado.md](encarregado.md)) | suporteterrabr@gmail.com |
| Operadores | **Nenhum contratado.** Os terceiros abaixo são destinatários ou controladores próprios — ver [vendors/](vendors/) | — |

## 2. Operações como **controlador**

Detalhe de dados, base legal e fontes: [data-map.md](data-map.md) e [legal-basis.md](legal-basis.md).

| ID | Operação / finalidade | Categorias de titulares | Categorias de dados | Compartilhamento | Segurança | Retenção | Base legal |
|---|---|---|---|---|---|---|---|
| A001 | Obter o clima da região (função central) | Usuários, inclusive 13–17 | Latitude/longitude aproximadas (~1 km), indicador de local padrão | MET Norway (Noruega) | HTTPS; cache em armazenamento privado do app; coordenadas arredondadas | Só o último registro, no aparelho; MET: até 90 dias (política deles) | Art. 7º, I (consentimento) |
| A003 | Guardar preferências e direitos no aparelho | Usuários | Cenário/arte/estilo, brilho, flag Premium | Nenhum | Armazenamento privado do app | Enquanto instalado | Art. 7º, V |
| A004 | Confirmar compras e restaurá-las | Compradores | Estado da compra, ID de produto, recibo assinado | Google (Play) | Verificação criptográfica da assinatura do recibo | Flag local; histórico fica na conta Google | Art. 7º, V |
| A005 | Atender suporte, reembolso e pedidos de titular | Quem escreve | E-mail, nome, texto e anexos voluntários; versão do app, modelo/Android, cenário/arte/estilo | Google (Gmail) | Conta protegida; acesso só do responsável **[DECISÃO: quem mais acessa?]** | Proposta: 60 dias após resolver, salvo obrigação legal/defesa **[DECISÃO]** | Art. 7º, V e II |

## 3. Operações como **operador**

Nenhuma. O Terra não trata dado em nome de outro controlador.

## 4. Inativas / planejadas

| ID | Operação | Estado |
|---|---|---|
| A007 | Download de conteúdo (Cloudflare R2) | Código existe; URL de produção vazia; inativa na 1.0.5. Vale a partir da versão que ativar a Loja de packs. |
| A008 | Site de apresentação | Sem hospedagem definida |

## 5. Transferência internacional, decisão automatizada, risco

- **Transferência internacional**: Noruega (MET), EUA (Google, Cloudflare). Ver [transfers/README.md](transfers/README.md). **[JURÍDICO]**
- **Decisão automatizada / perfilamento**: nenhum.
- **Avaliação de risco**: não é alto risco — ver [RIPD/dispensa-2026-10-08.md](RIPD/dispensa-2026-10-08.md).
- **Legítimo interesse**: nenhuma atividade o utiliza (após as correções A002/A006).

## 6. Histórico

| Versão | Data | Mudança |
|---|---|---|
| 0.1 | 2026-10-08 | Primeiro registro, a partir do código `main` @ `2d01d16`. Rascunho. |

> **Checkpoint (skill `lgpd-ropa`)**: este registro só vira v1.0 depois de revisão do encarregado/jurídico.
