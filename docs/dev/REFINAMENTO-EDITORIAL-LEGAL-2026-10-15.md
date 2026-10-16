# Refinamento editorial e pendências legais — 15/10/2026

## Respostas de Rafael em 15/10 — atualização do escopo

Teste fechado exclusivamente no Brasil, somente com adultos e contas de licença. Gabriel fará a revisão/aprovação final dos textos, ainda pendente. O candidato é a próxima versão após os ajustes; número/versionCode e data inicial dependem de conferência no Console. Quantidade de contas e configuração efetiva de licença não confirmadas. Premium inclui futuros efeitos e estilos, preservadas artes/variantes separadas. Intenção de não retirar conteúdo, com plano de exceção ainda em estudo. Logs, retenção e acesso dos fornecedores seguem sem definição/verificação.

Este registro supera as pendências de decisão sobre país, participantes menores, responsável pela revisão e inclusão de efeitos/estilos futuros nos registros anteriores. Não confirma configuração do Console, aprovação jurídica ou início da contagem. Fontes locais 1.11/1.7/1.3 precisam de revisão e republicação no Sites; geração de HTML não altera páginas públicas.

Detalhe: [respostas e roteiro para iniciar o teste](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).


Fontes refinadas: Privacidade 1.10, Termos 1.6 e Contato 1.3. Esta entrega é editorial: não registra parecer jurídico, aprovação, vigência, publicação web ou aceite do aplicativo. As lacunas materiais continuam indicadas nos textos. O Git preserva as revisões anteriores.

## Alterações preparadas

- Condições de compras de teste concentradas nos Termos; removido o mesmo parágrafo operacional dos resumos de Privacidade e Contato.
- Privacidade: resumo inclui prévias automáticas; finalidades descritas por operação; responsabilidades e limites de retenção mais claros; removida narrativa interna de implantação do site.
- Termos: oito efeitos identificados conforme `EFEITOS_PREMIUM` em `PremiumScreen.kt`; quinze estilos pagos e pixel gratuito conforme `Estilos`/`EstiloEfeito` em `Estilo.kt`. Estilos não concedem arte com nome semelhante. A descrição corresponde ao código, sem comprovar renderização no aparelho ou oferta ativa no Console.
- Contato: foco em como pedir ajuda, dados necessários, retenção e direitos. Instruções de conferência das contas ficam no guia interno.
- Preservados direitos de atendimento/reembolso, separação Premium/artes, ausência de promessa sobre efeitos futuros e estado de rascunho.

## Instruções internas que não pertencem às páginas públicas

- Gabriel/responsável pelo Console: cadastrar e conferir cada conta de licença e a indicação de instrumento de teste no checkout. Não basta incluir a pessoa na faixa fechada. Manter e-mails e comprovantes fora do repositório público.
- Registrar quem pode acessar a caixa de suporte e executar a exclusão de atendimento encerrado após 60 dias, com exceções justificadas e documentadas. O app não executa essa rotina.
- Conferir as configurações de Google Sites, MET, Google Play/Gmail e Cloudflare, incluindo logs, exportações, retenção e acesso. Não preencher lacunas com prazos presumidos.
- Conferir origem e autorização de uso de artes, sprites, fontes e dados meteorológicos do candidato; registrar evidências antes da oferta aprovada. Remover essa tarefa dos Termos não a conclui.
- Avaliar designação/formalização de encarregado ou enquadramento aplicável; Rafael é o responsável e canal confirmado, sem presumir dispensa legal.

## Decisões e evidências para fechar a etapa 5

| Pendência | Evidência necessária | Quem pode encaminhar |
|---|---|---|
| Bases legais por operação | Mapa de finalidade, dados, fundamento e procedimento; análise jurídica da localização e do atendimento | Responsável com revisão jurídica |
| Adolescentes/13+ | Público e participantes efetivos, enquadramento e medidas aplicáveis antes do teste com adolescentes | Responsável com revisão jurídica |
| Fornecedores e transferências | Configurações, papéis, garantias/mecanismos e retenção sob controle da equipe | Administradores das contas e revisão jurídica |
| Continuidade das compras | Procedimento de manutenção de arquivos, restauração, indisponibilidade e eventual encerramento | Responsável pelo produto/infraestrutura |
| Efeitos futuros do Premium | Regra comercial expressa; não incluídos por inferência nesta revisão | Rafael e responsável pelo produto |
| Licenças/origem | Registros por material efetivamente distribuído | Responsável pelo acervo |
| Oferta atual | Lista de efeitos/estilos comparada ao candidato e aos produtos/ofertas do Console | Gabriel e Rafael |
| Aprovação e vigência | Registro real de aprovação, data e versão do candidato em `revisao.json`; metadados coerentes | Responsável pela aprovação e Gabriel |
| Publicação | Republicar Privacidade, Termos e Contato e comparar texto/versões sem login, inclusive no celular | Administrador do Google Sites |

## Conferência e entrega

Executar `python scripts/sincronizar_documentos_legais.py` e depois `--check`; conferir as nove cópias HTML e o manifesto. Não editar HTMLs isolados. O gerador não publica no Google Sites, não aprova documentos e não atualiza aplicativos já instalados.

As fontes locais desta entrega são diferentes das versões 1.9/1.5/1.2 informadas como publicadas no site. Não declarar equivalência com a página pública até republicação e conferência. O roteiro de teste fechado permanece em [PREPARACAO-LEGAL-TESTE-FECHADO-2026-10-14.md](PREPARACAO-LEGAL-TESTE-FECHADO-2026-10-14.md).
