$ErrorActionPreference='Stop'
Set-StrictMode -Version Latest
$script:candidateStage='test'
$source=Join-Path $PSScriptRoot 'run-final-candidate.ps1'
$tokens=$null;$errors=$null
$ast=[System.Management.Automation.Language.Parser]::ParseFile($source,[ref]$tokens,[ref]$errors)
if($errors.Count){throw 'Erro de sintaxe'}
$definitions=$ast.FindAll({param($n) $n -is [System.Management.Automation.Language.FunctionDefinitionAst]},$true)
foreach($definition in $definitions){. ([scriptblock]::Create($definition.Extent.Text))}
$repositoryRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$artifactDirectory=Join-Path $repositoryRoot ('local/tmp/runner-test-' + [guid]::NewGuid().ToString() + ' with space')
New-Item -ItemType Directory -Force -Path $artifactDirectory|Out-Null
$originalPath=$env:PATH
try {
# #114, 10/11/2026: vários ApplicationInfo não podem virar um caminho concatenado.
$fixturePython=@(Get-Command python3 -CommandType Application -ErrorAction Stop)[0].Source
foreach($folder in @('first','second')) {
    $directory=Join-Path $artifactDirectory $folder
    New-Item -ItemType Directory -Path $directory|Out-Null
    New-Item -ItemType SymbolicLink -Path (Join-Path $directory 'python3') -Target $fixturePython|Out-Null
}
$env:PATH=(Join-Path $artifactDirectory 'first')+[IO.Path]::PathSeparator+(Join-Path $artifactDirectory 'second')+[IO.Path]::PathSeparator+$originalPath
if(@(Get-Command python3 -CommandType Application).Count -lt 2){throw 'Fixture PATH nao reproduziu multiplos executaveis'}
$python=@(Get-Command python3 -CommandType Application -ErrorAction Stop)[0].Source
$testArguments=@('espaco no caminho','aspas"internas','fim\','&naoexecutar','')
$expected=ConvertTo-Json -InputObject $testArguments -Compress
Invoke-Checked 'arguments' $python (@('-c','import sys,json;print(json.dumps(sys.argv[1:]))')+$testArguments)
$received=Get-Content -LiteralPath (Join-Path $artifactDirectory 'arguments.log') -Raw | ConvertFrom-Json
if((ConvertTo-Json -InputObject $received -Compress) -ne $expected){throw 'Argumentos alterados'}
Invoke-Checked 'multiple-path' 'python3' @('-c','print("single execution")')
if((Get-Content -LiteralPath (Join-Path $artifactDirectory 'multiple-path.log') -Raw).Trim() -ne 'single execution'){throw 'Resolucao PATH nao executou uma vez'}
Invoke-Checked 'stderr-success' $python @('-c','import sys;sys.stderr.write("FAKE_CANARIO_PRIVADO");sys.exit(0)')
if((Get-Content -LiteralPath (Join-Path $artifactDirectory 'stderr-success.stderr.log') -Raw) -ne 'FAKE_CANARIO_PRIVADO'){throw 'Stderr nao capturado'}
$failed=$false
try {Invoke-Checked 'failure' $python @('-c','import sys;sys.stderr.write("FAKE_CANARIO_PRIVADO");sys.exit(9)')}catch{$failed=$true;if($_.ToString().Contains('FAKE_CANARIO_PRIVADO')){throw 'Canario vazou'}}
if(!$failed){throw 'Codigo nao-zero foi engolido'}
$failed=$false
try {Invoke-Checked 'missing-command' 'fake-candidate-command-that-does-not-exist' @()}catch{$failed=$true}
if(!$failed){throw 'Comando ausente nao falhou'}
# Guard configurável: controle bloqueado e configuração atual, sem operações.
$AppTargetReady=$false
$failed=$false
try { Assert-AppTargetReady } catch { $failed=$true }
if (!$failed) { throw 'Alvo pendente nao bloqueou' }
$AppTargetReady=$true
Assert-AppTargetReady
# #86, 11/11/2026: regressões do ensaio PS5.1, sem executar o runner completo.
if ((Quote-NativeArgument '-3') -ne '-3') { throw 'Seletor py deve permanecer sem aspas' }
$emptyLog=Join-Path $artifactDirectory 'empty.log'
[IO.File]::WriteAllText($emptyLog, '')
if ((Read-NativeLog $emptyLog) -ne '') { throw 'Log vazio nao normalizado' }
$failed=$false
try { Read-NativeLog (Join-Path $artifactDirectory 'missing.log') } catch { $failed=$true }
if (!$failed) { throw 'Arquivo ausente foi tratado como vazio' }
$utf8Before=$env:PYTHONUTF8
try {
    $env:PYTHONUTF8='0'
    Invoke-Checked 'utf8' $python @('-c','import sys; print(sys.flags.utf8_mode)')
    if ((Read-NativeLog (Join-Path $artifactDirectory 'utf8.log')).Trim() -ne '1') { throw 'Python filho sem UTF8' }
    if ($env:PYTHONUTF8 -ne '0') { throw 'Ambiente original alterado' }
} finally { $env:PYTHONUTF8=$utf8Before }
# O ramo público Final continua exigindo os três insumos antes do build.
$finalInputs=$ast.Find({param($n) $n -is [System.Management.Automation.Language.IfStatementAst] -and $n.Extent.Text.Contains('Final exige APK, referencia e aceite anterior')},$true)
if (!$finalInputs) { throw 'Guard Final ausente' }
$fakeReceipt=Join-Path $artifactDirectory 'accepted-fixture.json'
$fakeApk=Join-Path $artifactDirectory 'fixture.apk'
[IO.File]::WriteAllText($fakeReceipt, '{}')
[IO.File]::WriteAllText($fakeApk, 'FAKE_NOT_AN_APK')
$Phase='Final'
foreach ($missing in @('receipt','apk','certificate')) {
    $AcceptedRehearsalReceipt=$fakeReceipt
    $RehearsalApk=$fakeApk
    $ExpectedUploadSha256='0' * 64
    switch ($missing) {
        'receipt' { $AcceptedRehearsalReceipt=$null }
        'apk' { $RehearsalApk=$null }
        'certificate' { $ExpectedUploadSha256=$null }
    }
    $failed=$false
    try { . ([scriptblock]::Create($finalInputs.Extent.Text)) } catch { $failed=$true }
    if (!$failed) { throw 'Final aceitou insumo ausente' }
}
# Apenas valida presença/paths; conteúdo SHA/hash é validado nos testes Python.
$AcceptedRehearsalReceipt=$fakeReceipt
$RehearsalApk=$fakeApk
$ExpectedUploadSha256='0' * 64
. ([scriptblock]::Create($finalInputs.Extent.Text))
if ($resolvedAcceptance -ne $fakeReceipt -or $resolvedRehearsalApk -ne $fakeApk) { throw 'Paths do aceite nao preservados' }
# Executa somente o ramo de build de ensaio com fronteira nativa simulada.
$rehearsalBranch=$ast.Find({param($n) $n -is [System.Management.Automation.Language.IfStatementAst] -and $n.Extent.Text.Contains("'gradle-rehearsal'")},$true)
if(!$rehearsalBranch){throw 'Ramo de ensaio ausente'}
$script:recordedStages=@()
$script:recordedArguments=@()
function Invoke-Checked {param($Stage,$Executable,$CommandArguments) $script:recordedStages+=@($Stage);$script:recordedArguments+=@($CommandArguments)}
$Phase='Rehearsal';$buildCheckout=$artifactDirectory
$helperDirectory=$PSScriptRoot
. ([scriptblock]::Create($rehearsalBranch.Extent.Text.Replace('$PSScriptRoot','$helperDirectory')))
if(($script:recordedStages -join ',') -ne 'gradle-rehearsal,rehearsal-export'){throw 'Ensaio entrou em finalizacao'}
if($script:recordedArguments -contains 'bundleRelease'){throw 'Ensaio gerou AAB'}
Write-Output 'FIM testes PowerShell: sintaxe, argumentos, stderr0, exit9 e comando ausente conferidos; sem build/assinatura'

} finally {
    $env:PATH=$originalPath
    Remove-Item -LiteralPath $artifactDirectory -Recurse -Force
}
