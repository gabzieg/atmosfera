param(
    [Parameter(Mandatory = $true)][string]$Dist,
    [Parameter(Mandatory = $true)][string]$Output,
    [string]$Python = 'python',
    [string]$CredentialFile = (Join-Path $env:LOCALAPPDATA 'Terra/r2-upload.dpapi'),
    [switch]$DryRun
)
$ErrorActionPreference = 'Stop'
$taskArguments = @((Join-Path $PSScriptRoot 'publicar_terra_r2.py'), '--dist', $Dist, '--output', $Output)
if ($DryRun) {
    & $Python @taskArguments --dry-run
    if ($LASTEXITCODE -ne 0) { throw 'Validação do acervo falhou.' }
    return
}
$taskFields = @('R2_ACCOUNT_ID', 'R2_BUCKET', 'R2_ACCESS_KEY_ID', 'R2_SECRET_ACCESS_KEY')
$taskPrevious = @{}
foreach ($taskField in $taskFields) {
    $taskPrevious[$taskField] = [Environment]::GetEnvironmentVariable($taskField, 'Process')
}
$taskSecure = (Get-Content -LiteralPath $CredentialFile -Raw).Trim() | ConvertTo-SecureString
$taskPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($taskSecure)
try {
    $taskConfig = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($taskPointer) | ConvertFrom-Json
    foreach ($taskField in $taskFields) {
        if ([string]::IsNullOrWhiteSpace($taskConfig.$taskField)) { throw "Configuração incompleta: $taskField" }
        [Environment]::SetEnvironmentVariable($taskField, $taskConfig.$taskField, 'Process')
    }
    & $Python @taskArguments
    if ($LASTEXITCODE -ne 0) { throw 'Publicação interrompida; confira a saída acima.' }
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($taskPointer)
    foreach ($taskField in $taskFields) {
        [Environment]::SetEnvironmentVariable($taskField, $taskPrevious[$taskField], 'Process')
    }
    $taskConfig = $null
}
