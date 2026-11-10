"""Audita AAB local e exporta somente recibo/artefatos permitidos, sem rede.

Não gera ou assina. Configuração privada e logs brutos permanecem no Windows.
"""
import argparse
import base64
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

APP_COMMIT = 'e45de4c81cccb88af8dd56ef0247b0931b6b33dd'
ENDPOINT = 'https://acervo.terrawallpaper.com.br/'
ANDROID = '{http://schemas.android.com/apk/res/android}'
PUBLIC_FILES = (
    'app-release.aab', 'mapping.txt', 'configuration.txt', 'manifest.xml',
    'provenance-final.json', 'signature-public.json', 'audit-existing.json', 'tools-public.json',
)


def read_json(path):
    return json.loads(path.read_text(encoding='utf-8-sig'))


def read_log(path):
    data = path.read_bytes()
    encoding = 'utf-16' if data.startswith((b'\xff\xfe', b'\xfe\xff')) else 'utf-8-sig'
    return data.decode(encoding)


def file_sha256(path):
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for chunk in iter(lambda: stream.read(65536), b''):
            digest.update(chunk)
    return digest.hexdigest()


def verify_tools(output):
    tools = read_json(output / 'tools-public.json')
    if set(tools) != {'java', 'python', 'powershell', 'bundletool'}:
        raise ValueError('Inventario de ferramentas inesperado')
    if any(not isinstance(value, str) or not re.fullmatch(r'[0-9][0-9A-Za-z.+_-]{0,63}', value)
           for value in tools.values()):
        raise ValueError('Versao de ferramenta invalida; nao exportar texto livre')
    return tools


def verify_certificate(output, expected_cert):
    expected = expected_cert.replace(':', '').lower()
    if not re.fullmatch(r'[a-f0-9]{64}', expected):
        raise ValueError('Fingerprint de referencia invalido')
    pem = read_log(output / 'upload-certificate.log')
    first = re.search(r'-----BEGIN CERTIFICATE-----\s*(.*?)\s*-----END CERTIFICATE-----', pem, re.S)
    if not first:
        raise ValueError('Certificado RFC ausente')
    actual = hashlib.sha256(base64.b64decode(first[1])).hexdigest()
    if actual != expected:
        raise ValueError('Certificado de upload diverge da referencia')
    return actual


def classify_signature(output, expected_cert):
    """Strict4 só é classificável por identidade/validade/integridade e motivo explícitos.

    JDK17 também usa4 para certificado expirado e algoritmo desativado: código sozinho
    nunca aprova. TSA, cadeia não autoassinada e outro motivo permanecem bloqueios.
    """
    strict = read_json(output / 'strict-result.json')
    raw_integrity = read_json(output / 'signature-integrity.log')
    fields = ('all_entries_signed', 'certificate_currently_valid', 'self_signed',
              'has_timestamp', 'entries', 'leaf_sha256')
    integrity = {key: raw_integrity[key] for key in fields}
    if (integrity['all_entries_signed'] is not True
            or integrity['certificate_currently_valid'] is not True
            or integrity['leaf_sha256'] != expected_cert.replace(':', '').lower()
            or type(integrity['entries']) is not int or integrity['entries'] <= 0):
        raise ValueError('Integridade/identidade/validade ausentes')
    reference = strict['certificate_reference']
    if reference not in {'previously_confirmed_upload_certificate', 'previously_accepted_aab'}:
        raise ValueError('Origem de certificado nao identificada')
    code = strict['exit_code']
    classification = 'strict_passed'
    if type(code) is not int:
        raise ValueError('Codigo strict invalido')
    if code != 0:
        if code != 4 or integrity['self_signed'] is not True or integrity['has_timestamp'] is not False:
            raise ValueError('Falha strict nao classificada')
        text = read_log(output / 'signature-strict.log') + read_log(output / 'signature-strict.stderr.log')
        errors = []
        collecting = False
        for line in text.splitlines():
            if line.strip() == 'Error:':
                collecting = True
                continue
            if collecting and line.strip() == 'Warning:':
                break
            if collecting and line.strip():
                errors.append(line.strip())
        self_signed = 'This jar contains entries whose signer certificate is self-signed.'
        invalid_chain = ('This jar contains entries whose certificate chain is invalid. '
                         'Reason: PKIX path building failed: ')
        if not errors or not all(line == self_signed or line.startswith(invalid_chain) for line in errors):
            raise ValueError('Motivo strict inesperado')
        classification = 'trusted_self_signed_valid_integrity_checked'
    result = dict(exit_code=code, classification=classification,
                  certificate_reference=reference, integrity=integrity)
    (output / 'signature-public.json').write_text(json.dumps(result, indent=2) + '\n', encoding='utf-8')
    return result


def verify_manifest(output):
    tree = ET.fromstring(read_log(output / 'manifest.log'))
    if (tree.get('package') != 'com.terra.wallpaper' or tree.get(ANDROID + 'versionCode') != '8'
            or tree.get(ANDROID + 'versionName') != '1.0.5'):
        raise ValueError('Identidade do candidato divergente')
    sdk = tree.find('uses-sdk')
    if sdk is None or sdk.get(ANDROID + 'minSdkVersion') != '26' or sdk.get(ANDROID + 'targetSdkVersion') != '36':
        raise ValueError('SDK divergente')
    app = tree.find('application')
    if (app is None or app.get(ANDROID + 'debuggable', 'false') != 'false'
            or app.get(ANDROID + 'usesCleartextTraffic', 'false') != 'false'):
        raise ValueError('Flags release invalidas')
    components = list(app.findall('activity')) + list(app.findall('activity-alias'))
    if any(item.get(ANDROID + 'name', '').endswith('.debug.DebugLauncher') for item in components):
        raise ValueError('Launcher debug presente')
    debug = [item for item in app.findall('activity') if item.get(ANDROID + 'name', '').endswith('.debug.DebugActivity')]
    if len(debug) != 1 or debug[0].get(ANDROID + 'exported') != 'false':
        raise ValueError('DebugActivity exportada/ausente')
    return tree


def verify_build_config(root):
    config = (root / 'android-app/app/build/generated/source/buildConfig/release/com/terra/wallpaper/BuildConfig.java').read_text()
    if ('boolean DEBUG = false' not in config or ENDPOINT not in config
            or not re.search(r'PLAY_LICENSE_PUBLIC_KEY = "[^"]+"', config)):
        raise ValueError('Endpoint/Billing/release incompletos')


def verify_assets_and_mapping(root, aab, mapping, candidate_catalog):
    assets = root / 'android-app/app/src/main/assets'
    source_files = [path for path in assets.rglob('*') if path.is_file()]
    with zipfile.ZipFile(aab) as bundle:
        names = bundle.namelist()
        if not any(name.startswith('META-INF/') and name.endswith(('.RSA', '.DSA', '.EC')) for name in names):
            raise ValueError('Assinatura JAR ausente')
        source_names = {path.relative_to(assets).as_posix() for path in source_files}
        actual = {name.removeprefix('base/assets/') for name in names
                  if name.startswith('base/assets/') and not name.endswith('/')}
        if len(names) != len(set(names)) or source_names != actual:
            raise ValueError('Conjunto de assets diverge da fonte (extra/ausente/duplicado)')
        for path in source_files:
            if bundle.read('base/assets/' + path.relative_to(assets).as_posix()) != path.read_bytes():
                raise ValueError('Asset diverge da fonte')
        if any(candidate_catalog.caminho_vedado(name) for name in actual):
            raise ValueError('Asset vedado no AAB')
        if bundle.read('BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map') != mapping.read_bytes():
            raise ValueError('Mapping embarcado divergente')
    return len(source_files)


def sanitized_r8(data):
    result = []
    for line in data.decode('utf-8').splitlines(keepends=True):
        if re.search(r'(?:[A-Za-z]:[\\/]|/(?:Users|home|private|var|tmp)/)', line):
            if not line.lstrip().startswith('#'):
                raise ValueError('Caminho privado em diretiva R8; requer tratamento')
            result.append('# origem local omitida; hash original no recibo\n')
        else:
            result.append(line)
    return ''.join(result).encode('utf-8')


def copy_public_artifacts(paths, output):
    records = {}
    for path in paths:
        target = output / path.name
        original_sha = file_sha256(path)
        if path.suffix == '.aab':
            shutil.copy2(path, target)
        else:
            target.write_bytes(sanitized_r8(path.read_bytes()))
        digest = file_sha256(target)
        records[path.name] = dict(bytes=target.stat().st_size, sha256=digest,
                                 original_sha256=original_sha, sanitized_comments_only=digest != original_sha)
    return records


def audit(root, aab, commit, output, expected_cert):
    if commit != APP_COMMIT or subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip() != commit:
        raise ValueError('SHA fonte diverge do app congelado')
    sys.path.insert(0, str(root / 'scripts'))
    import candidate_catalog
    inventory = candidate_catalog.conferir(root)
    cert_hash = verify_certificate(output, expected_cert)
    manifest = verify_manifest(output)
    verify_build_config(root)
    signature = classify_signature(output, expected_cert)
    tools = verify_tools(output)
    r8 = root / 'android-app/app/build/outputs/mapping/release'
    mapping, configuration = r8 / 'mapping.txt', r8 / 'configuration.txt'
    if any(not path.is_file() or not path.stat().st_size for path in (mapping, configuration)):
        raise ValueError('R8 mapping/configuracao ausente')
    assets_verified = verify_assets_and_mapping(root, aab, mapping, candidate_catalog)
    artifacts = copy_public_artifacts((aab, mapping, configuration), output)
    report = dict(
        signature=signature, tools=tools, commit=commit,
        versionName='1.0.5', versionCode=8, endpoint=ENDPOINT,
        upload_certificate_sha256=cert_hash, inventory=inventory, assets_verified=assets_verified,
        permissions=sorted(item.get(ANDROID + 'name') for item in manifest.findall('uses-permission')),
        legal_manifest_sha256=file_sha256(root / 'docs/legal/manifesto-documentos.json'),
        artifacts=artifacts,
        runner_files={path.name: file_sha256(path) for path in Path(__file__).parent.iterdir()
                      if path.name in {'run-final-candidate.ps1', 'PrepareSigning.java', 'VerifySignature.java', 'verify_final_candidate.py', 'rehearsal.py'}},
        limits=[
            'BuildConfig gerado e auxiliar: valor no dexAAB nao foi demonstrado por essa leitura; auditoria5.3 deve conferir o artefato',
            'Sem Console/upload/instalacao/jornadas Billing',
            'Presenca/formato Billing nao certificam correspondencia com app Play; conferir configuracao legitima no Windows',
        ],
    )
    (output / 'manifest.xml').write_bytes(ET.tostring(manifest, encoding='utf-8', xml_declaration=True))
    (output / 'provenance-final.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    with zipfile.ZipFile(output / 'candidate-receipt.zip', 'w', compression=zipfile.ZIP_DEFLATED) as package:
        for name in PUBLIC_FILES:
            package.write(output / name, name)
    return report


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    for name in ('root', 'aab', 'output'):
        parser.add_argument('--' + name, type=Path, required=True)
    parser.add_argument('--commit', required=True)
    parser.add_argument('--expected-upload-sha256', required=True)
    args = parser.parse_args()
    try:
        audit(args.root.resolve(), args.aab.resolve(), args.commit, args.output.resolve(), args.expected_upload_sha256)
    except Exception as error:
        # Caminhos/valores vindos de ferramentas não entram na saída compartilhável.
        print('Auditoria final interrompida: ' + type(error).__name__, file=sys.stderr)
        return 1
    print('FIM auditoria adicional: 0')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
