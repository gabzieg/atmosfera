import contextlib
import io
import json
import shutil
from pathlib import Path
import tempfile
import unittest
from unittest.mock import MagicMock, Mock, patch

import disparar_site_legal as site
import sincronizar_documentos_legais as legal


class TriggerTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        (self.root / 'docs/legal').mkdir(parents=True)
        self.meta = dict(aprovado_para_publicacao=True, status='aprovado',
                         aprovacao_registro='https://example.invalid/aceite', vigencia='2026-11-09',
                         versao_app='teste', dominio_publico='https://example.invalid')
        self.hook = 'https://api.cloudflare.com/client/v4/pages/webhooks/deploy_hooks/synthetic'
        self.post = Mock(return_value=200)
        self.validate = Mock()
        self.messages = []

    def write(self, manifest=None):
        base = self.root / 'docs/legal'
        (base / 'revisao.json').write_text(json.dumps(self.meta))
        (base / 'manifesto-documentos.json').write_text(json.dumps(
            dict(self.meta, documentos={}) if manifest is None else manifest))

    def run_trigger(self, hook=None):
        self.write()
        return site.trigger(self.root, self.hook if hook is None else hook,
                            validate=self.validate, post=self.post, emit=self.messages.append)

    def test_approved_posts_once(self):
        self.assertEqual(self.run_trigger(), 'requested')
        self.post.assert_called_once_with(self.hook)
        self.validate.assert_called_once_with(self.root)

    def test_editorial_null_posts_once(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia=None, versao_app=None)
        self.assertEqual(self.run_trigger(), 'requested')
        self.post.assert_called_once_with(self.hook)

    def test_editorial_null_missing_hook_skips(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia=None, versao_app=None)
        self.assertEqual(self.run_trigger(''), 'missing_hook')
        self.post.assert_not_called()

    def test_editorial_with_invalid_date_fails_without_hook(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia='2026-02-30', versao_app=None)
        with self.assertRaises(ValueError):
            self.run_trigger('')
        self.post.assert_not_called()

    def test_non_public_approval_record_fails(self):
        self.meta['aprovacao_registro'] = 'aceite sem referencia'
        with self.assertRaises(ValueError):
            self.run_trigger('')
        self.post.assert_not_called()

    def test_draft_never_posts(self):
        self.meta.update(aprovado_para_publicacao=False, status='rascunho_para_revisao')
        self.assertEqual(self.run_trigger(), 'draft')
        self.post.assert_not_called()
        self.assertIn('SKIP', self.messages[0])

    def test_missing_hook_is_explicit_success(self):
        self.assertEqual(self.run_trigger(''), 'missing_hook')
        self.post.assert_not_called()
        self.assertIn('ausente', self.messages[0])

    def test_non_boolean_approval_fails(self):
        self.meta['aprovado_para_publicacao'] = 'false'
        with self.assertRaises(ValueError):
            self.run_trigger()
        self.post.assert_not_called()

    def test_missing_approval_record_fails_even_without_hook(self):
        self.meta['aprovacao_registro'] = None
        with self.assertRaises(ValueError):
            self.run_trigger('')
        self.post.assert_not_called()

    def test_manifest_disagreement_fails(self):
        self.write(dict(self.meta, aprovado_para_publicacao=False, documentos={}))
        with self.assertRaises(ValueError):
            site.trigger(self.root, self.hook, validate=self.validate, post=self.post)
        self.post.assert_not_called()

    def test_broken_exports_fail_even_when_draft(self):
        self.meta.update(aprovado_para_publicacao=False, status='rascunho_para_revisao')
        self.validate.side_effect = ValueError('divergência sintética')
        with self.assertRaises(ValueError):
            self.run_trigger()
        self.post.assert_not_called()

    def test_http_failure_fails_without_retry(self):
        self.post.return_value = 500
        with self.assertRaises(ValueError):
            self.run_trigger()
        self.post.assert_called_once()

    def test_transport_failure_fails_without_retry(self):
        self.post.side_effect = OSError('url sintética confidencial')
        with self.assertRaises(OSError):
            self.run_trigger()
        self.post.assert_called_once()

    def test_rejects_redirect_target_credentials_or_query(self):
        for url in ['http://api.cloudflare.com/x', 'https://other.invalid/x',
                    self.hook + '?token=x', self.hook.replace('https://', 'https://user:pass@')]:
            with self.subTest(url=url), self.assertRaises(ValueError):
                self.run_trigger(url)
        self.post.assert_not_called()

    def test_cli_error_does_not_print_exception_or_secret(self):
        output = io.StringIO()
        with patch.object(site, 'trigger', side_effect=OSError(self.hook)), contextlib.redirect_stdout(output):
            self.assertEqual(site.main(), 1)
        self.assertNotIn(self.hook, output.getvalue())
        self.assertNotIn('synthetic', output.getvalue())

    def test_exports_checked_by_real_generator_before_post(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia=None, versao_app=None)
        self.write()
        folder = self.root / 'docs/legal'
        (folder / 'modelo.html').write_text('{{title}}{{body}}')
        source = ('# Teste\n**Versão:** 1.0\n**Estado:** Em revisão\n'
                  '**Vigência:** A definir\n\n## Resumo\nSintético.\n\n## 1. Texto\nTeste.\n')
        for name in legal.DOCS.values():
            (folder / (name + '.md')).write_text(source)
        (self.root / 'scripts').mkdir()
        shutil.copy2(Path(legal.__file__), self.root / 'scripts/sincronizar_documentos_legais.py')
        with patch.object(legal, 'ROOT', self.root), contextlib.redirect_stdout(io.StringIO()):
            legal.gerar()
        self.assertEqual(site.trigger(self.root, self.hook, post=self.post), 'requested')
        self.post.assert_called_once_with(self.hook)
        self.post.reset_mock()
        (self.root / 'public_html/termos/index.html').write_text('divergente')
        with self.assertRaises(ValueError):
            site.trigger(self.root, '', post=self.post)
        self.post.assert_not_called()

    def test_transport_uses_one_post_and_timeout(self):
        response = Mock(status=200)
        opener = MagicMock()
        opener.open.return_value.__enter__.return_value = response
        with patch.object(site.urllib.request, 'build_opener', return_value=opener) as build:
            self.assertEqual(site.post_hook(self.hook), 200)
        self.assertIs(build.call_args.args[0], site.NoRedirect)
        opener.open.assert_called_once()
        request = opener.open.call_args.args[0]
        self.assertEqual(request.get_method(), 'POST')
        self.assertEqual(request.full_url, self.hook)
        self.assertEqual(opener.open.call_args.kwargs, {'timeout': 30})

    def test_redirect_is_refused(self):
        self.assertIsNone(site.NoRedirect().redirect_request(None, None, 302, '', {}, 'https://other.invalid'))

    def test_web_date_null_app_posts_once(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia='2026-11-10', versao_app=None)
        self.assertEqual(self.run_trigger(), 'requested')
        self.post.assert_called_once_with(self.hook)

    def test_web_date_null_app_missing_hook_is_explicit_skip(self):
        self.meta.update(status='aprovado_para_publicacao', vigencia='2026-11-10', versao_app=None)
        self.assertEqual(self.run_trigger(''), 'missing_hook')
        self.post.assert_not_called()
        self.assertIn('ausente', self.messages[0])
