"""Prepara o contrato do Acervo Android sem rede, credenciais ou publicação."""
import copy
import hashlib
import re
from pathlib import PurePosixPath


def prepare_catalog(manifest, read_bytes):
    """Retorna cópia normalizada e mapa chave → origem; leitura é injetada.

    Incidente #103 (09/11/2026): hash abreviado e hash duplicado no nome
    geravam manifesto incompatível com as verificações de Acervo.kt.
    """
    result = copy.deepcopy(manifest)
    excluded = {'simpsons', 'budokai', 'konoha', 'burj'}
    result['cenas'] = {k: v for k, v in result['cenas'].items() if k not in excluded}
    files = {}

    def collect(value):
        if isinstance(value, dict):
            if 'arquivo' in value:
                name = value['arquivo']
                match = re.fullmatch(
                    r'(pack/[a-z0-9_]+/[a-z0-9_]+|(?:preview|thumb)/[a-z0-9_]+)'
                    r'(?:\.([a-f0-9]{64}))?\.(zip|webp)', name)
                if not match or (name.startswith('pack/') != (match[3] == 'zip')):
                    raise ValueError('Caminho incompatível com Acervo: ' + name)
                size = value['bytes']
                limit = (128 if name.startswith('pack/') else 4) * 1024 * 1024
                if type(size) is not int or not 1 <= size <= limit:
                    raise ValueError('Tamanho inválido: ' + name)
                data = read_bytes(name)
                digest = hashlib.sha256(data).hexdigest()
                declared = value['sha256']
                # Preserva entrada legada abreviada, mas nunca a exporta ao Android.
                if (not isinstance(declared, str) or not re.fullmatch(r'[a-fA-F0-9]{1,64}', declared)
                        or len(data) != size or not digest.startswith(declared.lower())):
                    raise ValueError('Integridade inválida: ' + name)
                if match[2] and match[2] != digest:
                    raise ValueError('Nome e hash divergem: ' + name)
                key = f'{match[1]}.{digest}.{match[3]}'
                value['arquivo'] = key
                value['sha256'] = digest
                files[key] = PurePosixPath(name).as_posix()
            for child in value.values():
                collect(child)
        elif isinstance(value, list):
            for child in value:
                collect(child)

    collect(result)
    return result, files
