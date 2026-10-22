"""Importa o manifesto conferido e miniaturas do R2. Nunca baixa HD ou faz upload."""
import argparse
import concurrent.futures
import hashlib
import json
import re
import urllib.parse
import urllib.request
from pathlib import Path

EXCLUIDOS = {"simpsons", "budokai", "konoha", "timessquare", "burj", "ogro", "eiffel"}
RAIZ = Path(__file__).resolve().parents[1]
NATIVAS = {("cabana", "pixel"), ("bruxa", "pixel"), ("bruxa", "clay"),
           ("lavanda", "pixel"), ("lavanda", "aqua"), ("esfinge", "pixel"),
           ("esfinge", "vangogh"), ("jardim", "pixel"), ("jardim", "ukiyoe")}
GRATIS = {("cabana", "pixel"), ("bruxa", "clay"), ("lavanda", "aqua"),
          ("esfinge", "vangogh"), ("jardim", "ukiyoe")}


def normalizar(descritor):
    caminho = descritor["arquivo"]
    m = re.fullmatch(r"(?:pack/[a-z0-9_]+/[a-z0-9_]+|(?:thumb|preview)/[a-z0-9_]+)\.([a-f0-9]{64})\.(?:zip|webp)", caminho)
    if not m or not m.group(1).startswith(descritor["sha256"].lower()):
        raise ValueError("Nome/hash incoerente: " + caminho)
    if not 0 < descritor["bytes"] <= 128 * 1024 * 1024:
        raise ValueError("Tamanho invalido: " + caminho)
    return dict(descritor, sha256=m.group(1))


def importar(manifesto, base, baixar=True):
    parsed = urllib.parse.urlsplit(base)
    if parsed.scheme != "https" or not parsed.netloc or parsed.username is not None or parsed.password is not None or parsed.query or parsed.fragment:
        raise ValueError("Informe URL HTTPS publica sem credenciais ou parametros")
    dados = json.loads(manifesto.read_text(encoding="utf-8"))
    cenas, artes, thumbs = {}, [], []
    for cena, conteudo in sorted(dados["cenas"].items()):
        if cena in EXCLUIDOS:
            continue
        if not re.fullmatch(r"[a-z0-9_]+", cena):
            raise ValueError("Cena invalida")
        normal = {"cena": normalizar(conteudo["cena"]), "artes": {}}
        for arte, item in sorted(conteudo["artes"].items()):
            if not re.fullmatch(r"[a-z0-9_]+", arte):
                raise ValueError("Arte invalida")
            normal["artes"][arte] = {k: normalizar(v) for k, v in item.items()}
            thumb = normal["artes"][arte]["thumb"]
            caminho_thumb = f"atmosfera/miniaturas/{cena}__{arte}.webp"
            artes.append({"cena": cena, "arte": arte, "embarcada": (cena, arte) in NATIVAS,
                          "produto": "" if (cena, arte) in GRATIS else f"terra_arte_{cena}_{arte}",
                          "miniatura": caminho_thumb})
            thumbs.append((thumb, caminho_thumb))
        cenas[cena] = normal
    pares = {(a["cena"], a["arte"]) for a in artes}
    if not NATIVAS <= pares:
        raise ValueError("Manifesto omite artes nativas")
    assets = RAIZ / "android-app/app/src/main/assets"

    def obter(par):
        info, caminho = par
        alvo = assets / caminho
        if alvo.is_file() and hashlib.sha256(alvo.read_bytes()).hexdigest() == info["sha256"]:
            return
        with urllib.request.urlopen(urllib.request.Request(base.rstrip("/") + "/" + info["arquivo"], headers={"User-Agent": "Terra/1.0 acervo"}), timeout=30) as response:
            conteudo = response.read(2 * 1024 * 1024 + 1)
        if len(conteudo) != info["bytes"] or hashlib.sha256(conteudo).hexdigest() != info["sha256"]:
            raise ValueError("Miniatura corrompida: " + info["arquivo"])
        alvo.parent.mkdir(parents=True, exist_ok=True)
        tmp = alvo.with_suffix(".tmp")
        tmp.write_bytes(conteudo)
        tmp.replace(alvo)

    if baixar:
        with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:
            list(pool.map(obter, thumbs))
    pasta = assets / "atmosfera/catalogo"
    pasta.mkdir(parents=True, exist_ok=True)
    for nome, valor in [("manifest.json", {"versao": 2, "formato": "webp", "cenas": cenas}), ("artes.json", artes)]:
        (pasta / nome).write_text(json.dumps(valor, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return len(cenas), len(artes)


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--manifesto", default="docs/loja/conciliacao-2026-10-11/MANIFESTO-R2.json")
    p.add_argument("--base", default="https://pub-722473b758b144279fc34ecb931a6045.r2.dev/")
    p.add_argument("--sem-download", action="store_true")
    a = p.parse_args()
    print("Catalogo importado:", importar(RAIZ / a.manifesto, a.base, not a.sem_download), flush=True)
