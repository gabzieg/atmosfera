"""Verificacao opt-in de um lote HD: tamanho, hash completo e estrutura ZIP; sem upload."""
import hashlib
import io
import json
import urllib.request
import zipfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
manifest = json.loads((root / "android-app/app/src/main/assets/atmosfera/catalogo/manifest.json").read_text(encoding="utf-8"))
base = "https://pub-722473b758b144279fc34ecb931a6045.r2.dev/"
saida = root / "android-app/app/build/acervo-smoke"
resultados = []
for cena, arte in [("cabana", "clay"), ("aldeiajapao", "aqua"), ("porto", "pixel")]:
    infos = [("_cena", manifest["cenas"][cena]["cena"]),
             (arte, manifest["cenas"][cena]["artes"][arte]["pack"])]
    for variante, info in infos:
        alvo = saida / info["arquivo"]
        alvo.parent.mkdir(parents=True, exist_ok=True)
        data = alvo.read_bytes() if alvo.exists() else None
        if data is None or hashlib.sha256(data).hexdigest() != info["sha256"]:
            req = urllib.request.Request(base + info["arquivo"], headers={"User-Agent": "Terra/1.0 acervo"})
            with urllib.request.urlopen(req, timeout=30) as response:
                data = response.read(min(info["bytes"], 128 * 1024 * 1024) + 1)
        assert len(data) == info["bytes"]
        assert hashlib.sha256(data).hexdigest() == info["sha256"]
        with zipfile.ZipFile(io.BytesIO(data)) as z:
            assert z.testzip() is None
            nomes = z.namelist()
            assert nomes and all(".." not in n and "\\" not in n and ":" not in n and not n.startswith("/") for n in nomes)
            assert sum(x.file_size for x in z.infolist()) <= 256 * 1024 * 1024
            obrigatorios = ["zonas.webp"] if variante == "_cena" else ["fundo.webp", "frente.webp"]
            assert all(n in nomes for n in obrigatorios), nomes
        alvo.write_bytes(data)
        resultados.append({"cena": cena, "arte": variante, "arquivo": info["arquivo"],
                           "bytes": len(data), "sha256": info["sha256"], "conteudo": nomes})
        print("Pacote conferido:", cena, variante, len(data), flush=True)
(saida / "RESULTADOS.json").write_text(json.dumps(resultados, indent=2) + "\n", encoding="utf-8")
print("Lote validado. Nao certifica renderizacao nem compra pela Play.", flush=True)
