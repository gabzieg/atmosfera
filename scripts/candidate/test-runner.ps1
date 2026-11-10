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
try {
$python=(Get-Command python3 -CommandType Application).Source
$testArguments=@('espaco no caminho','aspas"internas','fim\','&naoexecutar','')
$expected=ConvertTo-Json -InputObject $testArguments -Compress
Invoke-Checked 'arguments' $python (@('-c','import sys,json;print(json.dumps(sys.argv[1:]))')+$testArguments)
$received=Get-Content -LiteralPath (Join-Path $artifactDirectory 'arguments.log') -Raw | ConvertFrom-Json
if((ConvertTo-Json -InputObject $received -Compress) -ne $expected){throw 'Argumentos alterados'}
Invoke-Checked 'stderr-success' $python @('-c','import sys;sys.stderr.write("FAKE_CANARIO_PRIVADO");sys.exit(0)')
if((Get-Content -LiteralPath (Join-Path $artifactDirectory 'stderr-success.stderr.log') -Raw) -ne 'FAKE_CANARIO_PRIVADO'){throw 'Stderr nao capturado'}
$failed=$false
try {Invoke-Checked 'failure' $python @('-c','import sys;sys.stderr.write("FAKE_CANARIO_PRIVADO");sys.exit(9)')}catch{$failed=$true;if($_.ToString().Contains('FAKE_CANARIO_PRIVADO')){throw 'Canario vazou'}}
if(!$failed){throw 'Codigo nao-zero foi engolido'}
$failed=$false
try {Invoke-Checked 'missing-command' 'fake-candidate-command-that-does-not-exist' @()}catch{$failed=$true}
if(!$failed){throw 'Comando ausente nao falhou'}
Write-Output 'FIM testes PowerShell: sintaxe, argumentos, stderr0, exit9 e comando ausente conferidos; sem build/assinatura'

} finally {
    Remove-Item -LiteralPath $artifactDirectory -Recurse -Force
}
