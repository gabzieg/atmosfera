# Fornecedor — MET Norway (previsão do tempo)

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-vendor-audit`.

| Campo | Valor |
|---|---|
| Identificação | Meteorologisk institutt (Instituto Meteorológico da Noruega) — órgão público norueguês. Servidores próprios na Europa (Oslo), segundo a política deles. |
| Serviço | Locationforecast 2.0 (`https://api.met.no/weatherapi/locationforecast/2.0/compact`), API pública, sem chave |
| Finalidade | Fornecer a previsão que o papel de parede desenha |
| Dados que recebe | Coordenadas arredondadas a 2 casas (~1 km), IP do aparelho (inerente), `User-Agent` com nome/versão do app e o e-mail de suporte |
| Dados que **não** recebe | Identificador de usuário ou de aparelho, conta, e-mail do usuário |
| **Papel (proposta)** | **Destinatário que atua por conta própria** (serviço público de dados, não executa tratamento sob instrução do Terra). **Não é operador** no sentido do Art. 39. **[JURÍDICO]** |
| Contrato / DPA | **Não existe e não é possível**: é uma API pública com termos de uso (<https://api.met.no/doc/TermsOfService>), sem acordo individual. |
| Licença dos dados | CC BY 4.0 (<https://api.met.no/doc/License>) — atribuição presente na Ajuda do app e nos textos legais |
| Retenção do lado deles | Endereços IP guardados por até 90 dias nos serviços públicos; os registros podem conter as coordenadas consultadas (<https://www.met.no/en/About-us/privacy>) |
| Tier | **Médio** (dado pessoal comum, escala pequena). Reavaliar para Alto com crescimento. |
| Última revisão / próxima | 2026-10-08 / 2027-10-08 |
| Responsável interno | Técnico: Gabriel · Controlador: Rafael Huppes |

## Observação sobre o critério eliminatório da skill

A skill `lgpd-vendor-audit` diz: *"se o operador não assina DPA — não pode ser usado"*. Isso vale para **operadores**. A MET **não é contratada como operador**, e não existe como assinar um DPA com uma API pública. A conclusão correta depende de **classificar corretamente o papel** — ponto a validar com advogado **[JURÍDICO]**. Se a MET fosse tratada como operador, a consequência seria trocar de fornecedor ou passar a usar um intermediário próprio (descartado antes: contradiz a promessa "a localização nunca passa por nenhum servidor nosso").

## Mitigações já em vigor

- Coordenadas arredondadas a ~1 km antes de sair do aparelho (`WeatherRepository.coordenada()`).
- Nenhum identificador de usuário no pedido.
- Consulta **só** com o wallpaper visível na home e desbloqueado (PR #46) — cumpre a regra da MET de não buscar dado fora de uso.
- Freio persistente para 429/403 (`weather/FreioMet.kt`), cache HTTP respeitando `Expires`, User-Agent com contato.
- HTTPS.

## Pendências

- [ ] **[JURÍDICO]** Papel da MET e suficiência das mitigações.
- [ ] Monitorar a política da MET anualmente (retenção e finalidade).
