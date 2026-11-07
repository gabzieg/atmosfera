#!/usr/bin/env python3
"""Gate sem rede das regressões Play; em 06/11/2026 (#73) elas não rodavam na CI."""
import sys
import unittest
from pathlib import Path


def main():
    directory = Path(__file__).resolve().parent
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    for pattern in ("test_cadastrar_produtos_play.py", "test_preparar_produtos_play.py"):
        tests = loader.discover(str(directory), pattern=pattern)
        if tests.countTestCases() == 0:
            print(f"ERRO: nenhuma regressão encontrada em {pattern}; restaure a suíte.", file=sys.stderr)
            return 2
        suite.addTests(tests)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    return 0 if result.wasSuccessful() else 1


if __name__ == "__main__":
    sys.exit(main())
