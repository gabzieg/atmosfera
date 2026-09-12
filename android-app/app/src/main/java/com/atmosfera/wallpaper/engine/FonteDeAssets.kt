package com.atmosfera.wallpaper.engine

import java.io.InputStream

/**
 * De onde o motor lê os arquivos de um cenário (fundo, frente, sprites, zonas).
 *
 * **Por que existe.** Até 2026-08 o motor recebia um `AssetManager` direto e só
 * sabia ler o que estava embarcado no APK. Conteúdo pago não pode embarcar (ver
 * `docs/dev/ROADMAP.md` → Fase 4): ele baixa sob demanda via Play Asset
 * Delivery, e asset pack "on-demand" **não aparece** em `context.assets` — vive
 * num armazenamento à parte, lido pelo `AssetPackManager`, que devolve caminho
 * de arquivo.
 *
 * Uma interface de um método só resolve isso sem contaminar o motor: ele
 * continua pedindo "abre este caminho e me dá um stream", exatamente como fazia,
 * e **nunca descobre que asset pack existe**. Quem decide a origem é o front,
 * que é onde a compra e o download já vivem.
 *
 * Passar um caminho de arquivo em vez desta interface pareceria mais simples,
 * mas obrigaria o motor a ramificar entre "ler do APK" e "ler do disco" — ou
 * seja, conceito de loja dentro do render.
 *
 * Efeito colateral útil: uma implementação falsa permite exercitar
 * [EffectEngine.carregar] **sem aparelho**, coisa que era impossível enquanto o
 * parâmetro era um `AssetManager`.
 *
 * @see com.atmosfera.wallpaper.comoFonte para a implementação do conteúdo
 *   embarcado no APK.
 */
fun interface FonteDeAssets {
    /**
     * Abre [caminho] para leitura. O caminho é o mesmo que o motor sempre usou,
     * relativo à raiz do conteúdo — por exemplo `atmosfera/cenas/farol/fundo.png`.
     * Cabe à implementação mapear isso para onde o arquivo realmente está.
     *
     * Deve lançar se o arquivo não existir: o motor trata ausência de asset
     * opcional com `try/catch` no ponto de uso, e ausência de asset obrigatório
     * precisa mesmo falhar o carregamento.
     */
    fun abrir(caminho: String): InputStream
}
