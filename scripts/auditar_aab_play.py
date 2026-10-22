"""Audita um AAB local com bundletool oficial, sem ler chaves ou alterar o pacote.

Exit 0: verificacoes locais passaram e maior codigo informado.
Exit 1: divergencias comprovadas. Exit 2: evidencia de versionCode incompleta.
Nao aprova Console, assinatura, ofertas ou comportamento no aparelho.
"""
import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

ROOT = Path(__file__).resolve().parents[1]
ANDROID = "{http://schemas.android.com/apk/res/android}"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("aab", type=Path)
    parser.add_argument("--bundletool", required=True, type=Path)
    parser.add_argument("--java", default="java")
    parser.add_argument("--maior-version-code", type=int)
    parser.add_argument("--saida", type=Path)
    args = parser.parse_args()
    if args.maior_version_code is not None and args.maior_version_code < 0:
        parser.error("maior-version-code deve ser nao negativo")
    aab = args.aab.resolve(strict=True)
    jar = args.bundletool.resolve(strict=True)
    result = subprocess.run(
        [args.java, "-Dfile.encoding=UTF-8", "-jar", str(jar), "dump", "manifest", "--bundle=" + str(aab)],
        capture_output=True, check=True,
    )
    manifest = ET.fromstring(result.stdout)
    gradle = (ROOT / "android-app/app/build.gradle").read_text(encoding="utf-8")
    expected_package = re.search(r'applicationId\s+"([^"]+)"', gradle)[1]
    expected_code = int(re.search(r'versionCode\s+(\d+)', gradle)[1])
    expected_name = re.search(r'versionName\s+"([^"]+)"', gradle)[1]
    sdk = manifest.find("uses-sdk")
    code = int(manifest.get(ANDROID + "versionCode"))
    with aab.open("rb") as source:
        digest = hashlib.file_digest(source, "sha256").hexdigest()
    report = {
        "arquivo": aab.name,
        "sha256": digest,
        "bytes": aab.stat().st_size,
        "package": manifest.get("package"),
        "versionCode": code,
        "versionName": manifest.get(ANDROID + "versionName"),
        "minSdk": sdk.get(ANDROID + "minSdkVersion") if sdk is not None else None,
        "targetSdk": sdk.get(ANDROID + "targetSdkVersion") if sdk is not None else None,
        "permissoes": [x.get(ANDROID + "name") for x in manifest.findall("uses-permission")],
        "features": [{k.split("}")[-1]: v for k, v in x.attrib.items()}
                     for x in manifest.findall("uses-feature")],
        "fonte": {"package": expected_package, "versionCode": expected_code,
                  "versionName": expected_name},
        "maior_version_code_informado": args.maior_version_code,
        "documentos": {}, "erros": [], "pendencias": [],
    }
    if report["package"] != expected_package:
        report["erros"].append("Identidade diverge do projeto")
    if code != expected_code or report["versionName"] != expected_name:
        report["erros"].append("Versao do AAB diverge da fonte atual")
    if args.maior_version_code is None:
        report["pendencias"].append("Conferir maior versionCode em todas as faixas do Console")
    elif code <= args.maior_version_code:
        report["erros"].append("Codigo nao supera o maior versionCode informado")
    with zipfile.ZipFile(aab) as bundle:
        names = set(bundle.namelist())
        report["abis"] = sorted({n.split("/")[2] for n in names
                                 if n.startswith("base/lib/") and n.endswith(".so")})
        report["modulos"] = sorted({n.split("/")[0] for n in names
                                    if n.endswith("/manifest/AndroidManifest.xml")})
        report["assinatura_presente"] = any(n.startswith("META-INF/") and
                                            n.endswith((".RSA", ".DSA", ".EC")) for n in names)
        for slug in ("privacidade", "termos", "contato"):
            name = f"base/assets/legal/{slug}/index.html"
            embedded = bundle.read(name) if name in names else None
            local = (ROOT / f"android-app/app/src/main/assets/legal/{slug}/index.html").read_bytes()
            matches = embedded == local
            report["documentos"][slug] = {"presente": embedded is not None,
                                          "identico_a_fonte": matches}
            if not matches:
                report["erros"].append("Documento ausente ou desatualizado: " + slug)
    if not report["assinatura_presente"]:
        report["erros"].append("AAB sem bloco de assinatura JAR identificado")
    report["pendencias"].extend([
        "Conferir certificado de upload e assinatura efetiva; presenca de bloco nao valida assinatura",
        "Conferir inclusao/processamento e compatibilidade na versao do Console",
        "Conferir configuracao de acervo/compras, build e jornadas de release",
    ])
    # Sem prova de procedencia: nome/data/versao iguais nao vinculam AAB a commit.
    report["aprovado_para_envio"] = False
    report["resultado_local"] = "divergencias" if report["erros"] else "checks_locais_sem_divergencias"
    data = json.dumps(report, ensure_ascii=False, indent=2) + "\n"
    if args.saida:
        args.saida.parent.mkdir(parents=True, exist_ok=True)
        args.saida.write_text(data, encoding="utf-8")
    print(data, end="")
    return 1 if report["erros"] else (2 if args.maior_version_code is None else 0)


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (OSError, ValueError, ET.ParseError, zipfile.BadZipFile, subprocess.CalledProcessError) as exc:
        print("Falha na auditoria local: " + type(exc).__name__, file=sys.stderr)
        sys.exit(1)
