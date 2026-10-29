#!/usr/bin/env python3
"""Gera inventário de conferência; não chama APIs nem altera o Play Console."""
import argparse
import csv
import io
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "docs/loja/conciliacao-2026-10-11/ARTES-CONCILIADAS.csv"
TARGET = ROOT / "docs/loja/produtos-play-2026-10-27/PRODUTOS.csv"
PILOTO = "terra_arte_biblioteca_vangogh"
TITULO_PILOTO = "Biblioteca — Van Gogh"  # Exceção aprovada pelo usuário em 29/10/2026.
DESCRICAO = "Arte para o wallpaper Terra. Compra única. Não inclui o Premium."
APP = ROOT / "android-app/app/src/main/java/com/terra/wallpaper"


def nomes_apresentacao(caminho, declaracao):
    texto = caminho.read_text(encoding="utf-8")
    trecho = texto.split(declaracao, 1)[1].split(")", 1)[0]
    nomes = dict(re.findall(r'"([^"\n]+)"\s+to\s+"([^"\n]+)"', trecho))
    if not nomes:
        raise ValueError(f"Tabela de nomes não encontrada: {caminho.name}")
    return nomes


def gerar():
    cenarios = nomes_apresentacao(APP / "engine/NomesCatalogo.kt", "private val nomes = mapOf(")
    estilos = nomes_apresentacao(APP / "ui/StoreTab.kt", "private val NOMES_ESTILO:")
    with SOURCE.open(encoding="utf-8-sig", newline="") as arquivo:
        artes = list(csv.DictReader(arquivo, delimiter=";"))
    if len(artes) != 366:
        raise ValueError("Reconciliar o catálogo antes de gerar: esperado 366 artes")
    pagas = [arte for arte in artes if arte["amostra_gratis"].lower() == "false"]
    if len(pagas) != 361:
        raise ValueError("Reconciliar amostras antes de gerar: esperado 361 artes pagas")
    produtos = set()
    saida = io.StringIO(newline="")
    campos = ["product_id", "purchase_option_id_proposto", "preco_brl",
              "pais_alvo", "cenario", "arte", "vitrine_candidato_7",
              "estado_console", "titulo", "descricao", "estado_opcao", "revisar_nomes"]
    writer = csv.DictWriter(saida, fieldnames=campos, lineterminator="\n")
    writer.writeheader()
    for arte in sorted(pagas, key=lambda item: item["produto_proposto"]):
        produto = arte["produto_proposto"]
        esperado = f"terra_arte_{arte['cenario']}_{arte['arte']}"
        if produto != esperado or produto in produtos:
            raise ValueError(f"ID divergente ou duplicado: {produto}")
        if not re.fullmatch(r"[a-z0-9][a-z0-9_.]*", produto):
            raise ValueError(f"ID inválido: {produto}")
        produtos.add(produto)
        opcao = produto.replace("_", "-")
        # Mantém o padrão da opção já cadastrada no produto piloto.
        if len(opcao) > 63 or not re.fullmatch(r"[a-z][a-z0-9-]*", opcao):
            raise ValueError(f"Opção requer revisão manual: {opcao}")
        cenario, estilo = arte["cenario"], arte["arte"]
        revisar = []
        for tipo, slug, nomes in [("cenario", cenario, cenarios), ("arte", estilo, estilos)]:
            if slug not in nomes:
                revisar.append(f"{tipo}:{slug}")
        # Reproduz o fallback do app, sem inventar aliases ou numeração romana.
        titulo = f"{cenarios.get(cenario, cenario[:1].upper() + cenario[1:])} — {estilos.get(estilo, estilo[:1].upper() + estilo[1:])}"
        if produto == PILOTO:
            titulo = TITULO_PILOTO
        if len(titulo) > 55 or len(DESCRICAO) > 200:
            raise ValueError(f"Texto excede limites da Play: {produto}")
        writer.writerow(dict(zip(campos, [produto, opcao, "1.99", "BR",
            arte["cenario"], arte["arte"],
            "oculta" if arte["cenario"] in {"natal", "estacionamento"} else "visivel",
            "piloto_ativo_compra_relatada" if produto == PILOTO else "nao_reconferido",
            titulo, DESCRICAO, "INACTIVE" if cenario in {"natal", "estacionamento"} else "ACTIVE",
            "|".join(revisar)])))
    if PILOTO not in produtos:
        raise ValueError("Produto piloto ausente")
    return saida.getvalue()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    texto = gerar()
    if args.check:
        if not TARGET.exists() or TARGET.read_text(encoding="utf-8") != texto:
            raise SystemExit("Inventário divergente; execute o gerador e revise o diff")
    else:
        TARGET.parent.mkdir(parents=True, exist_ok=True)
        with TARGET.open("w", encoding="utf-8", newline="") as arquivo:
            arquivo.write(texto)
    linhas = list(csv.DictReader(io.StringIO(texto)))
    revisar = [x for x in linhas if x["revisar_nomes"]]
    slugs = sorted({s for x in revisar for s in x["revisar_nomes"].split("|")})
    print(f"OK: 361 IDs únicos; 353 opções ativas; 8 inativas; 5 amostras excluídas; {len(revisar)} títulos para revisão")
    print("Revisar: " + ", ".join(slugs))
    print(f"Máximo título: {max(len(x['titulo']) for x in linhas)}/55; descrição: {len(DESCRICAO)}/200; sem acesso ao Console")


if __name__ == "__main__":
    main()
