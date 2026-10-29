"""Regressões sem rede do contrato de dinheiro/credenciais (incidente A7, 29/10/2026)."""
import contextlib
import copy
import io
import tempfile
import unittest
from types import SimpleNamespace
from pathlib import Path
from unittest.mock import patch

import cadastrar_produtos_play as play


class ConsoleSimulado(play.PublishingAPI):
    def __init__(self, produtos):
        self.produtos = {p['productId']: copy.deepcopy(p) for p in produtos}
        self.chamadas = []

    def requisitar(self, metodo, recurso, corpo=None, params=None, ausente=False):
        self.chamadas.append((metodo, recurso, copy.deepcopy(corpo), params))
        if recurso == 'oneTimeProducts':
            return {'oneTimeProducts': copy.deepcopy(list(self.produtos.values()))}
        if metodo == 'GET':
            return copy.deepcopy(self.produtos.get(recurso.split('/')[-1]))
        if recurso == 'pricing:convertRegionPrices':
            return {'regionVersion': {'version': '2025/01'}}
        if metodo == 'PATCH':
            pid = corpo['productId']
            if params['allowMissing'] == 'true':
                assert params['updateMask'] == 'productId'
                assert pid not in self.produtos
                self.produtos[pid] = copy.deepcopy(corpo)
                for opcao in self.produtos[pid]['purchaseOptions']:
                    assert 'state' not in opcao
                    opcao['state'] = 'DRAFT'
            else:
                assert pid == play.PILOTO
                assert params['updateMask'] == 'purchaseOptions'
                anterior = self.produtos[pid]['purchaseOptions'][0]
                estado = anterior['state']
                self.produtos[pid]['purchaseOptions'] = copy.deepcopy(corpo['purchaseOptions'])
                self.produtos[pid]['purchaseOptions'][0]['state'] = estado
            return copy.deepcopy(self.produtos[pid])
        assert recurso.endswith('purchaseOptions:batchUpdateStates')
        pedido = corpo['requests'][0]['activatePurchaseOptionRequest']
        self.produtos[pedido['productId']]['purchaseOptions'][0]['state'] = 'ACTIVE'
        return {'oneTimeProducts': [copy.deepcopy(self.produtos[pedido['productId']])]}

    @property
    def escritas(self):
        return [c for c in self.chamadas if c[0] != 'GET' and c[1] != 'pricing:convertRegionPrices']


def existente(linha):
    produto = play.produto_novo(linha)
    produto['purchaseOptions'][0]['state'] = 'ACTIVE' if linha['estado_opcao'] == 'ACTIVE' else 'DRAFT'
    return produto


class CadastroTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.linhas = play.inventario()
        cls.piloto_linha = next(x for x in cls.linhas if x['product_id'] == play.PILOTO)

    def piloto(self):
        produto = existente(self.piloto_linha)
        produto['listings'][0]['title'] = 'Biblioteca — Van Gogh'
        opcao = produto['purchaseOptions'][0]
        opcao[play.REGIONS].append({'regionCode': 'US', 'price': {'currencyCode': 'USD', 'units': '2'},
                                   'availability': 'AVAILABLE'})
        opcao['newRegionsConfig'] = {'availability': 'AVAILABLE',
                                    'usdPrice': {'currencyCode': 'USD', 'units': '2'},
                                    'eurPrice': {'currencyCode': 'EUR', 'units': '2'}}
        return produto

    def executar(self, api, aplicar=False):
        with contextlib.redirect_stdout(io.StringIO()):
            return play.executar(api, self.linhas, aplicar)

    def test_padrao_nao_escreve_e_planeja_360_mais_piloto(self):
        api = ConsoleSimulado([{'productId': 'terra_premium'}, self.piloto()])
        plano = self.executar(api)
        self.assertEqual(len(plano['criar']), 360)
        self.assertEqual(sum(x['estado_opcao'] == 'ACTIVE' for x in plano['criar']), 352)
        self.assertEqual(sum(x['estado_opcao'] == 'INACTIVE' for x in plano['criar']), 8)
        self.assertEqual([x['product_id'] for x in plano['ajustar']], [play.PILOTO])
        self.assertEqual(plano['divergencias'], [])
        self.assertEqual(api.escritas, [])

    def test_aplicar_rele_segunda_execucao_vazia_e_preserva_extras(self):
        premium = {'productId': 'terra_premium', 'listings': [{'title': 'Premium fictício'}]}
        extra = {'productId': 'produto_extra_ficticio'}
        piloto = self.piloto()
        api = ConsoleSimulado([premium, extra, piloto])
        plano = self.executar(api, True)
        self.assertFalse(plano['criar'] or plano['ajustar'] or plano['divergencias'])
        self.assertEqual(api.produtos['terra_premium'], premium)
        self.assertEqual(api.produtos['produto_extra_ficticio'], extra)
        esperado = copy.deepcopy(piloto)
        esperado['purchaseOptions'][0][play.REGIONS][1]['availability'] = 'NO_LONGER_AVAILABLE'
        esperado['purchaseOptions'][0]['newRegionsConfig']['availability'] = 'NO_LONGER_AVAILABLE'
        self.assertEqual(api.produtos[play.PILOTO], esperado)
        self.assertEqual(api.produtos[play.PILOTO]['listings'][0]['title'], 'Biblioteca — Van Gogh')
        contagens = play.resumo(plano, list(api.produtos.values()))
        self.assertEqual((contagens['ativos_efetivos'], contagens['draft_efetivos'], contagens['inativos_efetivos']), (353, 8, 0))
        for linha in self.linhas:
            opcao = api.produtos[linha['product_id']]['purchaseOptions'][0]
            if linha['product_id'] != play.PILOTO:
                self.assertEqual(opcao[play.REGIONS], [{'regionCode': 'BR', 'price': play.PRICE, 'availability': 'AVAILABLE'}])
                self.assertNotIn('newRegionsConfig', opcao)
            self.assertEqual(opcao['state'], 'ACTIVE' if linha['estado_opcao'] == 'ACTIVE' else 'DRAFT')
        antes = len(api.escritas)
        self.executar(api, True)
        self.assertEqual(len(api.escritas), antes)
        self.assertEqual(sum(c[1] == 'oneTimeProducts' for c in api.chamadas), 4)

    def test_preco_divergente_bloqueia_toda_escrita(self):
        produto = existente(self.piloto_linha)
        produto['purchaseOptions'][0][play.REGIONS][0]['price']['units'] = '9'
        api = ConsoleSimulado([produto])
        plano = self.executar(api)
        self.assertIn('preço BR', plano['divergencias'][0]['motivos'])
        with self.assertRaises(play.ErroOperacional):
            self.executar(api, True)
        self.assertEqual(api.escritas, [])
        self.assertEqual(api.produtos[produto['productId']], produto)

    def test_resumo_conta_estados_divergentes_sem_incluir_extras(self):
        # Incidente de 29/10/2026: leitura real retornou dez ACTIVE, resumo mostrou zero.
        for estado in ['ACTIVE', 'DRAFT', 'INACTIVE']:
            with self.subTest(estado=estado):
                produto = existente(self.linhas[0])
                produto['purchaseOptions'][0]['state'] = estado
                produto['purchaseOptions'][0][play.REGIONS][0]['price']['units'] = '9'
                premium = {'productId': 'terra_premium', 'purchaseOptions': [{'state': 'ACTIVE'}]}
                api = ConsoleSimulado([produto, premium])
                plano = self.executar(api)
                contagens = play.resumo(plano, list(api.produtos.values()))
                self.assertEqual(contagens['divergencias'], 1)
                self.assertEqual(contagens['ativos_efetivos'], int(estado == 'ACTIVE'))
                self.assertEqual(contagens['draft_efetivos'], int(estado == 'DRAFT'))
                self.assertEqual(contagens['inativos_efetivos'], int(estado == 'INACTIVE'))
                self.assertEqual(contagens['indisponiveis_efetivos'], int(estado != 'ACTIVE'))
                self.assertEqual(api.escritas, [])

    def test_titulo_estado_tipo_regiao_e_opcao_divergentes(self):
        for alterar in [lambda p: p['listings'][0].update(title='Divergência fictícia'),
                        lambda p: p['purchaseOptions'][0].update(state='INACTIVE'),
                        lambda p: p['purchaseOptions'][0].update(buyOption={'multiQuantityEnabled': True}),
                        lambda p: p['purchaseOptions'][0].update(purchaseOptionId='outro-id'),
                        lambda p: p['purchaseOptions'][0].update(newRegionsConfig={'availability': 'AVAILABLE'})]:
            with self.subTest(alterar=alterar):
                produto = existente(self.linhas[0])
                alterar(produto)
                api = ConsoleSimulado([produto])
                with self.assertRaises(play.ErroOperacional):
                    self.executar(api, True)
                self.assertEqual(api.escritas, [])

    def test_piloto_rejeita_outro_titulo_descricao_ou_idioma(self):
        for campo, valor in [('title', 'Biblioteca — Vangogh'), ('description', 'Outra descrição fictícia'), ('languageCode', 'en-US')]:
            with self.subTest(campo=campo):
                piloto = self.piloto()
                piloto['listings'][0][campo] = valor
                api = ConsoleSimulado([piloto])
                with self.assertRaises(play.ErroOperacional):
                    self.executar(api, True)
                self.assertEqual(api.escritas, [])

    def test_recusa_chave_no_repo_inclusive_link_simbolico(self):
        with self.assertRaisesRegex(play.ErroOperacional, 'dentro do repositório'):
            play.validar_chave(str(play.ROOT / 'chave-ficticia.json'))
        with tempfile.TemporaryDirectory() as pasta:
            link = Path(pasta) / 'link.json'
            link.symlink_to(play.TARGET)
            with self.assertRaisesRegex(play.ErroOperacional, 'dentro do repositório'):
                play.validar_chave(str(link))
        with self.assertRaisesRegex(play.ErroOperacional, 'Informe --credentials'):
            play.validar_chave(None)

    def test_paginacao(self):
        api = ConsoleSimulado([])
        with patch.object(api, 'requisitar', side_effect=[{'oneTimeProducts': [{'productId': 'a'}], 'nextPageToken': 'p2'},
                                                        {'oneTimeProducts': [{'productId': 'b'}]}]) as req:
            self.assertEqual(len(api.listar()), 2)
            self.assertEqual(req.call_args.kwargs['params']['pageToken'], 'p2')

    def test_erro_autenticacao_nao_expoe_resposta_e_tem_timeout(self):
        api = play.PublishingAPI.__new__(play.PublishingAPI)
        api.session = SimpleNamespace(request=unittest.mock.Mock(side_effect=RuntimeError('SEGREDO_FICTICIO')))
        with self.assertRaises(play.ErroOperacional) as erro:
            api.obter(play.PILOTO)
        self.assertNotIn('SEGREDO_FICTICIO', str(erro.exception))
        self.assertEqual(api.session.request.call_args.kwargs['timeout'], 30)

    def test_main_sem_credencial_erro_claro_sem_traceback(self):
        saida = io.StringIO()
        with patch.dict('os.environ', {}, clear=True), patch('sys.argv', ['cadastro']), contextlib.redirect_stderr(saida):
            self.assertEqual(play.main(), 1)
        self.assertIn('Informe --credentials', saida.getvalue())
        self.assertNotIn('Traceback', saida.getvalue())

    def test_mudanca_entre_plano_e_escrita_interrompe(self):
        api = ConsoleSimulado([])
        with patch.object(api, 'obter', return_value={'productId': self.linhas[0]['product_id']}):
            with self.assertRaisesRegex(play.ErroOperacional, 'Console mudou'):
                self.executar(api, True)
        self.assertEqual(api.escritas, [])

    def test_releitura_divergente_nao_declara_sucesso(self):
        produtos = [existente(x) for x in self.linhas]
        api = ConsoleSimulado(produtos)
        with patch.object(api, 'listar', side_effect=[produtos, []]):
            with self.assertRaisesRegex(play.ErroOperacional, 'Releitura ainda diverge'):
                self.executar(api, True)


if __name__ == '__main__':
    unittest.main()
