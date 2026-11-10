"""Disparo Pages somente para revisão aprovada; nunca registra a URL do hook.

Contrato #109, 09/11/2026. Docs: developers.cloudflare.com/pages/configuration/deploy-hooks/
O hook inicia build, não prova publicação. O site valida o SHA e mantém seu ponteiro aprovado.
"""
import json
import os
from pathlib import Path
import subprocess
import sys
import urllib.parse
import urllib.request

from sincronizar_documentos_legais import validar_estado


def check_exports(root):
    result = subprocess.run(
        [sys.executable, str(root / 'scripts/sincronizar_documentos_legais.py'), '--check'],
        cwd=root, capture_output=True, text=True, timeout=60)
    if result.returncode:
        raise ValueError('Exportações legais inválidas')


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def post_hook(url):
    # Um único POST, sem retry ou redirect que possa vazar o hook/disparar outro build.
    request = urllib.request.Request(url, data=b'', method='POST')
    with urllib.request.build_opener(NoRedirect).open(request, timeout=30) as response:
        return response.status


def trigger(root, hook, *, validate=check_exports, post=post_hook, emit=print):
    revision = json.loads((root / 'docs/legal/revisao.json').read_text(encoding='utf-8'))
    manifest = json.loads((root / 'docs/legal/manifesto-documentos.json').read_text(encoding='utf-8'))
    approved = revision.get('aprovado_para_publicacao')
    if type(approved) is not bool or manifest.get('aprovado_para_publicacao') is not approved:
        raise ValueError('Aprovação deve ser booleana e coerente')
    if {k: v for k, v in manifest.items() if k != 'documentos'} != revision:
        raise ValueError('Manifesto diverge dos metadados')
    validar_estado(revision)
    validate(root)  # Erro de fonte/hash/exportação não é skip, mesmo sem secret.
    if not approved:
        emit('::notice::SKIP: revisão não aprovada; preservar última versão aprovada no site.')
        return 'draft'
    if not hook:
        emit('::warning::SKIP: TERRA_SITE_DEPLOY_HOOK ausente; nenhum build solicitado.')
        return 'missing_hook'
    parsed = urllib.parse.urlsplit(hook)
    if (parsed.scheme != 'https' or parsed.hostname != 'api.cloudflare.com'
            or parsed.username or parsed.password or parsed.port not in (None, 443)
            or not parsed.path.startswith('/client/v4/pages/webhooks/deploy_hooks/')
            or parsed.query or parsed.fragment):
        raise ValueError('Formato do hook inválido')
    status = post(hook)
    if not 200 <= status < 300:
        raise ValueError('Falha HTTP no disparo')
    emit('Build Pages solicitado; publicação e SHA aprovado devem ser conferidos no site.')
    return 'requested'


def main():
    try:
        trigger(Path(__file__).resolve().parents[1], os.environ.get('TERRA_SITE_DEPLOY_HOOK', ''))
    except Exception:
        # Exceções HTTP/URL podem conter o secret: mensagem fixa, saída não zero.
        print('::error::Falha na validação legal ou no disparo Pages; nenhum detalhe sensível exposto.')
        return 1
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
