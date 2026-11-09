"""Contrato do catálogo com dados sintéticos; não importa nem executa o uploader."""
import hashlib
import unittest

from catalog import prepare_catalog


class CatalogTest(unittest.TestCase):
    def setUp(self):
        self.data = b'conteudo sintetico'
        self.sha = hashlib.sha256(self.data).hexdigest()

    def prepare(self, path='pack/cena/arte.zip', sha=None, size=None):
        item = dict(arquivo=path, bytes=len(self.data) if size is None else size,
                    sha256=self.sha if sha is None else sha)
        source = dict(cenas={'cena': dict(cena=item, artes={})})
        result, files = prepare_catalog(source, lambda _: self.data)
        self.assertEqual(source['cenas']['cena']['cena'], item)
        return result['cenas']['cena']['cena'], files

    def test_expands_legacy_hash_for_android(self):
        item, _ = self.prepare(sha=self.sha[:12])
        self.assertEqual(item['sha256'], self.sha)

    def test_versioned_name_is_stable(self):
        path = f'pack/cena/arte.{self.sha}.zip'
        item, files = self.prepare(path=path)
        self.assertEqual(item['arquivo'], path)
        self.assertEqual(files[path], path)

    def test_unversioned_name_gets_full_hash(self):
        item, files = self.prepare()
        self.assertEqual(item['arquivo'], f'pack/cena/arte.{self.sha}.zip')
        self.assertEqual(files[item['arquivo']], 'pack/cena/arte.zip')

    def test_wrong_hash_is_rejected(self):
        with self.assertRaises(ValueError):
            self.prepare(sha='0' * 64)

    def test_wrong_size_is_rejected(self):
        with self.assertRaises(ValueError):
            self.prepare(size=1)

    def test_empty_hash_is_rejected(self):
        with self.assertRaises(ValueError):
            self.prepare(sha='')

    def test_versioned_name_must_match_content(self):
        with self.assertRaises(ValueError):
            self.prepare(path='pack/cena/arte.' + '0' * 64 + '.zip')

    def test_paths_outside_contract_are_rejected(self):
        for path in ['../fora.zip', '/tmp/fora.zip', 'pack/cena/arte.extra.zip']:
            with self.subTest(path=path), self.assertRaises(ValueError):
                self.prepare(path=path)

    def test_preview_uses_android_name_and_size_limits(self):
        item, _ = self.prepare(path='preview/cena__arte.webp')
        self.assertEqual(item['arquivo'], f'preview/cena__arte.{self.sha}.webp')
        data = b'x' * (4 * 1024 * 1024 + 1)
        with self.assertRaises(ValueError):
            prepare_catalog({'cenas': {'c': {'preview': dict(arquivo='preview/c__a.webp',
                bytes=len(data), sha256=hashlib.sha256(data).hexdigest())}}}, lambda _: data)

    def test_metadata_and_historical_exclusions_are_preserved(self):
        source = dict(versao=7, formato='webp', cenas={'simpsons': {}, 'cena': {'artes': {}}})
        result, files = prepare_catalog(source, lambda _: self.data)
        self.assertEqual(result, dict(versao=7, formato='webp', cenas={'cena': {'artes': {}}}))
        self.assertIn('simpsons', source['cenas'])
        self.assertEqual(files, {})


if __name__ == '__main__':
    unittest.main()
