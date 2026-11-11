"""Recibo de APK de ensaio e conferência local do aceite; sem instalar ou acessar rede."""
import argparse
import json
from pathlib import Path
import re
import subprocess
import tempfile
import zipfile

from verify_final_candidate import APP_COMMIT, file_sha256, read_json, read_log


def verify_environment(output):
    # #86, 11/11/2026: play-tests exige resolução real de symlink no Windows.
    # Testa o mesmo volume/processo antes de copiar configurações ou rodar gates.
    try:
        with tempfile.TemporaryDirectory(prefix='symlink-check-', dir=output) as folder:
            root = Path(folder)
            target = root / 'target.txt'
            target.write_bytes(b'fixture')
            link = root / 'link.txt'
            link.symlink_to(target)
            if not link.is_symlink() or link.resolve() != target.resolve() or link.read_bytes() != b'fixture':
                raise ValueError('Symlink nao resolveu o alvo')
    except OSError as error:
        raise ValueError('Processo sem suporte a symlink no volume de trabalho') from error


def verify_device(output):
    lines = read_log(output / 'devices.log').splitlines()
    if not lines or lines[0].strip() != 'List of devices attached':
        raise ValueError('Listagem de aparelho invalida')
    devices = [line.split() for line in lines[1:] if line.strip()]
    if len(devices) != 1 or len(devices[0]) != 2 or devices[0][1] != 'device':
        raise ValueError('Exige um unico aparelho autorizado e online')


def verify_acceptance(path, apk):
    receipt = read_json(path)
    if (receipt.get('app_commit') != APP_COMMIT or receipt.get('accepted_by_lead') is not True
            or not re.fullmatch(r'[a-f0-9]{64}', receipt.get('apk_sha256', ''))
            or not re.fullmatch(r'https://github\.com/gabzieg/atmosfera/issues/86#issuecomment-[0-9]+',
                                receipt.get('lead_acceptance_url', ''))):
        raise ValueError('Aceite de ensaio ausente/invalido para SHA fixo')
    if not apk or not apk.is_file() or file_sha256(apk) != receipt['apk_sha256']:
        raise ValueError('APK diverge do hash aceito no ensaio')
    # Formato/link não autenticam conteúdo: a lead confere o original antes de liberar.
    return receipt


def export_rehearsal(root, apk, output):
    if subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip() != APP_COMMIT:
        raise ValueError('SHA do ensaio divergente')
    verify_device(output)
    source = root / 'android-app/app/src/main/assets'
    with zipfile.ZipFile(apk) as package:
        names = package.namelist()
        expected = {p.relative_to(source).as_posix() for p in source.rglob('*') if p.is_file()}
        actual = {n[len('assets/'):] for n in names if n.startswith('assets/') and not n.endswith('/')}
        if len(names) != len(set(names)) or actual != expected:
            raise ValueError('Assets do APK de ensaio divergentes')
        for name in expected:
            if package.read('assets/' + name) != (source / name).read_bytes():
                raise ValueError('Bytes de asset do ensaio divergentes')
    receipt = dict(app_commit=APP_COMMIT, apk_sha256=file_sha256(apk), phase='rehearsal',
                   accepted_by_lead=False, lead_acceptance_url=None,
                   device_count=1, device_state='device',
                   limits=['APKdebug auxiliar; sem prova de R8/Billing/release/assinatura upload',
                           'Nao instalado pelo runner; capturas/execucao/aceite ainda pendentes',
                           'Sem serial/conta/log bruto neste recibo'])
    target = output / 'rehearsal-receipt.json'
    target.write_text(json.dumps(receipt, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    with zipfile.ZipFile(output / 'rehearsal-package.zip', 'w', zipfile.ZIP_DEFLATED) as package:
        package.write(apk, 'app-debug.apk')
        package.write(target, target.name)
    return receipt


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--acceptance', type=Path)
    parser.add_argument('--check-device', action='store_true')
    parser.add_argument('--check-environment', action='store_true')
    parser.add_argument('--root', type=Path)
    parser.add_argument('--apk', type=Path)
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    try:
        if args.check_environment:
            verify_environment(args.output)
        elif args.acceptance:
            verify_acceptance(args.acceptance, args.apk)
        elif args.check_device:
            verify_device(args.output)
        else:
            export_rehearsal(args.root, args.apk, args.output)
    except Exception:
        if args.check_environment:
            print('FALHA pre-requisito: symlink indisponivel; devolver falha sem pular gate ou elevar privilegios automaticamente')
            return 1
        print('FALHA ensaio: aparelho, fonte, pacote ou aceite divergente; nao enviar log privado')
        return 1
    if args.check_environment:
        print('FIM pre-requisito symlink: 0')
    else:
        print('FIM preparo ensaio: 0; nao equivale a execucao/aceite no aparelho')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
