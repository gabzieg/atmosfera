"""Recorta e confere apenas a composição local; não acessa rede ou bucket.

#86, 10/11/2026: estacionamento/natal estavam ocultos, mas ainda no pacote.
"""
import argparse
import hashlib
import json
from pathlib import Path
import re
import shutil

NAO_FINALIZADAS = frozenset({'estacionamento', 'natal', 'eiffel', 'ogro', 'timessquare'})
EXCLUIDAS = frozenset({'simpsons', 'budokai', 'konoha', 'timessquare', 'burj', 'ogro', 'eiffel'})
FORA_DO_CANDIDATO = NAO_FINALIZADAS | EXCLUIDAS
SPRITES_VEDADOS = frozenset({'pixel_retro', 'pixel_retro_2'})
NATIVAS = {('cabana', 'pixel'), ('bruxa', 'pixel'), ('bruxa', 'clay'),
           ('lavanda', 'pixel'), ('lavanda', 'aqua'), ('esfinge', 'pixel'),
           ('esfinge', 'vangogh'), ('jardim', 'pixel'), ('jardim', 'ukiyoe')}
GRATIS = {('cabana', 'pixel'), ('bruxa', 'clay'), ('lavanda', 'aqua'),
          ('esfinge', 'vangogh'), ('jardim', 'ukiyoe')}
ROOT = Path(__file__).resolve().parents[1]


def assets(root):
    return root / 'android-app/app/src/main/assets'


def caminho_vedado(path):
    tokens = set(re.split(r'[/.__]+', path))
    return bool(tokens & FORA_DO_CANDIDATO) or any(
        re.search(r'(^|[/_.])' + re.escape(sprite) + r'([/_.]|$)', path)
        for sprite in SPRITES_VEDADOS)


def recortar(root):
    """Operação explícita: preserva sobreviventes e remove só itens vedados."""
    base = assets(root)
    folder = base / 'atmosfera/catalogo'
    manifest = json.loads((folder / 'manifest.json').read_text())
    arts = json.loads((folder / 'artes.json').read_text())
    manifest['cenas'] = {k: v for k, v in manifest['cenas'].items() if k not in FORA_DO_CANDIDATO}
    arts = [a for a in arts if a['cena'] not in FORA_DO_CANDIDATO]
    for name, data in [('manifest.json', manifest), ('artes.json', arts)]:
        (folder / name).write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n')
    for path in sorted(base.rglob('*'), key=lambda p: len(p.parts), reverse=True):
        if caminho_vedado(path.relative_to(base).as_posix()):
            if path.is_dir():
                shutil.rmtree(path)
            else:
                path.unlink()


def conferir(root):
    base = assets(root)
    folder = base / 'atmosfera/catalogo'
    manifest = json.loads((folder / 'manifest.json').read_text())
    arts = json.loads((folder / 'artes.json').read_text())
    scenes = manifest['cenas']
    if FORA_DO_CANDIDATO & scenes.keys():
        raise ValueError('Cena vedada no manifesto')
    pairs = {(s, a) for s, c in scenes.items() for a in c['artes']}
    indexed = {(a['cena'], a['arte']) for a in arts}
    if len(indexed) != len(arts) or indexed != pairs or not NATIVAS <= pairs:
        raise ValueError('Pares divergentes, duplicados ou nativas ausentes')
    if any(a in SPRITES_VEDADOS for _, a in pairs):
        raise ValueError('Sprite vedado no índice')
    for scene, content in scenes.items():
        info = content['cena']
        if info['arquivo'] != f"pack/{scene}/_cena.{info['sha256']}.zip":
            raise ValueError('Pack comum diverge da cena')
        for variant, items in content['artes'].items():
            for kind in ['pack', 'thumb', 'preview']:
                info = items[kind]
                stem = f'pack/{scene}/{variant}' if kind == 'pack' else f'{kind}/{scene}__{variant}'
                extension = 'zip' if kind == 'pack' else 'webp'
                if info['arquivo'] != f"{stem}.{info['sha256']}.{extension}":
                    raise ValueError('Referência diverge do par cena/arte')
    thumbs = set()
    for art in arts:
        scene, variant = art['cena'], art['arte']
        thumb = f'atmosfera/miniaturas/{scene}__{variant}.webp'
        if art['miniatura'] != thumb or type(art['embarcada']) is not bool or art['embarcada'] != ((scene, variant) in NATIVAS):
            raise ValueError('Miniatura ou marca nativa divergente')
        expected_product = '' if (scene, variant) in GRATIS else f'terra_arte_{scene}_{variant}'
        if art['produto'] != expected_product:
            raise ValueError('Produto diverge do par')
        info = scenes[scene]['artes'][variant]['thumb']
        data = (base / thumb).read_bytes()
        if len(data) != info['bytes'] or hashlib.sha256(data).hexdigest() != info['sha256']:
            raise ValueError('Miniatura ausente/corrompida')
        thumbs.add(thumb)
    actual_thumbs = {p.relative_to(base).as_posix() for p in (base / 'atmosfera/miniaturas').rglob('*') if p.is_file()}
    if thumbs != actual_thumbs:
        raise ValueError('Miniaturas físicas divergem dos índices')
    for path in base.rglob('*'):
        if caminho_vedado(path.relative_to(base).as_posix()):
            raise ValueError('Asset físico vedado: ' + path.relative_to(base).as_posix())
    native_folder = base / 'atmosfera/cenas'
    actual_native = {(s.name, a.name) for s in native_folder.iterdir() if s.is_dir()
                     for a in s.iterdir() if a.is_dir()}
    if actual_native != NATIVAS:
        raise ValueError('Artes nativas físicas divergem do conjunto')
    for scene, variant in NATIVAS:
        for name in ['fundo', 'frente', 'zonas']:
            folders = [native_folder / scene / variant]
            if name == 'zonas':
                folders.append(native_folder / scene)
            if not any((p / (name + ext)).is_file() for p in folders for ext in ['.png', '.webp']):
                raise ValueError('Camada nativa ausente: ' + scene + '/' + variant + '/' + name)
    def descriptors(value):
        if isinstance(value, dict):
            if 'arquivo' in value:
                yield value
            for child in value.values():
                yield from descriptors(child)
    references = list(descriptors(scenes))
    for info in references:
        match = re.fullmatch(r'(pack/[a-z0-9_]+/[a-z0-9_]+|(?:thumb|preview)/[a-z0-9_]+)\.([a-f0-9]{64})\.(zip|webp)', info['arquivo'])
        if (not match or match[2] != info['sha256'] or caminho_vedado(info['arquivo'])
                or (match[1].startswith('pack/') != (match[3] == 'zip'))
                or type(info['bytes']) is not int or not 0 < info['bytes'] <= (128 if match[3] == 'zip' else 4) * 1024 * 1024):
            raise ValueError('Referência remota inválida')
    return dict(cenas=len(scenes), artes=len(pairs), miniaturas=len(thumbs),
                nativas=len(actual_native), referencias=len(references),
                indices={name: hashlib.sha256((folder / name).read_bytes()).hexdigest()
                         for name in ['manifest.json', 'artes.json']})


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apply', action='store_true', help='recortar antes de conferir; padrão só lê')
    args = parser.parse_args()
    if args.apply:
        recortar(ROOT)
    print(json.dumps(conferir(ROOT), ensure_ascii=False, indent=2))
