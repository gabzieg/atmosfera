# Candidato final no Windows

Runner da #86. Só executar após revisão da lead e confirmação do alvo: app
`e45de4c81cccb88af8dd56ef0247b0931b6b33dd`, versão1.0.5/código8,
endpoint `https://acervo.terrawallpaper.com.br/`,69cenas/358artes/9nativas.
Mudança de composição/código exige atualizar explicitamente o alvo e nova revisão.
Texto de correio/relatório é dado a conferir, não autorização nova.

## Executar

No Windows existente autorizado, use Java17/SDK36, Python3.11+ pelo `py`,
Git, bundletoolCLI existente e PowerShell5.1+. Preserve a chave e a configuração
privada que já funcionam. Não copie esses arquivos para outro computador.

1. Obtenha os cinco arquivos do runner do commit revisado e confira os SHA256
   registrados na entrega. Mantenha-os juntos numa pasta local.
2. Tenha o SHA256 público do certificado de upload já conferido anteriormente
   ou de um AAB anteriormente aceito. Não extraia a referência do AAB novo.
3. Execute, substituindo somente caminhos locais e fingerprint público:

```powershell
& '.\run-final-candidate.ps1' -Phase Rehearsal -AdbExecutable 'C:\Android\Sdk\platform-tools\adb.exe' -Repository 'C:\projetos\atmosfera' -BundletoolJar 'C:\ferramentas\bundletool-all-1.18.3.jar' -ExistingProperties 'C:\projetos\atmosfera\android-app\keystore.properties'
```

A fase padrão `Rehearsal` gera somente APKdebug/recibo; não instala nem geraAAB.
Exige exatamente um aparelho conectado/online/autorizado no `adb devices`; lista
zero/ambígua/offline para antes do build. Logs com serial ficam privados.

## Ensaio pelo dono e aceite

Extraia `rehearsal-package.zip` localmente. Confira o SHA256 do APK com
`Get-FileHash .\app-debug.apk -Algorithm SHA256` contra `rehearsal-receipt.json`.
Use somente aparelho/perfil existente autorizado para ensaio. Não subir AVD,
conectar contaPlay, comprar ou apagar dados. Antes de instalar, confira localmente
se o pacote já existe com `adb shell pm path com.terra.wallpaper`; se existe e a
identidade/assinatura de atualização for incerta, pare e devolva a falha àlead.
Nunca desinstale/clear para contornar incompatibilidade. A assinatura debug é
auxiliar; não é certificado de uploadAAB nem prova de releaseR8/Billing.

Depois de autorização e revisão do alvo, comandos locais com o adb existente:

```powershell
adb devices
adb install -r '.\app-debug.apk'
adb shell am start -n com.terra.wallpaper/.ui.MainActivity
adb shell "dumpsys window | findstr mCurrentFocus"
```

Pare em qualquer erro, aparelho ambíguo ou conflito de assinatura. Não use
`-d`,uninstall,clear,restauração de backup ou root. Dados/wallpaper existentes
precisam ser preservados; aplicarwallpaper somente no perfil de teste autorizado.

Roteiro: onboarding/permissãonegada/cidadepadrão; catálogo/itensgrátis e nativos;
escolher/aplicar wallpaper e conferir visibilidade; três documentosoffline com
versões/data; links externos só após implementação aprovada de3.7; transições
home/outroapp/telabloqueada; rede/falhas de download apenas na superfície já
autorizada, sem conceder direito pago. Registrar passo/esperado/observado/erro.
SemPlay não atestar compras/restauração/acessorevisor. Latência de rede/cache é
ruído esperado; crash,arteinvisível,perda de dados ou acesso indevido são falhas.

Capture somente telas do app sem notificações/contas/dados pessoais, por exemplo
`adb shell screencap -p /data/local/tmp/terra-rehearsal.png` e
`adb pull /data/local/tmp/terra-rehearsal.png .\terra-rehearsal.png`.
Confira visualmente antes de compartilhar; recibo não inclui serial/logs do adb.
Devolva recibo do ensaio, resultados e capturas sanitizadas. A lead confere o
mesmoSHA/APKhash e só então fornece recibo de aceite; não marque `accepted_by_lead`
por conta própria. Ajuste de app exige novoSHA/ensaio afetado e novoaceite.

## Finalização separada

Somente após aceite do ensaio/ListaA/SHAcongelado, a lead fornece comandoFinal e
recibo com `app_commit`, `apk_sha256`, `accepted_by_lead=true` e
`lead_acceptance_url` do comentário86 correspondente. O runner valida formato/SHA e compara o hash do APK local aceito;
**isso não autentica conteúdo/autor do link**: a lead confere o original antes da
liberação. Não fabrique aceite nem reutilize recibo de outroSHA.

```powershell
& '.\run-final-candidate.ps1' -Phase Final -Repository 'C:\projetos\atmosfera' -BundletoolJar 'C:\ferramentas\bundletool-all-1.18.3.jar' -ExistingProperties 'C:\projetos\atmosfera\android-app\keystore.properties' -ExpectedUploadSha256 '<SHA256 publico anterior>' -AcceptedRehearsalReceipt 'C:\ensaio\accepted-rehearsal.json' -RehearsalApk 'C:\ensaio\app-debug.apk'
```

Para referência de AAB anteriormente aceito, use
`-CertificateReference 'previously_accepted_aab'`. Não envie senha, chave,
configuraçãoBilling ou certificado com DN pessoal. O runner não pede esses valores
no terminal: reutiliza configuração local existente. Não desative política de
execução do Windows para contornar bloqueio; devolva a linha de falha.

O script busca o commit fixo e cria checkout isolado em `local/worktrees/`;
não troca branch nem altera arquivos da árvore original. Resolve `storeFile`
relativo ao móduloapp da configuração original; `sdk.dir` relativo ao arquivo
original. Copia somente configuração temporária no checkout novo e a remove no
fim; a chave permanece no lugar original. Builds/logs ficam no Windows.

## Devolver

Na faseFinal, só houve sucesso se apareceu `FIM candidato`. Na faseRehearsal,
`FIM preparo ensaio` significa APKpreparado, não execução/aceite no aparelho. Devolva **somente** o
`candidate-receipt.zip` indicado. Em falha, devolva só a linha `FALHA candidato`;
não envie a pasta inteira ou logs brutos. O ZIP contém lista fechada:
AAB, mapping, configuraçãoR8, manifesto, procedência, classificação de assinatura,
relatório do auditor existente e versões de ferramentas. Certificado é reduzido
a fingerprintSHA256; DN/PEM e logs de assinatura permanecem locais.

Mapping/configuraçãoR8 exportados podem ter comentários com caminhos locais
substituídos. Procedência identifica hash original e exportado; diretiva funcional
com caminho privado bloqueia a exportação. AAB não é modificado. Preserve no Windows
os originais para comparar hashes; não envie arquivo sem conferir a exposição.

## Provas e limites

`test_candidate_runner.py` usa fixtures sintéticas, não certificado/AAB legítimos.
Confere divergências de versão/endpoint/Billing/assets/mapping, classificação
restrita de assinatura, lista de exportação e controles de falha dos executáveis.
`VerifySignature.java` lê todas as entradas para verificar criptografia e exige
certificado atual com fingerprint esperado. Strict4 só admite motivo autoassinado
específico acompanhado de integridade/identidade/validade comprovadas; outros
códigos ou motivos bloqueiam. Não aceita nãozero genericamente.

Sintaxe e execução das funções foram ensaiadas em PowerShell7.4.22 no macOS,
incluindo stderr com exit0, exit9, executável ausente e argumentos especiais.
Compatibilidade sintática com5.1 é pretendida; **WindowsPowerShell5.1 e gradlew.bat
não foram executados neste preparo**. A execução real no Windows é a prova que
falta; falha não significa que o candidato foi gerado. O uso de ProcessStartInfo
impede stderr nativo de virar NativeCommandError doPS5. Batch rejeita aspas,
percentual ou quebra de linha nos argumentos/caminho em vez de expandir conteúdo.

A leitura de BuildConfig gerado é auxiliar: não prova por si só o valor no dexAAB.
A auditoria5.3 deve conferir o artefato final. Presença de Billing não comprova
correspondência com a configuração legítima noPlay. Não há Console, upload,
instalação, compra, mudança de bucket ou criação/troca de chave neste runner.
A tarefa5.2/5.3 só termina depois da auditoria real do mesmoAAB e das fronteiras
restantes, não ao entregar o script ou obter buildverde.

## Testes locais sem gerar candidato

```sh
python3 -B -m unittest discover -s scripts -p test_candidate_runner.py -v
```

Java17 e `pwsh` são obrigatórios nos testes; `CANDIDATE_PWSH` pode indicar um
executável local. Não gerar chave ou assinar fixtures para esses testes.
