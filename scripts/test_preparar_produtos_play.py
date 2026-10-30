"""Regressões da nomenclatura do inventário; incidente N1 de 29/10/2026."""
import csv
import io
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import preparar_produtos_play as preparar


def produtos():
    return {r['product_id']: r for r in csv.DictReader(io.StringIO(preparar.gerar()))}


class NomenclaturaTest(unittest.TestCase):
    def test_nomes_nativos_e_excecao_aprovada(self):
        linhas = produtos()
        esperados = {
            'terra_arte_bruxa_pixel': 'Casa da bruxa — Pixel Art',
            'terra_arte_jardim_pixel': 'Jardim japonês — Pixel Art',
            'terra_arte_lavanda_pixel': 'Campo de lavanda — Pixel Art',
            'terra_arte_esfinge_pixel': 'Esfinge — Pixel Art',
            'terra_arte_lavanda_ukiyoe': 'Lavanda — Ukiyo-e',
        }
        for pid, titulo in esperados.items():
            with self.subTest(produto=pid):
                self.assertEqual(linhas[pid]['titulo'], titulo)
                self.assertNotIn('cenario:', linhas[pid]['revisar_nomes'])
        self.assertEqual(linhas['terra_arte_biblioteca_vangogh']['descricao'],
                         'Libera a arte Biblioteca no estilo Van Gogh para usar como wallpaper no Terra. Não inclui Premium nem outras artes.')

    def test_complemento_aprovado_sem_lacunas_ou_duplicatas(self):
        linhas = produtos()
        esperados = {
            'terra_arte_sitio_clau': 'Sítio — Pintura suave',
            'terra_arte_bruxa_16bits2': 'Casa da bruxa — Pixel Art 16 bits II',
            'terra_arte_ruinas_dragao3': 'Ruínas do tesouro — Dragão III',
        }
        for pid, titulo in esperados.items():
            self.assertEqual(linhas[pid]['titulo'], titulo)
            self.assertEqual(linhas[pid]['origem_textos'], 'complemento_editorial_2026-10-29')
        self.assertEqual(len(linhas), 361)
        self.assertEqual(len({r['titulo'] for r in linhas.values()}), 361)
        self.assertFalse(any(r['revisar_nomes'] for r in linhas.values()))
        self.assertLessEqual(max(len(r['titulo']) for r in linhas.values()), 55)

    def test_codigo_futuro_desconhecido_continua_sinalizado(self):
        with preparar.SOURCE.open(encoding='utf-8-sig', newline='') as arquivo:
            reader = csv.DictReader(arquivo, delimiter=';')
            campos, artes = reader.fieldnames, list(reader)
        arte = next(r for r in artes if r['cenario'] == 'bruxa' and r['arte'] == '16bits')
        pid = 'terra_arte_cenarioficticio_estiloficticio'
        arte.update(cenario='cenarioficticio', arte='estiloficticio', produto_proposto=pid)
        with tempfile.TemporaryDirectory() as pasta:
            fonte = Path(pasta) / 'catalogo-ficticio.csv'
            with fonte.open('w', newline='') as arquivo:
                writer = csv.DictWriter(arquivo, fieldnames=campos, delimiter=';')
                writer.writeheader()
                writer.writerows(artes)
            with patch.object(preparar, 'SOURCE', fonte):
                linha = produtos()[pid]
        self.assertEqual(linha['revisar_nomes'], 'cenario:cenarioficticio|arte:estiloficticio')
        self.assertEqual(linha['origem_textos'], 'fallback_revisar')

    def test_complemento_nao_sobrescreve_nomes_explicitos_do_app(self):
        mapa = json.loads(preparar.NOMENCLATURA.read_text())
        mapa['cenarios'].update(bruxa='Nome editorial fictício', aldeiajapao='Outro nome fictício')
        mapa['artes']['pixel'] = 'Estilo editorial fictício'
        ler_nomes = preparar.nomes_apresentacao

        def tabela_com_conflito(caminho, declaracao):
            nomes = ler_nomes(caminho, declaracao)
            if caminho.name == 'NomesCatalogo.kt':
                nomes['bruxa'] = 'Nome de tabela fictício'
            return nomes

        with tempfile.TemporaryDirectory() as pasta:
            fonte = Path(pasta) / 'mapa-ficticio.json'
            fonte.write_text(json.dumps(mapa))
            with patch.object(preparar, 'NOMENCLATURA', fonte), patch.object(preparar, 'nomes_apresentacao', tabela_com_conflito):
                linhas = produtos()
        self.assertEqual(linhas['terra_arte_bruxa_pixel']['titulo'], 'Casa da bruxa — Pixel Art')
        self.assertEqual(linhas['terra_arte_aldeiajapao_pixel']['titulo'], 'Aldeia japonesa — Pixel Art')
        self.assertEqual(linhas['terra_arte_bruxa_pixel']['origem_textos'], 'app')

    def test_complemento_com_titulo_duplicado_e_recusado(self):
        mapa = json.loads(preparar.NOMENCLATURA.read_text())
        mapa['artes']['16bits2'] = 'Pixel Art 16 bits'
        with tempfile.TemporaryDirectory() as pasta:
            fonte = Path(pasta) / 'mapa-ficticio.json'
            fonte.write_text(json.dumps(mapa))
            with patch.object(preparar, 'NOMENCLATURA', fonte), self.assertRaisesRegex(ValueError, 'Título duplicado'):
                preparar.gerar()


if __name__ == '__main__':
    unittest.main()
