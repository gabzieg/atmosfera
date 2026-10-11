# Plano de lançamento — atualizado em 11/10/2026

Roteiro vigente: [checklist de finalização](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md). A versão candidata ainda não está aprovada nem publicada. Plano anterior preservado em `historico/anterior-a-2026-10-11/PLANO-LANCAMENTO.md`; suas ofertas, estados de teste e números não são o estado atual.

## Oferta e conteúdo

- Cinco cenas nativas, nove artes HD e cinco amostras gratuitas. Acervo R2 conciliado: 71 cenas/366 variantes após cortes, com miniaturas locais e download escolhido.
- Premium: efeitos/estilos da oferta, preço-base R$ 49,90. Artes individuais: R$ 1,99, promoções configuradas na Play; 361 SKUs propostos, cadastro atual não comprovado. Não há packs comerciais. Cada arte nova é compra separada; efeitos futuros aguardam decisão.
- As implementações locais das etapas 1–4 estão na PR #50. Aceite visual, jornada pela Play, catálogo anunciado e produção ainda dependem de evidência.

## Sequência até publicar

1. Rafael avaliar Premium separado das artes, clima só na home, backup e suporte conforme roteiros das etapas 1/2.
2. Revisar a adaptação R2/R8 e conferir todas as artes anunciadas, incluindo as 54 variantes sem parâmetros específicos declarados no motor local.
3. Configurar/conferir produtos, ofertas, países e jornada compra → download → aplicação → reinício → restauração. Testar falha, cancelamento, falta de espaço, reembolso e offline.
4. Definir endpoint de produção, continuidade/versionamento dos objetos e acesso/retenção de logs. O domínio `r2.dev` é de teste.
5. Aprovar os textos da [etapa 5](ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md), processos, licenças, público e obrigações legais; definir vigência, versão e domínio. Willian importa a revisão identificada e publica páginas conferidas, preservando sua apresentação.
6. Conferir próximo `versionCode`, gerar um único AAB da revisão aprovada e registrar commit, hash, certificado e mapping R8. Nenhum AAB novo foi gerado pelas etapas 4/5.
7. Validar o artefato pela Play e cumprir o teste fechado/requisitos aplicáveis à conta. Não tratar teste interno como aprovação para produção.
8. Concluir ficha e declarações do Console com os fluxos reais de MET, Google, R2 e atendimento, sem presumir ausência de tratamento por falta de login.
9. Solicitar/publicar quando aprovado; registrar data real e iniciar [operação pós-lançamento](OPERACAO-POS-LANCAMENTO.md).

Não fixar data comercial antes de resolver essas pendências. Builds e hashes comprovam artefatos, não parecer jurídico, aceite visual ou entrega comercial.
