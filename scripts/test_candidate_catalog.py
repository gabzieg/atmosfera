"""Composição real e controles sintéticos sem rede ou alteração do checkout."""
import copy
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import candidate_catalog as catalog
import sincronizar_acervo_r2 as importer


class CompositionTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.base = catalog.assets(self.root)
        self.folder = self.base / 'atmosfera/catalogo'
        self.folder.mkdir(parents=True)
        self.data = b'imagem sintetica'
        digest = hashlib.sha256(self.data).hexdigest()
        self.manifest = dict(versao=2, formato='webp', cenas={})
        self.arts = []
        for scene, variant in sorted(catalog.NATIVAS):
            info = dict(arquivo=f'thumb/{scene}__{variant}.{digest}.webp', bytes=len(self.data), sha256=digest)
            common = dict(info, arquivo=f'pack/{scene}/_cena.{digest}.zip')
            item = self.manifest['cenas'].setdefault(scene, dict(cena=common, artes={}))
            item['artes'][variant] = dict(thumb=info, preview=dict(info, arquivo=f'preview/{scene}__{variant}.{digest}.webp'),
                                         pack=dict(info, arquivo=f'pack/{scene}/{variant}.{digest}.zip'))
            thumb = f'atmosfera/miniaturas/{scene}__{variant}.webp'
            self.arts.append(dict(cena=scene, arte=variant, miniatura=thumb, embarcada=True,
                                  produto='' if (scene, variant) in catalog.GRATIS else f'terra_arte_{scene}_{variant}'))
            target = self.base / thumb
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(self.data)
            for layer in ['fundo', 'frente', 'zonas']:
                target = self.base / 'atmosfera/cenas' / scene / variant / (layer + '.webp')
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(self.data)
        self.write()

    def write(self):
        for name, value in [('manifest.json', self.manifest), ('artes.json', self.arts)]:
            (self.folder / name).write_text(json.dumps(value))

    def test_checkout_complies_with_candidate(self):
        catalog.conferir(catalog.ROOT)

    def test_valid_fixture_and_cut_are_idempotent(self):
        catalog.conferir(self.root)
        catalog.recortar(self.root)
        before = {p: p.read_bytes() for p in self.base.rglob('*') if p.is_file()}
        catalog.recortar(self.root)
        self.assertEqual(before, {p: p.read_bytes() for p in self.base.rglob('*') if p.is_file()})
        catalog.conferir(self.root)

    def test_both_indices_and_physical_reentry_are_rejected_then_cut(self):
        self.manifest['cenas']['natal'] = copy.deepcopy(self.manifest['cenas']['cabana'])
        self.write()
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)
        catalog.recortar(self.root)
        catalog.conferir(self.root)
        self.arts.append(dict(self.arts[0], cena='estacionamento'))
        self.write()
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)
        catalog.recortar(self.root)
        catalog.conferir(self.root)
        for relative in ['atmosfera/miniaturas/natal__pixel.webp', 'atmosfera/cenas/ogro/pixel/fundo.webp',
                         'atmosfera/sprites/pixel_retro_2.webp']:
            path = self.base / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(self.data)
            with self.assertRaises(ValueError):
                catalog.conferir(self.root)
            catalog.recortar(self.root)
            catalog.conferir(self.root)

    def test_pairs_duplicates_and_references_fail(self):
        original = copy.deepcopy(self.arts)
        for change in ['duplicate', 'missing', 'native', 'thumb', 'product']:
            self.arts = copy.deepcopy(original)
            if change == 'duplicate':
                self.arts.append(self.arts[0])
            elif change == 'missing':
                self.arts.pop()
            elif change == 'native':
                self.arts[0]['embarcada'] = False
            elif change == 'thumb':
                self.arts[0]['miniatura'] = '../fora.webp'
            else:
                self.arts[0]['produto'] = 'outro'
            self.write()
            with self.subTest(change=change), self.assertRaises(ValueError):
                catalog.conferir(self.root)

    def test_corrupted_thumbnail_missing_native_and_bad_hash_fail(self):
        thumb = self.base / self.arts[0]['miniatura']
        thumb.write_bytes(b'corrompido')
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)
        thumb.write_bytes(self.data)
        native = next((self.base / 'atmosfera/cenas').rglob('fundo.webp'))
        native.unlink()
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)
        native.write_bytes(self.data)
        self.manifest['cenas']['cabana']['cena']['sha256'] = '0' * 64
        self.write()
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)

    def test_reference_cannot_point_to_another_scene(self):
        info = self.manifest['cenas']['cabana']['artes']['pixel']['thumb']
        info['arquivo'] = info['arquivo'].replace('cabana__', 'jardim__')
        self.write()
        with self.assertRaises(ValueError):
            catalog.conferir(self.root)

    def test_importer_does_not_reintroduce_unfinished_families(self):
        for scene in catalog.NAO_FINALIZADAS:
            self.manifest['cenas'][scene] = copy.deepcopy(self.manifest['cenas']['cabana'])
        self.write()
        with patch.object(importer, 'RAIZ', self.root):
            importer.importar(self.folder / 'manifest.json', 'https://example.invalid/', baixar=False)
        imported = json.loads((self.folder / 'manifest.json').read_text())
        self.assertFalse(catalog.FORA_DO_CANDIDATO & imported['cenas'].keys())


if __name__ == '__main__':
    unittest.main()
