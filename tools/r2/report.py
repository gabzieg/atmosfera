"""Histórico de evidência pública; não faz requisições nem atesta acesso atual."""
from copy import deepcopy


def attach_public_history(report, previous):
    """Nunca promove dados do relatório anterior a prova da execução atual.

    Incidente #103 (09/11/2026): public_access antigo sobrescrevia o estado
    não verificado atual. Contexto ausente permanece ausente, sem inferência.
    """
    result = deepcopy(report)
    fields = ('bucket', 'storage_class', 'scenes', 'art_variants', 'catalog_files',
              'catalog_bytes', 'manifest_sha256', 'public_access', 'public_base_url',
              'public_manifest_url', 'public_verification', 'previous_public_evidence')
    history = {field: deepcopy(previous[field]) for field in fields if field in previous}
    if history:
        result['previous_public_evidence'] = history
    return result
