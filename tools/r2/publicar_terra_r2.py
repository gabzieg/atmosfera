"""Publica o catálogo revisado no R2: conteúdo versionado, índice por último.

Executar por publicar_terra_r2.ps1. Sem exclusão de objetos remotos.
"""
import concurrent.futures
import argparse
import hashlib
import json
import os
from pathlib import Path
import sys

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--dist', required=True, type=Path, help='Diretório do acervo com manifest.json e arquivos originais')
parser.add_argument('--output', required=True, type=Path, help='Diretório local para manifesto versionado e relatório')
parser.add_argument('--dry-run', action='store_true', help='Validar e preparar manifesto sem acessar o R2 nem ler credenciais')
args = parser.parse_args()
source = args.dist.resolve()
args.output.mkdir(parents=True, exist_ok=True)
manifest = json.loads((source / 'manifest.json').read_text(encoding='utf-8'))
excluded = {'simpsons', 'budokai', 'konoha', 'burj'}
manifest['cenas'] = {k: v for k, v in manifest['cenas'].items() if k not in excluded}
files = {}

def collect(value):
    if isinstance(value, dict):
        if 'arquivo' in value:
            original = (source / value['arquivo']).resolve()
            if not original.is_relative_to(source):
                raise ValueError('Arquivo fora do diretório do acervo')
            data = original.read_bytes()
            digest = hashlib.sha256(data).hexdigest()
            if len(data) != value['bytes'] or not digest.startswith(value['sha256']):
                raise ValueError('Integridade inválida: ' + value['arquivo'])
            relative = Path(value['arquivo'])
            # Cada alteração gera outra URL, compatível com cache immutable.
            key = (relative.parent / (relative.stem + '.' + digest + relative.suffix)).as_posix()
            value['arquivo'] = key
            files[key] = original
        for child in value.values():
            collect(child)
    elif isinstance(value, list):
        for child in value:
            collect(child)

collect(manifest)
data = json.dumps(manifest, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
if args.dry_run:
    (args.output / 'manifest.json').write_bytes(data)
    print(json.dumps({'dry_run': True, 'scenes': len(manifest['cenas']), 'art_variants': sum(len(c['artes']) for c in manifest['cenas'].values()), 'catalog_files': len(files) + 1, 'catalog_bytes': sum(p.stat().st_size for p in files.values()) + len(data), 'excluded_scenes': sorted(excluded)}, ensure_ascii=False))
    sys.exit(0)
import boto3
from botocore.config import Config
cfg = {key: os.environ[key] for key in ['R2_ACCOUNT_ID', 'R2_BUCKET', 'R2_ACCESS_KEY_ID', 'R2_SECRET_ACCESS_KEY']}
s3 = boto3.client('s3', endpoint_url='https://' + cfg['R2_ACCOUNT_ID'] + '.r2.cloudflarestorage.com', aws_access_key_id=cfg['R2_ACCESS_KEY_ID'], aws_secret_access_key=cfg['R2_SECRET_ACCESS_KEY'], region_name='auto', config=Config(retries={'max_attempts': 5, 'mode': 'standard'}, max_pool_connections=8))
bucket = cfg['R2_BUCKET']
remote = {}
for page in s3.get_paginator('list_objects_v2').paginate(Bucket=bucket):
    remote.update({o['Key']: o for o in page.get('Contents', [])})

def upload(item):
    key, path = item
    data = path.read_bytes()
    md5 = hashlib.md5(data).hexdigest()
    old = remote.get(key)
    if not old or old['Size'] != len(data) or old['ETag'].strip('"') != md5:
        s3.put_object(Bucket=bucket, Key=key, Body=data, ContentType='application/zip' if path.suffix == '.zip' else 'image/webp', CacheControl='public, max-age=31536000, immutable')
        changed = True
    else:
        changed = False
    head = s3.head_object(Bucket=bucket, Key=key)
    if head['ContentLength'] != len(data) or head['ETag'].strip('"') != md5:
        raise ValueError('Objeto remoto divergente: ' + key)
    if head.get('CacheControl') != 'public, max-age=31536000, immutable':
        raise ValueError('Cache remoto divergente: ' + key)
    return changed

sent = 0
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:
    for count, changed in enumerate(pool.map(upload, files.items()), 1):
        sent += changed
        if count % 50 == 0 or count == len(files):
            print(f'{count}/{len(files)} conferidos; {sent} enviados', flush=True)
data = json.dumps(manifest, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
old = remote.get('manifest.json')
if not old or old['ETag'].strip('"') != hashlib.md5(data).hexdigest():
    s3.put_object(Bucket=bucket, Key='manifest.json', Body=data, ContentType='application/json', CacheControl='public, max-age=300')
if s3.get_object(Bucket=bucket, Key='manifest.json')['Body'].read() != data:
    raise ValueError('Manifesto remoto divergente')
published = args.output / 'manifest.json'
published.write_bytes(data)
report = {'bucket': bucket, 'storage_class': 'Standard', 'scenes': len(manifest['cenas']), 'art_variants': sum(len(c['artes']) for c in manifest['cenas'].values()), 'catalog_files': len(files) + 1, 'catalog_bytes': sum(p.stat().st_size for p in files.values()) + len(data), 'excluded_scenes': sorted(excluded), 'remote_upload': 'complete; all catalog objects verified by size and MD5/ETag; manifest downloaded and compared byte for byte', 'uploaded_assets_this_run': sent, 'public_access': 'not checked by this uploader', 'unreferenced_objects_preserved': sorted(set(remote) - set(files) - {'manifest.json'})}
report_path = args.output / 'relatorio.json'
if report_path.exists():
    previous = json.loads(report_path.read_text(encoding='utf-8'))
    for field in ('public_access', 'public_base_url', 'public_manifest_url'):
        if field in previous:
            report[field] = previous[field]
    if 'public_verification' in previous:
        report['previous_public_verification'] = previous['public_verification']
report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding='utf-8')
print(json.dumps(report, ensure_ascii=False), flush=True)
