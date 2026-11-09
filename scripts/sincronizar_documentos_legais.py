"""Gera explicitamente as tres copias HTML e o manifesto; --check nao escreve."""
import argparse
import datetime
import hashlib
import html
import json
import re
from pathlib import Path
from urllib.parse import urlsplit

ROOT = Path(__file__).resolve().parents[1]
DOCS = {"privacidade": "PRIVACIDADE", "termos": "TERMOS", "contato": "CONTATO"}
LINKS = {name + ".md": "../" + slug + "/" for slug, name in DOCS.items()}


def inline(text):
    text = html.escape(text, quote=False)
    def link(m):
        url = LINKS.get(m[2], m[2])
        if not url.startswith(("https://", "mailto:", "../")):
            raise ValueError("Link nao permitido: " + url)
        return '<a href="' + html.escape(html.unescape(url), quote=True) + '">' + m[1] + '</a>'
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", link, text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"<strong>\1</strong>", text)
    text = re.sub(r"`([^`]+)`", r"<code>\1</code>", text)
    return text


def render(text):
    """Subconjunto editorial deliberado: paragrafos, titulos, listas e tabelas."""
    lines = text.splitlines()
    result, i = [], 0
    while i < len(lines):
        line = lines[i].strip()
        if not line:
            i += 1; continue
        if line.startswith("## "):
            result.append("<h2>" + inline(line[3:]) + "</h2>"); i += 1
        elif line.startswith("- "):
            items = []
            while i < len(lines) and lines[i].startswith("- "):
                items.append("<li>" + inline(lines[i][2:]) + "</li>"); i += 1
            result.append("<ul>" + "".join(items) + "</ul>")
        elif line.startswith("|"):
            rows = []
            while i < len(lines) and lines[i].startswith("|"):
                cells = [x.strip() for x in lines[i].strip().strip("|").split("|")]
                if not all(re.fullmatch(r":?-+:?", x) for x in cells): rows.append(cells)
                i += 1
            if any(len(row) != len(rows[0]) for row in rows): raise ValueError("Tabela irregular")
            head = "<thead><tr>" + "".join("<th>" + inline(c) + "</th>" for c in rows[0]) + "</tr></thead>"
            body = "<tbody>" + "".join("<tr>" + "".join("<td>" + inline(c) + "</td>" for c in row) + "</tr>" for row in rows[1:]) + "</tbody>"
            result.append("<table>" + head + body + "</table>")
        else:
            para = []
            while i < len(lines) and lines[i].strip() and not lines[i].startswith(("## ", "- ", "|")):
                para.append(lines[i].strip()); i += 1
            result.append("<p>" + inline(" ".join(para)) + "</p>")
    return "\n".join(result)


def validar_estado(manifest):
    """#89, 09/11/2026: aceite editorial não inventa vigência/candidato."""
    approved = manifest.get('aprovado_para_publicacao')
    if type(approved) is not bool:
        raise ValueError('Aprovacao deve ser booleana')
    if not approved:
        if manifest.get('status') != 'rascunho_para_revisao':
            raise ValueError('Estado de rascunho incoerente')
        return 'rascunho'
    for key in ('aprovacao_registro', 'dominio_publico'):
        value = manifest.get(key)
        if not isinstance(value, str) or not value.strip():
            raise ValueError('Aprovacao sem identificacao: ' + key)
        url = urlsplit(value)
        if url.scheme != 'https' or not url.netloc or url.username or url.password:
            raise ValueError('Referencia publica HTTPS invalida: ' + key)
    if manifest.get('status') == 'aprovado_para_publicacao':
        if manifest.get('vigencia') is not None or manifest.get('versao_app') is not None:
            raise ValueError('Estado editorial nao fixa data ou candidato')
        return 'editorial'
    if manifest.get('status') != 'aprovado':
        raise ValueError('Estado de aprovacao incoerente')
    for key in ('vigencia', 'versao_app'):
        if not isinstance(manifest.get(key), str) or not manifest[key].strip():
            raise ValueError('Aprovacao efetiva sem identificacao: ' + key)
    datetime.date.fromisoformat(manifest['vigencia'])
    return 'vigente'


def gerar(check=False):
    template = (ROOT / "docs/legal/modelo.html").read_text(encoding="utf-8")
    manifest = json.loads((ROOT / "docs/legal/revisao.json").read_text(encoding="utf-8"))
    fase = validar_estado(manifest)
    manifest["documentos"] = {}
    expected = {}
    for slug, name in DOCS.items():
        src = ROOT / "docs/legal" / (name + ".md")
        md = src.read_text(encoding="utf-8")
        header, rest = md.split("\n## Resumo\n", 1)
        version = re.search(r"\*\*Vers\u00e3o[^*]*:\*\* ([0-9.]+)", header).group(1)
        if fase == 'vigente' and ("em revis" in header or "A definir" in header):
            raise ValueError("Documento aprovado ainda tem metadados de rascunho: " + slug)
        if fase == 'editorial':
            # Somente apresentacao Estado/Vigencia: fonte e corpo aceitos intocados.
            header = re.sub(r'^\*\*Estado:\*\*.*$',
                            '**Estado:** Redação aprovada para publicação; publicação não comprovada.',
                            header, flags=re.MULTILINE)
            header = re.sub(r'^\*\*Vigência:\*\*.*$',
                            '**Vigência:** Pendente do evento real de publicação das três páginas.',
                            header, flags=re.MULTILINE)
        summary, content = rest.split("\n## 1.", 1)
        title = header.splitlines()[0].removeprefix("# ")
        meta = "".join("<li>" + inline(line) + "</li>" for line in header.splitlines()[1:] if line.strip())
        estado = ('Vigência: ' + manifest['vigencia'] if fase == 'vigente' else
                  'Redação aprovada para publicação; vigência pendente do evento real' if fase == 'editorial'
                  else 'Documento em revisão')
        footer = "<footer><p>Terra — " + html.escape(title) + ", revisão " + version + ". " + html.escape(estado) + ".</p>" + \
                 '<p><a href="../privacidade/">Privacidade</a> · <a href="../termos/">Termos</a> · <a href="../contato/">Contato</a></p></footer>'
        body = "<header><h1>" + html.escape(title) + '</h1><ul class="meta">' + meta + "</ul></header>\n" + \
               '<section class="summary"><h2>Resumo</h2>' + render(summary) + "</section>\n" + render("## 1." + content) + "\n" + footer
        output = template.replace("{{title}}", html.escape(title)).replace("{{description}}", html.escape(title + " — revisão " + version + ", contato e informações do Terra.", quote=True)).replace("{{body}}", body)
        data = output.encode("utf-8")
        paths = [f"docs/{slug}/index.html", f"public_html/{slug}/index.html", f"android-app/app/src/main/assets/legal/{slug}/index.html"]
        for path in paths: expected[path] = data
        manifest["documentos"][slug] = {"versao": version, "fonte": src.relative_to(ROOT).as_posix(),
            "sha256_fonte": hashlib.sha256(src.read_bytes()).hexdigest(), "sha256_html": hashlib.sha256(data).hexdigest(), "copias": paths}
    expected["docs/legal/manifesto-documentos.json"] = (json.dumps(manifest, ensure_ascii=False, indent=2) + "\n").encode("utf-8")
    for path, data in expected.items():
        target = ROOT / path
        if check:
            if not target.is_file() or target.read_bytes() != data: raise ValueError("Documento fora de sincronia: " + path)
        else:
            target.parent.mkdir(parents=True, exist_ok=True); target.write_bytes(data)
    print("Documentos e manifesto conferidos." if check else "Copias geradas; estado: " + manifest["status"] + ". Nenhuma publicacao realizada.")


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--check", action="store_true")
    gerar(p.parse_args().check)
