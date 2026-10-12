# Encaminhamento ao Huppes — 11/10/2026

Branch de trabalho: `fix/premium-privacidade-etapas-1-2`. PR #50 ainda em rascunho, sem merge. Fonte de acompanhamento: [checklist de finalização](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md).

```bash
git fetch origin
git switch fix/premium-privacidade-etapas-1-2
git pull --ff-only
```

Se houver alterações locais, preservá-las antes de trocar/atualizar a branch. Para trabalhar em paralelo, criar uma branch a partir desta revisão e indicar a base no PR.

## Material pronto para avaliação

- Etapas 1/2: Premium independente das artes, suporte oficial, backup e clima/logs. Roteiro em `PROMPT-RAFAEL-AVALIACAO-ETAPAS-1-2-E-CATALOGO-2026-10-10.md`.
- Etapas 3/4: 71 cenas/366 artes, miniaturas locais, nove artes HD nativas e entrega R2 solicitada. R8 9.1.29 integrado ao build e CI. Roteiro em `ADAPTACAO-R2-R8-2026-10-11.md`.
- Etapa 5: termos 1.4, privacidade 1.7 e contato 1.1 refinados, sincronizados com nove HTMLs e testados com o importador de Willian. Continuam rascunhos; ver `ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md`.
- Registros antigos agora versionados por solicitação do usuário: prompt de revisão, análise de retomada, brief, consolidação de 04/10 e rascunhos de 05/10. Estão marcados como históricos; não são a fonte da oferta ou dos documentos atuais.

CI da revisão `6dffa5a` aprovada. A adaptação R2/R8 passou em 89 testes, lint e builds locais; etapa 5 passou nos checks específicos. Isso não representa aceite no aparelho ou pela Play.

## Próximas ações do Rafael

1. Avaliar no aparelho os fluxos das etapas 1/2 e a adaptação R2: variantes adicionais das cenas nativas, cenas remotas, efeitos e as 54 variantes sem parâmetros específicos declarados no motor. Registrar aparelho/Android, revisão, resultado e evidência.
2. Conferir produtos, compra individual, pendência, confirmação, restauração/reembolso, download/cancelamento/falha e uso offline pela faixa de teste. O catálogo contém 361 SKUs pagos propostos; não foi comprovado seu cadastro na Play.
3. Fechar regras de efeitos futuros, continuidade/versionamento do conteúdo, logs/contratos do R2 e revisão dos documentos, público e licenças. Domínio e vigência/versão dos documentos ainda pendentes; não publicar os rascunhos como definitivos.
4. Conferir no Play Console o maior `versionCode`, incluindo outras faixas e rascunhos. Gabriel informou que ainda não sabe esse número. O projeto segue com 6/1.0.5; não selecionar código novo por suposição.
5. Após aprovar a revisão e definir a configuração, gerar um único novo candidato AAB, registrar commit/hash/certificado/mapping e testar o artefato pela Play. Arquivos antigos em `build/outputs` não são candidatos desta revisão só por existirem.

Não houve novo AAB, envio ao Console, publicação do site ou alterações no bucket nesta passagem. Arquivos de assinatura, senhas e pacotes de build permanecem fora do Git. Testes em aparelho ficam com Rafael, conforme combinado.
