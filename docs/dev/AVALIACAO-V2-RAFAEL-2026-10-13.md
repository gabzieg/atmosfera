# Integração da avaliação v2 de Rafael — 13/10/2026

Os patches da entrega local de 12/10 foram encontrados na máquina de Rafael e aplicados sobre o commit `8ec0993` da branch `fix/premium-privacidade-etapas-1-2`. Esta entrega incorpora o código funcional à revisão da PR #50, mantida em rascunho. O registro documental anterior permanece no histórico Git.

## Alterações integradas

- Prévia intermediária da variante aberta, com miniatura imediata e cancelamento ao trocar/sair. Selecionar uma variante só mostra a prévia; Aplicar muda o wallpaper.
- Cache separado do HD instalado, limite de 24 MB e limpeza após cinco minutos sem uso. A prévia aberta é protegida; se o processo encerrar, a limpeza física pode esperar a próxima abertura.
- Busca e posição de rolagem preservadas ao voltar à Loja; cadeados atualizados ao retornar do painel.
- Simular Premium e liberar artes são controles independentes, restritos a debug. Não simulam compras reais.
- Nomes de apresentação e agrupamentos Cabana Lago/Selva e Cyberpunk Canal/Rua. IDs, SKUs e direitos de cada variante permanecem separados.
- Privacidade 1.8 descreve prévias e cache; fonte, manifesto e três HTMLs correspondentes sincronizados. Termos/contato e demais exportações preservados. Documentos continuam rascunhos.

Os nomes dos atalhos, o pacote `.avaliacao`, seu versionCode local 7 e o destrave inicial exclusivo do APK de avaliação não foram integrados. O app mantém a configuração de versão existente; ela não foi conferida no Console.

## Catálogo e continuidade

O manifesto embarcado continua com 71 cenas/366 artes e suas miniaturas. A vitrine filtra temporariamente `estacionamento` e `natal`, quatro artes cada: 69 cenas/358 artes, cinco amostras e 353 artes pagas propostas. Os agrupamentos de duas famílias produzem 67 cards. `natalchale` e `natalsala` continuam disponíveis.

Não houve remoção no bucket, renomeação de produto ou revogação de compra por esta integração. O filtro retira as cenas do catálogo utilizável nesta revisão: não comprova continuidade de acesso a uma arte dessas cenas previamente adquirida. Conforme informação registrada pelo titular, só houve compras de teste; continuidade de conteúdo e oferta definitiva ainda precisam de aceite antes da produção. Produtos ativos, preços e países não foram alterados.

Navio, Órbita e Trem continuam para avaliação. Há 34 cenas disponíveis com mapeamento automático das zonas, sem marcação manual arquivada; isso não equivale a aprovação visual das demais cenas.

## Validação da integração

98 testes unitários em 20 classes, zero falhas/erros; lint debug sem erros; assembleDebug e assembleRelease aprovados com JDK 17. Conferência do gerador de documentos legais e git diff --check aprovadas. O primeiro empacotamento de recursos de testes falhou no caminho longo do checkout Windows; a execução completa passou ao redirecionar somente o diretório de build para uma pasta temporária curta. Nenhuma configuração de versão/assinatura foi alterada. R8 emitiu dois avisos de parsing síncrono do provedor AGP; sem falha de build. CI da nova revisão será conferida após o push.

## Pendências externas

**Reconferência após publicação:** commit `e9a221f` recuperado e incorporado à cópia local em 13/10. Build e secret-scan aprovados no GitHub ([execução](https://github.com/gabzieg/atmosfera/actions/runs/38403864423)). Reexecutados localmente 17 testes: catálogo (9), cache (3), download de prévias (3) e sincronização legal (2), todos sem falhas/erros. O gerador legal também passou em `--check`. Esta reconferência não repetiu os 98 testes/builds locais registrados acima nem substitui a avaliação no aparelho. PR segue em rascunho.

Avaliação no aparelho e pela Play: visual, clima, efeitos, direitos, compras/download/restauração, offline, cancelamento/falha e atualização. Produtos/ofertas, endpoint de produção, continuidade do conteúdo e catálogo final continuam pendentes. Revisão/aprovação jurídica, domínio/hospedagem e vigência dos documentos também permanecem pendentes.

Reflexos do ouro/Natal, carros variáveis, dragão opcional, vapor da Pizzaria e movimento no exterior do Trem estão registrados como melhorias futuras, não implementadas neste commit. Nenhum novo AAB, merge ou envio à Play foi realizado.
