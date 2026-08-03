# Guia — Data Safety Form e Content Rating (IARC)

> Respostas derivadas do **código atual**, não de suposição. Cada resposta
> abaixo tem a linha de código que a sustenta. Se o código mudar, esta folha
> mente — a tabela de rastreio em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md#rastreio-código--política)
> diz o que revisar.
>
> **Divisão de responsabilidade** (ver [SPEC.md](SPEC.md)): o Data Safety é
> declaração sobre o que o código faz — preenchido pelo Gabriel, com o Willian
> conferindo contra os textos legais. A conta do console é do titular legal.

**Última atualização:** 1º de agosto de 2026 · confere com o app 1.0.0

---

## Antes de começar

O Data Safety só aparece depois que o app existe no Play Console. Ordem:

1. Conta de desenvolvedor criada (US$ 25, uma vez — pode levar dias pra aprovar)
2. App criado no console (`com.atmosfera.wallpaper`)
3. **URL da política de privacidade já no ar** — o formulário a exige
4. Aí sim: Política do app → Segurança dos dados

---

## Data Safety Form

### Passo 1 — Visão geral

| Pergunta | Resposta | Base no código |
|---|---|---|
| O app coleta ou compartilha algum dos tipos de dados exigidos? | **Sim** | Localização é enviada à Open-Meteo |
| Todos os dados são criptografados em trânsito? | **Sim** | `WeatherRepository` usa `https://api.open-meteo.com/` |
| Você fornece um meio de o usuário pedir exclusão dos dados? | **Sim** | Não há servidor; limpar dados do app ou desinstalar apaga tudo — descrito na política §10 |

### Passo 2 — Tipos de dados

Marque **apenas** o que está abaixo. Marcar a mais é tão problemático quanto a
menos.

#### Localização → Localização aproximada — **SIM**

| Campo | Resposta |
|---|---|
| Coletado | Sim |
| Compartilhado | **Sim** — enviado à Open-Meteo |
| Obrigatório? | **Opcional** (o app funciona sem; cai no fallback de Guarapuava/PR) |
| Finalidade | **Funcionalidade do app** |
| Processado de forma efêmera? | Não (fica em cache local) |

#### Localização → Localização precisa — **SIM**

Contraintuitivo, mas obrigatório: o manifesto declara `ACCESS_FINE_LOCATION`, e
o Google compara a declaração com o que o APK pede. Declarar só "aproximada"
com `FINE` no manifesto é inconsistência — **causa nº1 de rejeição**.

> **Alternativa mais limpa:** remover `ACCESS_FINE_LOCATION` do manifesto. O
> código só checa `ACCESS_COARSE_LOCATION` (`LocationHelper.hasPermission()`) e
> pede `PRIORITY_BALANCED_POWER_ACCURACY` — a permissão fine não tem uso real.
> Mexe no manifesto → área de risco, exige PR. Se fizer isso, **desmarque este
> item** e ajuste a política §5.

Mesmas respostas da aproximada.

#### Compras no app — **NÃO marcar como coletado por você**

O Google Play Billing processa tudo. O app recebe só o resultado da compra e
grava um booleano local (`Plano`). Você não coleta histórico de compras — o
Google coleta, e isso é declarado por ele, não por você.

#### Tudo o mais — **NÃO**

Nome, e-mail, telefone, ID de usuário, contatos, fotos, arquivos, mensagens,
áudio, calendário, atividade no app, histórico de navegação, desempenho,
diagnósticos, ID de dispositivo, ID de publicidade: **nenhum**. Não há conta,
analytics, crash reporting ou SDK de anúncios — política §4 e §12.

### Passo 3 — Práticas de segurança

| Pergunta | Resposta |
|---|---|
| Dados criptografados em trânsito | **Sim** (HTTPS/TLS) |
| Usuário pode pedir exclusão | **Sim** |
| Comprometido com a Play Families Policy | Não (o app não é direcionado a crianças) |
| Passou por avaliação de segurança independente | Não |

### Armadilha do backup

`AndroidManifest.xml` tem `allowBackup="true"`: cache e preferências podem ir
pro backup do Android, na conta Google **do usuário**. Não é coleta sua (é
mecanismo do sistema) e **não** precisa ser declarado como compartilhamento —
mas está documentado na política §3.6 para ser honesto. Se um revisor
perguntar, é essa a resposta.

---

## Content Rating (IARC)

Questionário rápido. O Atmosfera é um papel de parede sem conteúdo gerado por
usuário, sem interação social e sem compras aleatórias.

| Pergunta | Resposta |
|---|---|
| Categoria do app | **Utilitário / Personalização** (não é jogo) |
| Violência, sangue, conteúdo sexual, linguagem imprópria, drogas | **Não** para todas |
| Jogos de azar / simulação de apostas | **Não** |
| Usuários interagem ou trocam conteúdo entre si | **Não** |
| Compartilha localização com outros usuários | **Não** — a localização vai só ao serviço de meteorologia, nunca a outro usuário |
| Permite compras digitais | **Sim** — Premium e cenários avulsos |
| Contém anúncios | **Não** — nenhum SDK de anúncios integrado |
| Conteúdo gerado por usuário | **Não** |

Resultado esperado: **Livre / L (todas as idades)** nas classificações
brasileira e internacional.

> Um cenário do catálogo é um **tanque de guerra em campo de batalha**
> (`cenario_tanque`). É arte estática de ambiente, sem pessoas, combate, sangue
> ou representação de violência — não muda a resposta de "violência". Se um dia
> entrar cenário com figuras humanas em conflito, refaça o questionário.

---

## Depois de enviar

- **Data Safety e política precisam concordar.** Se você marcar algo aqui que a
  política não menciona (ou o contrário), é rejeição. As duas fontes são este
  guia e `PRIVACIDADE.md`.
- **Toda mudança em `weather/`, `billing/` ou no manifesto** obriga a revisar os
  dois. Ver a tabela de rastreio no `CHECKLIST_PUBLICACAO.md`.
- Reenviar o formulário é gratuito e rápido — errar por omissão e corrigir é
  melhor que declarar a mais "por segurança".
