#!/bin/bash
# Liga os hooks versionados em .githooks/ neste clone.
# Uso: ./scripts/setup-hooks.sh   (uma vez por clone, por pessoa)
set -e

cd "$(dirname "$0")/.."

# core.hooksPath APONTA para .githooks/ em vez de copiar pra .git/hooks/.
# Cópia congela: quem instalou ontem fica com a versão de ontem e ninguém
# percebe. Apontando, todo mundo roda o que está versionado, sempre.
git config core.hooksPath .githooks

# Permissão de execução não sobrevive a clone no Windows; o bit fica no índice.
chmod +x .githooks/* 2>/dev/null || true

echo "✅ core.hooksPath = .githooks"
echo ""
echo "Hooks ativos:"
for h in .githooks/*; do
  [ -f "$h" ] && echo "  - $(basename "$h")"
done
echo ""
echo "Desligar: git config --unset core.hooksPath"
