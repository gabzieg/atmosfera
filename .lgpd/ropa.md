# Registro das operações de tratamento (ROPA) — rascunho

**Versão**: 0.1 · **Data**: 2026-10-16 · **Base**: [data-map.md](data-map.md) (A01–A08) e [legal-basis.md](legal-basis.md), código de `0d36b4a`.
**Estado: RASCUNHO para avaliação.** Não é o registro final do art. 37 da LGPD nem declara conformidade: reúne o que já está apurado em formato de registro e marca o que ainda precisa de evidência. Revisão indicada: Gabriel; **indicação não é aprovação**. Próxima revisão: 2027-04-16 ou antes, se mudar fornecedor, finalidade, dado ou público.

O art. 37 exige que controlador e operador mantenham registro das operações de tratamento. A estrutura abaixo segue o que o inventário já levantou; **conferir o modelo simplificado vigente da ANPD para agentes de pequeno porte** antes de tratar este documento como definitivo, porque o enquadramento como agente de pequeno porte ainda não foi confirmado ([governanca-encarregado.md](governanca-encarregado.md)).

**Complemento em 19/10:** importado da PR #52. Não substitui as evidências E01–E10 ou as rotinas atuais. A01 inclui cache HTTP `cacheDir/clima-met` de 512 KiB, separado do último clima. Sete contas de licença são relato administrativo, não número auditado de usuários ou adesões. Fontes atuais 1.14/1.8/1.6; continuam rascunhos.

## 1. Contato

| Papel | Quem | Observação |
|---|---|---|
| Controlador | Rafael Huppes (pessoa física) | Confirmado pelo responsável |
| Canal do titular | suporteterrabr@gmail.com | Confirmado. **Não** é, por si, designação de encarregado |
| Encarregado | Não designado formalmente | Decisão pendente (D05) |
| Operadores | Papéis ainda não concluídos por operação | Papéis por serviço não são presumidos: ver [vendors.md](vendors.md) |

## 2. Operações

Base legal = **hipótese em avaliação** (não adotada por inferência), conforme [legal-basis.md](legal-basis.md).

| ID | Operação / finalidade | Titulares | Dados | Compartilhamento e destino | Segurança (evidência estática) | Retenção | Base (hipótese) | Evidência que falta |
|---|---|---|---|---|---|---|---|---|
| A01 | Clima conforme localização opcional | Usuários | Localização aproximada; coordenadas arredondadas a 2 casas; indicador de local padrão | MET Norway (Noruega): coordenadas, IP e identificação do app | HTTPS; armazenamento privado; exclusões de backup; consulta só com o wallpaper na home | Último clima e cache HTTP no aparelho; logs externos em Oslo, prazo específico pendente | Art. 7º, V ou I, a decidir | Comprovar a jornada do consentimento, se for a base; prazo dos logs da MET |
| A02 | Conservar escolhas e arquivos locais | Usuários | Preferências de cena, arte e estilo; HD instalado | Nenhum | Armazenamento privado; `acervo` fora do backup | Até limpeza ou desinstalação | Art. 7º, V | Demonstrar vínculo com pessoa identificável (pode nem ser dado pessoal) |
| A03 | Validar e restaurar compras | Compradores | Estado de compra, produtos, comprovante/assinatura, token | Google Play (confirmação e restauração) | Verificação de assinatura; flags excluídos do backup | Flags no aparelho; pedidos ficam na Play | Art. 7º, V | Instrumentos da conta Play; norma específica, se houver retenção por obrigação legal |
| A04 | Prévias automáticas e download HD | Usuários | IP, caminho, horário e dados de conexão; User-Agent do app | Cloudflare R2 | HTTPS; hash completo; instalação privada | Prévia ~5 min sem uso (limite nominal 24 MiB); HD até remoção; **logs da conta não comprovados** | Art. 7º, V | Adesão ao DPA, logs, acessos e retenção na conta Cloudflare |
| A05 | Suporte, defeitos e direitos | Quem escreve | E-mail, mensagem e anexos; cena/arte/estilo, sintomas, versão do app, fabricante/modelo e Android | Gmail (Google); cliente de e-mail do usuário | Conta de e-mail; abre cliente externo, não envia sozinho | 60 dias após o encerramento, com exceções justificadas | Art. 7º, V; II e VI em casos específicos | **Rotina de exclusão, acessos e registro de execução** ([retencao-operacao.md](retencao-operacao.md)) |
| A06 | Páginas públicas | Visitantes | Dados técnicos de navegação | Google Sites | Não conferido | Não conferido | A definir | Configuração e cookies da conta; verificação pública |
| A07 | Organizar o teste fechado e o feedback | Participantes | E-mails/contas e inscrição/licença no Console; feedback | Console da Play; canal de feedback usado | Não conferido | **Não comprovada**; os 60 dias do suporte não se estendem à lista | Art. 7º, V como hipótese | Quem acessa a lista, quando apagar, onde fica |
| A08 | Diagnóstico técnico | Usuários | Logs Android e callbacks do Billing | Plataforma (Play), conforme o serviço | Sem SDK de analytics/anúncios/falhas identificado | Sem prazo universal comprovado | Art. 7º, IX e art. 10 só se demonstrados | Conferir SDKs do candidato; **nenhuma LIA foi concluída** |

## 3. Outros campos do registro

- **Transferência internacional**: MET Norway, na Noruega, está coberta pelo reconhecimento de adequação da Resolução ANPD 32/2026 (art. 1º, parágrafo único; mecanismo do art. 33, I da LGPD) — **confirmar o fluxo e o destinatário**. Google e Cloudflare dependem de evidência de destinos e instrumentos ([vendors.md](vendors.md)).
- **Decisão automatizada ou perfilamento**: nenhum identificado no código conferido.
- **Dados sensíveis**: nenhuma coleta intencional identificada no código. Relatos de suporte podem conter dado sensível incidental; ver tratamento restrito em [legal-basis.md](legal-basis.md).
- **Crianças e adolescentes**: o teste fechado é só de adultos no Brasil; adolescentes são objetivo posterior ([adolescentes-eca-digital.md](adolescentes-eca-digital.md)).
- **Risco**: [avaliacao-risco.md](avaliacao-risco.md).

## 4. Histórico

| Versão | Data | Mudança |
|---|---|---|
| 0.1 | 2026-10-16 | Primeiro registro em formato de ROPA, derivado do mapa de dados de 15/10. Rascunho. |
