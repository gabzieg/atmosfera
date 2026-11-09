"""Inventario local por arte; nao publica bucket nem configura produtos na Play."""
import argparse
import csv
import hashlib
import json
import re
import struct
from collections import Counter
from pathlib import Path

EXCLUIDOS = {"simpsons", "budokai", "konoha", "timessquare", "burj", "ogro", "eiffel"}
AMOSTRAS = {("cabana", "pixel"), ("bruxa", "clay"), ("lavanda", "aqua"),
            ("esfinge", "vangogh"), ("jardim", "ukiyoe")}
SPRITES_EXCLUIDOS = {"pixel_retro", "pixel_retro_2"}
EXTENSOES = {".png", ".webp", ".json"}


def declaracoes(arquivo):
    texto = arquivo.read_text(encoding="utf-8")
    texto = re.sub(r"/\*.*?\*/", "", texto, flags=re.S)
    texto = re.sub(r"//[^\n]*", "", texto)
    blocos = list(re.finditer(r'"([a-z0-9_]+)"\s+to\s+CenaCfg\(', texto))
    encontrados = {}
    avisos = []
    for i, bloco in enumerate(blocos):
        corpo = texto[bloco.end():blocos[i + 1].start() if i + 1 < len(blocos) else len(texto)]
        for arte, cena_pasta, arte_pasta in re.findall(
                r'"([a-z0-9_]+)"\s+to\s+VarFundo\("cenas/([^/]+)/([^/]+)/"', corpo):
            chave = (bloco.group(1), arte)
            if chave in encontrados:
                avisos.append("variante_duplicada_no_motor:" + "/".join(chave))
            if chave != (cena_pasta, arte_pasta):
                avisos.append("prefixo_diferente_da_chave:" + "/".join(chave))
            encontrados[chave] = (cena_pasta, arte_pasta)
    if not encontrados:
        raise ValueError("Nenhuma variante reconhecida; revisar o parser antes de gerar inventario.")
    return encontrados, sorted(set(avisos))


def gerar(raiz, saida, data):
    motor = raiz / "android-app/app/src/main/java/com/terra/wallpaper/engine/Cenario.kt"
    declaradas, avisos = declaracoes(motor)
    fontes = [("embarcada", raiz / "android-app/app/src/main/assets/atmosfera/cenas"),
              ("pasta_acervo", raiz / "content-packs/cenarios-futuros")]
    pastas_cena = {}
    fisicas = {}
    for origem, base in fontes:
        for cena in sorted(base.iterdir()):
            if not cena.is_dir():
                continue
            pastas_cena.setdefault(cena.name, []).append((origem, cena))
            for arte in sorted(cena.iterdir()):
                if arte.is_dir() and (arte / "fundo.png").is_file():
                    fisicas.setdefault((cena.name, arte.name), []).append((origem, arte))

    cache = {}

    def arquivo(path):
        if path not in cache:
            sha = hashlib.sha256()
            with path.open("rb") as stream:
                for chunk in iter(lambda: stream.read(1024 * 1024), b""):
                    sha.update(chunk)
            cache[path] = {"caminho": path.relative_to(raiz).as_posix(),
                           "bytes": path.stat().st_size, "sha256": sha.hexdigest()}
        return cache[path]

    def arquivos(pasta):
        return [arquivo(p) for p in sorted(pasta.iterdir())
                if p.is_file() and p.suffix.lower() in EXTENSOES] if pasta else []

    linhas, detalhes = [], []
    for cena, arte in sorted(set(declaradas) | set(fisicas)):
        chave = (cena, arte)
        locais = fisicas.get(chave, [])
        origem, pasta = locais[0] if locais else ("sem_arquivo_local", None)
        comum = pasta.parent if pasta else (
            pastas_cena[cena][0][1] if cena in pastas_cena else None)
        arquivos_arte, arquivos_comuns = arquivos(pasta), arquivos(comum)
        falta = []
        if not pasta:
            falta.append("fundo_local_ausente")
        else:
            if not (pasta / "frente.png").is_file():
                falta.append("frente_local_ausente")
            if not ((pasta / "zonas.png").is_file() or (comum / "zonas.png").is_file()):
                falta.append("zonas_local_ausente")
        if chave not in declaradas:
            falta.append("variante_nao_declarada_no_motor")
        if len(locais) > 1:
            falta.append("fontes_duplicadas_reconciliar")
        if "prefixo_diferente_da_chave:" + cena + "/" + arte in avisos:
            falta.append("prefixo_do_motor_reconciliar")
        largura = altura = ""
        if pasta:
            with (pasta / "fundo.png").open("rb") as stream:
                header = stream.read(24)
            if len(header) == 24 and header[:8] == b"\x89PNG\r\n\x1a\n":
                largura, altura = struct.unpack(">II", header[16:24])
            else:
                falta.append("cabecalho_png_invalido")
        excluida = cena in EXCLUIDOS
        gratis = chave in AMOSTRAS and not excluida
        proposta = "terra_arte_" + cena + "_" + arte if pasta and not excluida and not gratis else ""
        thumb = next((pasta / nome for nome in ("thumb.webp", "thumb.png")
                      if pasta and (pasta / nome).is_file()), None)
        fundo = next((p for p in arquivos_arte if p["caminho"].endswith("/fundo.png")), None)
        linha = {
            "arte_id": cena + "/" + arte, "cenario_id": cena, "variante_id": arte,
            "tipo": "excluida" if excluida else "amostra_gratuita" if gratis else "arte_paga_proposta",
            "status_local": "excluida" if excluida else "arquivos_pendentes" if falta else "arquivos_presentes_nao_testada",
            "origem": origem, "declarada_motor": chave in declaradas,
            "fundo_local": bool(pasta), "fundo_caminho": fundo["caminho"] if fundo else "",
            "fundo_sha256": fundo["sha256"] if fundo else "", "largura": largura, "altura": altura,
            "arquivos_arte_bytes": sum(p["bytes"] for p in arquivos_arte),
            "arquivos_comuns_bytes": sum(p["bytes"] for p in arquivos_comuns),
            "miniatura_caminho": thumb.relative_to(raiz).as_posix() if thumb else "",
            "miniatura_status": "arquivo_presente_nao_validado" if thumb else "pendente_geracao",
            "product_id_proposto": proposta, "status_console": "nao_verificado",
            "preco_regular_brl": "" if excluida or gratis else "1.99",
            "preco_promocional_brl": "", "promocao_status": "selecao_pendente" if proposta else "nao_aplicavel",
            "teste_android": "excluida" if excluida else "nao_testada",
            "licenca_origem": "conferencia_pendente", "pendencias_arquivos": ";".join(falta),
        }
        linhas.append(linha)
        detalhes.append({**linha, "arquivos_arte": arquivos_arte, "arquivos_comuns": arquivos_comuns,
                         "fontes_fisicas": [p.relative_to(raiz).as_posix() for _, p in locais]})

    candidatas = [r for r in linhas if r["tipo"] != "excluida"]
    presentes = [r for r in candidatas if r["fundo_local"]]
    sprites_base = raiz / "android-app/app/src/main/assets/atmosfera"
    resumo = {
        "data_inventario": data, "escopo": "arquivos_locais_e_declaracoes_motor_sem_consulta_bucket_console",
        "motor_sha256": arquivo(motor)["sha256"], "avisos_motor": avisos,
        "exclusoes_cenas": sorted(EXCLUIDOS), "exclusoes_sprites": sorted(SPRITES_EXCLUIDOS),
        "sprites_excluidos_presentes": [p.relative_to(raiz).as_posix() for p in sorted(sprites_base.iterdir())
                                       if p.is_file() and any(x in p.stem for x in SPRITES_EXCLUIDOS)],
        "linhas_total": len(linhas), "linhas_apos_exclusoes": len(candidatas),
        "cenas_com_fundo_apos_exclusoes": len({r["cenario_id"] for r in presentes}),
        "artes_com_fundo_apos_exclusoes": len(presentes),
        "artes_sem_fundo_apos_exclusoes": sum(not r["fundo_local"] for r in candidatas),
        "amostras_com_fundo": sum(r["tipo"] == "amostra_gratuita" for r in presentes),
        "produtos_propostos_com_fundo": sum(bool(r["product_id_proposto"]) for r in presentes),
        "status_apos_exclusoes": dict(sorted(Counter(r["status_local"] for r in candidatas).items())),
    }
    ids = [r["product_id_proposto"] for r in linhas if r["product_id_proposto"]]
    if len(ids) != len(set(ids)) or any(r["product_id_proposto"] for r in linhas if r["tipo"] == "excluida"):
        raise ValueError("Produtos duplicados ou produto proposto para item excluido.")
    if resumo["amostras_com_fundo"] != len(AMOSTRAS):
        raise ValueError("Uma das cinco amostras gratuitas esta ausente.")
    saida.mkdir(parents=True, exist_ok=True)
    with (saida / "ARTES.csv").open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=list(linhas[0]))
        writer.writeheader()
        writer.writerows(linhas)
    (saida / "ARTES.json").write_text(json.dumps({"resumo": resumo, "artes": detalhes},
                                              ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    (saida / "RESUMO.json").write_text(json.dumps(resumo, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return resumo


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--data", required=True, help="Data de referencia YYYY-MM-DD")
    parser.add_argument("--saida", required=True, help="Diretorio relativo ao repositorio")
    args = parser.parse_args()
    raiz = Path(__file__).resolve().parents[1]
    saida = (raiz / args.saida).resolve()
    saida.relative_to(raiz)  # Nao escrever fora do repositorio.
    print(json.dumps(gerar(raiz, saida, args.data), ensure_ascii=False, indent=2))
