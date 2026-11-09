"""Prepara pacotes PNG e indice versionado; nao faz upload nem aprova artes."""
import argparse
import hashlib
import json
import zipfile
from pathlib import Path


def preparar(raiz, inventario, saida, usar_no_app=False):
    dados = json.loads(inventario.read_text(encoding="utf-8"))
    cenas, catalogo = {}, []
    def pacote(arquivos, relativo):
        destino = saida / relativo
        destino.parent.mkdir(parents=True, exist_ok=True)
        with zipfile.ZipFile(destino, "w", compression=zipfile.ZIP_DEFLATED) as z:
            for item in sorted(arquivos, key=lambda x: x["caminho"]):
                origem = (raiz / item["caminho"]).resolve()
                origem.relative_to(raiz)
                conteudo = origem.read_bytes()
                if hashlib.sha256(conteudo).hexdigest() != item["sha256"]:
                    raise ValueError("Origem mudou; regenere o inventario: " + item["caminho"])
                info = zipfile.ZipInfo(origem.name, date_time=(1980, 1, 1, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                z.writestr(info, conteudo)
        return {"arquivo": relativo, "bytes": destino.stat().st_size,
                "sha256": hashlib.sha256(destino.read_bytes()).hexdigest()}
    for arte in dados["artes"]:
        if arte["tipo"] == "excluida" or arte["status_local"] != "arquivos_presentes_nao_testada":
            continue
        cena, variante = arte["cenario_id"], arte["variante_id"]
        if cena not in cenas:
            cenas[cena] = {"cena": pacote(arte["arquivos_comuns"], f"pack/{cena}/_cena.zip"), "artes": {}}
        cenas[cena]["artes"][variante] = {"pack": pacote(arte["arquivos_arte"], f"pack/{cena}/{variante}.zip")}
        catalogo.append({"cena": cena, "arte": variante,
                         "produto": arte["product_id_proposto"],
                         "embarcada": arte["origem"] == "embarcada",
                         "miniatura": f"atmosfera/miniaturas/{cena}__{variante}.jpg"})
    manifesto = {"versao": 2, "formato": "png", "cenas": cenas}
    # PNG preparation is now an alternative; do not replace the reviewed R2 catalog by default.
    assets = (raiz / "android-app/app/src/main/assets/atmosfera/catalogo") if usar_no_app else saida / "assets-catalogo"
    assets.mkdir(parents=True, exist_ok=True)
    for destino in (saida / "manifest.json", assets / "manifest.json"):
        destino.write_text(json.dumps(manifesto, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    (assets / "artes.json").write_text(json.dumps(catalogo, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return len(cenas), len(catalogo)


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--inventario", default="docs/loja/catalogo-2026-10-10/ARTES.json")
    p.add_argument("--saida", default="dist/acervo-preparado")
    p.add_argument("--usar-no-app", action="store_true", help="Substitui explicitamente o catalogo R2 pelo catalogo PNG local")
    args = p.parse_args()
    raiz = Path(__file__).resolve().parents[1]
    saida = (raiz / args.saida).resolve(); saida.relative_to(raiz)
    print("Pacotes locais preparados (nao publicados):", preparar(raiz, raiz / args.inventario, saida, args.usar_no_app))
