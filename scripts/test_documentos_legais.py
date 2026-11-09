"""Contrato editorial em árvore sintética; nenhuma fonte real é alterada."""
import contextlib
import io
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import sincronizar_documentos_legais as legal


class ApprovalTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        folder = self.root / 'docs/legal'
        folder.mkdir(parents=True)
        (folder / 'modelo.html').write_text('{{title}}{{body}}')
        self.meta = dict(status='aprovado_para_publicacao', aprovado_para_publicacao=True,
                         aprovacao_registro='https://github.com/example/repo/issues/1#issuecomment-1',
                         vigencia=None, versao_app=None, dominio_publico='https://example.invalid')
        self.source = ('# Teste\n**Versão:** 1.0\n'
                       '**Estado:** Documento em revisão; ainda não aprovado para publicação definitiva.\n'
                       '**Vigência:** A definir\n\n## Resumo\nResumo sintético.\n\n## 1. Conteúdo\nCorpo preservado.\n')
        for name in legal.DOCS.values():
            (folder / (name + '.md')).write_text(self.source)

    def generate(self, check=False):
        (self.root / 'docs/legal/revisao.json').write_text(json.dumps(self.meta))
        with patch.object(legal, 'ROOT', self.root), contextlib.redirect_stdout(io.StringIO()):
            legal.gerar(check)

    def test_approved_without_date_preserves_sources_and_marks_pending(self):
        self.generate()
        for name in legal.DOCS.values():
            self.assertEqual((self.root / 'docs/legal' / (name + '.md')).read_text(), self.source)
        content = (self.root / 'docs/privacidade/index.html').read_text()
        self.assertIn('Redação aprovada para publicação', content)
        self.assertIn('pendente do evento real', content)
        self.assertIn('Corpo preservado.', content)
        manifest = json.loads((self.root / 'docs/legal/manifesto-documentos.json').read_text())
        self.assertTrue(manifest['aprovado_para_publicacao'])
        self.assertIsNone(manifest['vigencia'])
        self.assertEqual(sum(1 for _ in self.root.rglob('index.html')), 9)

    def test_check_never_writes_and_rejects_changed_copy(self):
        self.generate()
        before = {p: p.read_bytes() for p in self.root.rglob('*') if p.is_file()}
        self.generate(check=True)
        self.assertEqual(before, {p: p.read_bytes() for p in self.root.rglob('*') if p.is_file()})
        p = self.root / 'public_html/termos/index.html'
        p.write_text('divergente')
        with self.assertRaises(ValueError):
            self.generate(check=True)
        self.assertEqual(p.read_text(), 'divergente')

    def test_draft_still_renders_as_draft(self):
        self.meta.update(status='rascunho_para_revisao', aprovado_para_publicacao=False,
                         aprovacao_registro=None)
        self.generate()
        self.assertIn('Documento em revisão', (self.root / 'docs/contato/index.html').read_text())

    def test_invalid_metadata_is_rejected(self):
        original = dict(self.meta)
        for change in [dict(aprovado_para_publicacao='false'), dict(aprovacao_registro=None),
                       dict(aprovacao_registro=''), dict(aprovacao_registro='aprovo'),
                       dict(dominio_publico='http://example.invalid'), dict(status='aprovado'),
                       dict(vigencia='2026-02-30'), dict(versao_app='8')]:
            with self.subTest(change=change):
                self.meta = dict(original, **change)
                with self.assertRaises(ValueError):
                    self.generate()

    def test_effective_state_requires_real_date_version_and_aligned_headers(self):
        self.meta.update(status='aprovado', vigencia='2026-11-09', versao_app='teste')
        with self.assertRaises(ValueError):
            self.generate()
        folder = self.root / 'docs/legal'
        for name in legal.DOCS.values():
            (folder / (name + '.md')).write_text(self.source.replace(
                'Documento em revisão; ainda não aprovado para publicação definitiva.',
                'Aprovado').replace('A definir', '2026-11-09'))
        self.generate()
        self.assertIn('Vigência: 2026-11-09', (self.root / 'docs/privacidade/index.html').read_text())
