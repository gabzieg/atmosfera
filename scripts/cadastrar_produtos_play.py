#!/usr/bin/env python3
"""Concilia artes na API Publishing; sem --apply apenas lê e simula."""
import argparse
import copy
import csv
import json
import os
import sys
from pathlib import Path

from preparar_produtos_play import PILOTO, ROOT, TARGET, gerar

PACKAGE = "com.terra.wallpaper"
PRICE = {"currencyCode": "BRL", "units": "1", "nanos": 990000000}
REGIONS = "regionalPricingAndAvailabilityConfigs"


class ErroOperacional(ValueError):
    """Mensagem controlada, sem resposta bruta da API ou credencial."""


def inventario():
    if TARGET.read_text(encoding="utf-8") != gerar():
        raise ErroOperacional("Inventário divergente; execute preparar_produtos_play.py --check")
    with TARGET.open(encoding="utf-8", newline="") as arquivo:
        return list(csv.DictReader(arquivo))


def produto_novo(linha):
    return {"packageName": PACKAGE, "productId": linha["product_id"],
            "listings": [{"languageCode": "pt-BR", "title": linha["titulo"], "description": linha["descricao"]}],
            "purchaseOptions": [{"purchaseOptionId": linha["purchase_option_id_proposto"],
                                 "buyOption": {"legacyCompatible": True},
                                 REGIONS: [{"regionCode": "BR", "price": PRICE.copy(),
                                            "availability": "AVAILABLE"}]}]}


def preco_correto(preco):
    return (preco.get("currencyCode") == "BRL" and str(preco.get("units", "0")) == "1"
            and preco.get("nanos", 0) == 990000000)


def comparar(linha, atual):
    """A única correção em produto existente é a disponibilidade do piloto."""
    erros = []
    esperado = produto_novo(linha)
    if atual.get("packageName") != PACKAGE:
        erros.append("pacote")
    if atual.get("listings") != esperado["listings"]:
        erros.append("título/descrição/idiomas")
    opcoes = atual.get("purchaseOptions", [])
    if len(opcoes) != 1 or opcoes[0].get("purchaseOptionId") != linha["purchase_option_id_proposto"]:
        return erros + ["opções"], None
    opcao = opcoes[0]
    buy = opcao.get("buyOption")
    if buy is None or buy.get("multiQuantityEnabled", False) or not buy.get("legacyCompatible", False):
        erros.append("tipo/compatibilidade")
    if "rentOption" in opcao:
        erros.append("aluguel")
    estado = opcao.get("state")
    estados = {"ACTIVE"} if linha["estado_opcao"] == "ACTIVE" else {"DRAFT", "INACTIVE"}
    if estado not in estados:
        erros.append("estado")
    configs = opcao.get(REGIONS, [])
    brasil = [x for x in configs if x.get("regionCode") == "BR"]
    if len(brasil) != 1 or not preco_correto(brasil[0].get("price", {})):
        erros.append("preço BR")
    if len({x.get("regionCode") for x in configs}) != len(configs):
        erros.append("regiões duplicadas")
    if any(x.get("availability") not in {"AVAILABLE", "NO_LONGER_AVAILABLE"} for x in configs):
        erros.append("disponibilidade desconhecida")
    if brasil and brasil[0].get("availability") != "AVAILABLE":
        erros.append("Brasil indisponível")
    novas = opcao.get("newRegionsConfig")
    if novas and novas.get("availability") not in {"AVAILABLE", "NO_LONGER_AVAILABLE"}:
        erros.append("regiões novas desconhecidas")
    fora_br = any(x.get("regionCode") != "BR" and x.get("availability") == "AVAILABLE" for x in configs)
    novas_ativas = novas and novas.get("availability") == "AVAILABLE"
    ajuste = None
    if fora_br or novas_ativas:
        if linha["product_id"] != PILOTO:
            erros.append("países/regiões novas")
        else:
            ajuste = copy.deepcopy(opcao)
            for config in ajuste[REGIONS]:
                if config["regionCode"] != "BR" and config["availability"] == "AVAILABLE":
                    config["availability"] = "NO_LONGER_AVAILABLE"
            if novas_ativas:
                ajuste["newRegionsConfig"]["availability"] = "NO_LONGER_AVAILABLE"
            ajuste.pop("state", None)
    return erros, ajuste


def planejar(linhas, atuais):
    por_id = {}
    for produto in atuais:
        pid = produto["productId"]
        if pid in por_id:
            raise ErroOperacional("Resposta com produto duplicado")
        por_id[pid] = produto
    plano = {"criar": [], "ajustar": [], "sem_mudanca": [], "divergencias": [], "extras": []}
    for linha in linhas:
        pid = linha["product_id"]
        atual = por_id.pop(pid, None)
        if atual is None:
            plano["criar"].append(linha)
            continue
        erros, ajuste = comparar(linha, atual)
        if erros:
            plano["divergencias"].append({"product_id": pid, "motivos": erros})
        elif ajuste:
            plano["ajustar"].append({"product_id": pid, "purchaseOptions": [ajuste]})
        else:
            plano["sem_mudanca"].append(pid)
    plano["extras"] = sorted(por_id)
    return plano


def resumo(plano, atuais):
    conhecidos = {x["productId"]: x for x in atuais}
    alvo = (set(plano["sem_mudanca"]) | {x["product_id"] for x in plano["ajustar"]}
            | {x["product_id"] for x in plano["divergencias"]})
    estados = [p.get("state") for pid in alvo for p in conhecidos[pid].get("purchaseOptions", [])]
    return {"criar": len(plano["criar"]), "ajustar": len(plano["ajustar"]),
            "sem_mudanca": len(plano["sem_mudanca"]), "extras_intocados": len(plano["extras"]),
            "divergencias": len(plano["divergencias"]), "ativos_efetivos": estados.count("ACTIVE"),
            "draft_efetivos": estados.count("DRAFT"), "inativos_efetivos": estados.count("INACTIVE"),
            "indisponiveis_efetivos": sum(x in {"DRAFT", "INACTIVE"} for x in estados)}


def executar(api, linhas, aplicar=False):
    atuais = api.listar()
    plano = planejar(linhas, atuais)
    consulta = [{"product_id": p["productId"], "opcoes": [
        {"purchase_option_id": o.get("purchaseOptionId"), "estado": o.get("state"),
         "paises_disponiveis": [r.get("regionCode") for r in o.get(REGIONS, []) if r.get("availability") == "AVAILABLE"],
         "preco_BR": [r.get("price") for r in o.get(REGIONS, []) if r.get("regionCode") == "BR"],
         "regioes_novas": o.get("newRegionsConfig", {}).get("availability")}
        for o in p.get("purchaseOptions", [])]} for p in atuais]
    print(json.dumps({"consulta": consulta, "plano": plano, "contagens": resumo(plano, atuais)}, ensure_ascii=False, indent=2))
    if not aplicar:
        return plano
    if plano["divergencias"]:
        raise ErroOperacional("Aplicação bloqueada: concilie as divergências sem sobrescrever o Console")
    versao = api.versao_regioes() if plano["criar"] or plano["ajustar"] else None
    for linha in plano["criar"]:
        pid = linha["product_id"]
        if api.obter(pid) is not None:
            raise ErroOperacional("Console mudou após simulação; simule novamente")
        criado = api.criar(produto_novo(linha), versao)
        # O estado é output-only: nunca enviar ACTIVE no upsert.
        conferido = copy.deepcopy(criado)
        for opcao in conferido.get("purchaseOptions", []):
            opcao["state"] = "ACTIVE" if linha["estado_opcao"] == "ACTIVE" else "DRAFT"
        erros, ajuste = comparar(linha, conferido)
        if erros or ajuste or criado["purchaseOptions"][0].get("state") != "DRAFT":
            raise ErroOperacional("Criação não confirmada como DRAFT esperado; interrompido sem ativação")
        if linha["estado_opcao"] == "ACTIVE":
            api.ativar(pid, linha["purchase_option_id_proposto"])
    for item in plano["ajustar"]:
        pid = item["product_id"]
        anterior = next(x for x in atuais if x["productId"] == pid)
        if api.obter(pid) != anterior:
            raise ErroOperacional("Piloto mudou após simulação; simule novamente")
        api.ajustar_piloto(item, versao)
    depois = api.listar()
    restante = planejar(linhas, depois)
    print(json.dumps({"apos_aplicar": resumo(restante, depois)}, ensure_ascii=False))
    if restante["criar"] or restante["ajustar"] or restante["divergencias"]:
        raise ErroOperacional("Releitura ainda diverge do plano; não considerar cadastro concluído")
    return restante


def validar_chave(caminho):
    if not caminho:
        raise ErroOperacional("Informe --credentials ou TERRA_PLAY_SERVICE_ACCOUNT_KEY; chave fora do repositório")
    chave = Path(caminho).expanduser().resolve()
    if chave == ROOT or ROOT in chave.parents:
        raise ErroOperacional("Chave dentro do repositório recusada")
    if not chave.is_file():
        raise ErroOperacional("Arquivo de credencial não encontrado fora do repositório")
    return chave


class PublishingAPI:
    def __init__(self, chave):
        try:
            from google.oauth2 import service_account
            from google.auth.transport.requests import AuthorizedSession
        except ImportError:
            raise ErroOperacional("Instale scripts/requirements-play.txt num ambiente virtual externo") from None
        try:
            credenciais = service_account.Credentials.from_service_account_file(
                str(chave), scopes=["https://www.googleapis.com/auth/androidpublisher"])
            self.session = AuthorizedSession(credenciais)
        except Exception:
            raise ErroOperacional("Não foi possível carregar a credencial; conteúdo omitido") from None

    def requisitar(self, metodo, recurso, corpo=None, params=None, ausente=False):
        url = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/{recurso}"
        try:
            resposta = self.session.request(metodo, url, json=corpo, params=params, timeout=30)
            if ausente and resposta.status_code == 404:
                return None
            if not resposta.ok:
                raise ErroOperacional(f"API Publishing respondeu HTTP {resposta.status_code}; detalhes omitidos")
            return resposta.json()
        except ErroOperacional:
            raise
        except ValueError:
            raise ErroOperacional("Resposta da API inválida ou recusada; detalhes omitidos") from None
        except Exception:
            # Erros de autenticação podem conter e-mail, chave ou token; nunca propagá-los.
            raise ErroOperacional("Falha de autenticação/rede na API; detalhes omitidos") from None

    def listar(self):
        produtos, token, vistos = [], None, set()
        while True:
            pagina = self.requisitar("GET", "oneTimeProducts", params={"pageSize": 1000, "pageToken": token})
            produtos.extend(pagina.get("oneTimeProducts", []))
            token = pagina.get("nextPageToken")
            if not token:
                return produtos
            if token in vistos:
                raise ErroOperacional("API repetiu token de paginação")
            vistos.add(token)

    def obter(self, pid):
        return self.requisitar("GET", f"oneTimeProducts/{pid}", ausente=True)

    def versao_regioes(self):
        # Conversão não grava catálogo. Usamos só a versão; nunca os preços convertidos.
        resultado = self.requisitar("POST", "pricing:convertRegionPrices", {"price": PRICE})
        versao = resultado.get("regionVersion", {}).get("version")
        if not versao:
            raise ErroOperacional("API não informou versão de regiões")
        return versao

    def criar(self, produto, versao):
        # Em criação a API ignora a máscara. Se surgir produto concorrente, a
        # máscara restrita ao ID imutável impede sobrescrever preço/texto/opções.
        return self.requisitar("PATCH", f"onetimeproducts/{produto['productId']}", produto,
                               {"allowMissing": "true", "updateMask": "productId", "regionsVersion.version": versao})

    def ativar(self, pid, opcao):
        return self.requisitar("POST", f"oneTimeProducts/{pid}/purchaseOptions:batchUpdateStates",
                               {"requests": [{"activatePurchaseOptionRequest": {
                                   "packageName": PACKAGE, "productId": pid, "purchaseOptionId": opcao}}]})

    def ajustar_piloto(self, item, versao):
        if item["product_id"] != PILOTO:
            raise ErroOperacional("Ajuste restrito ao piloto")
        corpo = {"packageName": PACKAGE, "productId": PILOTO, "purchaseOptions": item["purchaseOptions"]}
        # FieldMask não atravessa listas: preserva a opção relida, muda apenas
        # availability e omite state (output-only). Título fica fora da máscara.
        mascara = "purchaseOptions"
        return self.requisitar("PATCH", f"onetimeproducts/{PILOTO}", corpo,
                               {"allowMissing": "false", "updateMask": mascara, "regionsVersion.version": versao})


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--credentials", default=os.environ.get("TERRA_PLAY_SERVICE_ACCOUNT_KEY"))
    parser.add_argument("--apply", action="store_true", help="Escreve no Console; exige autorização sobre o plano")
    args = parser.parse_args()
    try:
        chave = validar_chave(args.credentials)
        executar(PublishingAPI(chave), inventario(), args.apply)
    except ErroOperacional as erro:
        print(f"ERRO: {erro}", file=sys.stderr)
        return 1
    except (ValueError, OSError):
        # Diagnósticos controlados são emitidos separadamente sem traceback ou paths secretos.
        print("ERRO: confira credencial externa, dependências, inventário e divergências do plano; operação interrompida", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
