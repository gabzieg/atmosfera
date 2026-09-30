#!/bin/bash
# Build command do Cloudflare Pages — ver "Como hospedar" em
# docs/dev/CHECKLIST_PUBLICACAO.md.
#
# Lista de permissão, não de exclusão: só copia as 3 pastas que devem virar
# URL pública. Ao contrário do GitHub Pages, o Cloudflare Pages não lê
# `_config.yml` (é config do Jekyll) — sem este script, apontar `docs/` direto
# como saída publicaria `dev/` e `legal/` junto, incluindo o GUIA_PLAY_CONSOLE
# (respostas do Data Safety) e o caminho da keystore citado no ROADMAP.
set -e
rm -rf _site
mkdir -p _site
cp -r privacidade _site/
cp -r termos _site/
cp -r contato _site/
