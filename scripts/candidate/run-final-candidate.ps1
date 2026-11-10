#requires -Version 5.1
# #86: executar no Windows existente. Não envia nada nem altera chaves/políticas.
[CmdletBinding()]
param(
    [ValidateSet('Rehearsal','Final')][string]$Phase = 'Rehearsal',
    [string]$AcceptedRehearsalReceipt,
    [string]$RehearsalApk,
    [string]$AdbExecutable = 'adb',
    [Parameter(Mandatory=$true)][string]$Repository,
    [Parameter(Mandatory=$true)][string]$BundletoolJar,
    [Parameter(Mandatory=$true)][string]$ExistingProperties,
    [ValidatePattern('^[a-fA-F0-9:]{64,95}$')][string]$ExpectedUploadSha256,
    [ValidateSet('previously_confirmed_upload_certificate','previously_accepted_aab')][string]$CertificateReference = 'previously_confirmed_upload_certificate'
)
$ExpectedCommit = '6ccd02c09b527bd45cdc44f8929636864e1fd179'
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$initialLocation = Get-Location
$script:candidateStage = 'preflight'
$temporaryProperties = $null
$temporaryLocalProperties = $null
function Quote-NativeArgument {
    param([string]$Value)
    $escaped = [regex]::Replace($Value, '(\\*)"', { param($m) ('\' * ($m.Groups[1].Value.Length * 2 + 1)) + '"' })
    $escaped = [regex]::Replace($escaped, '(\\+)$', '$1$1')
    return '"' + $escaped + '"'
}
function Invoke-Native {
    param([string]$Stage, [string]$Executable, [string[]]$CommandArguments)
    $script:candidateStage = $Stage
    $resolvedExecutable = @(Get-Command $Executable -CommandType Application -ErrorAction Stop)[0].Source
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.WorkingDirectory = (Get-Location).Path
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $quoted = @($CommandArguments | ForEach-Object { Quote-NativeArgument $_ }) -join ' '
    if ($resolvedExecutable.EndsWith('.bat', [System.StringComparison]::OrdinalIgnoreCase)) {
        # cmd expande % mesmo entre aspas; não executar entrada ambígua.
        foreach ($value in (@($resolvedExecutable) + $CommandArguments)) {
            if ($value -match '[%"\r\n]') { throw 'Argumento batch nao suportado' }
        }
        $startInfo.FileName = $env:ComSpec
        $startInfo.Arguments = '/d /v:off /s /c "' + (Quote-NativeArgument $resolvedExecutable) + ' ' + $quoted + '"'
    } else {
        $startInfo.FileName = $resolvedExecutable
        $startInfo.Arguments = $quoted
    }
    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo
    if (!$process.Start()) { throw 'Processo nao iniciou' }
    # Leitura concorrente impede deadlock; stderr nativo não vira exceção de PS5.
    $stdoutTask = $process.StandardOutput.ReadToEndAsync()
    $stderrTask = $process.StandardError.ReadToEndAsync()
    $process.WaitForExit()
    $exitStatus = $process.ExitCode
    $privateLog = Join-Path $artifactDirectory ($Stage + '.log')
    [System.IO.File]::WriteAllText($privateLog, $stdoutTask.Result, (New-Object System.Text.UTF8Encoding($false)))
    [System.IO.File]::WriteAllText((Join-Path $artifactDirectory ($Stage + '.stderr.log')), $stderrTask.Result, (New-Object System.Text.UTF8Encoding($false)))
    $process.Dispose()
    return $exitStatus
}
function Invoke-Checked {
    param([string]$Stage, [string]$Executable, [string[]]$CommandArguments)
    $exitStatus = Invoke-Native $Stage $Executable $CommandArguments
    if ($exitStatus -ne 0) { throw 'Etapa falhou; classificar log privado antes de seguir' }
}
try {
    if ([Environment]::OSVersion.Platform -ne [PlatformID]::Win32NT) { throw 'Execucao real somente no Windows autorizado' }
    $repoRoot = (Resolve-Path -LiteralPath $Repository).Path
    $resolvedBundletool = (Resolve-Path -LiteralPath $BundletoolJar).Path
    $resolvedProperties = (Resolve-Path -LiteralPath $ExistingProperties).Path
    $resolvedAcceptance = $null
    if ($Phase -eq 'Final') {
        if (!$AcceptedRehearsalReceipt -or !$ExpectedUploadSha256 -or !$RehearsalApk) { throw 'Final exige APK, referencia e aceite anterior' }
        $resolvedAcceptance = (Resolve-Path -LiteralPath $AcceptedRehearsalReceipt).Path
        $resolvedRehearsalApk = (Resolve-Path -LiteralPath $RehearsalApk).Path
    }
    $stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    $artifactRelative = 'local/private/candidate-final-' + $ExpectedCommit.Substring(0,12) + '-' + $stamp
    $artifactDirectory = Join-Path $repoRoot $artifactRelative
    if (Test-Path -LiteralPath $artifactDirectory) { throw 'Diretorio ja existe' }
    New-Item -ItemType Directory -Path $artifactDirectory | Out-Null
    Set-Location -LiteralPath $repoRoot
    Invoke-Checked 'git-fetch' 'git' @('fetch','origin','main')
    Invoke-Checked 'git-commit' 'git' @('rev-parse',('--verify'),($ExpectedCommit + '^{commit}'))
    if ((Get-Content -LiteralPath (Join-Path $artifactDirectory 'git-commit.log') -Raw).Trim() -ne $ExpectedCommit) { throw 'Commit indisponivel' }
    $buildCheckout = Join-Path $repoRoot ('local/worktrees/candidate-' + $ExpectedCommit.Substring(0,12) + '-' + $stamp)
    if (Test-Path -LiteralPath $buildCheckout) { throw 'Checkout ja existe; preservar trabalho' }
    Invoke-Checked 'worktree-create' 'git' @('worktree','add','--detach',$buildCheckout,$ExpectedCommit)
    Set-Location -LiteralPath $buildCheckout
    Invoke-Checked 'git-head' 'git' @('rev-parse','HEAD')
    if ((Get-Content -LiteralPath (Join-Path $artifactDirectory 'git-head.log') -Raw).Trim() -ne $ExpectedCommit) { throw 'SHA incorreto' }
    Invoke-Checked 'git-status' 'git' @('status','--porcelain')
    if ((Get-Content -LiteralPath (Join-Path $artifactDirectory 'git-status.log') -Raw).Trim()) { throw 'Checkout deve estar limpo' }
    Invoke-Checked 'java-version' 'java' @('-version')
    $javaVersion = (Get-Content -LiteralPath (Join-Path $artifactDirectory 'java-version.log') -Raw) + (Get-Content -LiteralPath (Join-Path $artifactDirectory 'java-version.stderr.log') -Raw)
    if ($javaVersion -notmatch 'version "17[.\"]') { throw 'Java17 obrigatorio' }
    Invoke-Checked 'python-version' 'py' @('-3','-c','import sys; assert sys.version_info >= (3,11); print(sys.version.split()[0])')
    if ($Phase -eq 'Final') {
        Invoke-Checked 'rehearsal-acceptance' 'py' @('-3',(Join-Path $PSScriptRoot 'rehearsal.py'),'--acceptance',$resolvedAcceptance,'--apk',$resolvedRehearsalApk)
    } else {
        Invoke-Checked 'devices' $AdbExecutable @('devices')
        Invoke-Checked 'device-check' 'py' @('-3',(Join-Path $PSScriptRoot 'rehearsal.py'),'--check-device','--output',$artifactDirectory)
    }
    Invoke-Checked 'bundletool-version' 'java' @('-jar',$resolvedBundletool,'version')
    $publicTools = @{
        powershell = $PSVersionTable.PSVersion.ToString()
        java = ([regex]::Match($javaVersion, 'version "(17[0-9A-Za-z.+_-]*)"')).Groups[1].Value
        python = (Get-Content -LiteralPath (Join-Path $artifactDirectory 'python-version.log') -Raw).Trim()
        bundletool = (Get-Content -LiteralPath (Join-Path $artifactDirectory 'bundletool-version.log') -Raw).Trim()
    }
    $publicTools | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $artifactDirectory 'tools-public.json') -Encoding UTF8
    $propertiesDestination = Join-Path $buildCheckout 'android-app/keystore.properties'
    $sdkDestination = Join-Path $buildCheckout 'android-app/local.properties'
    if ((Test-Path -LiteralPath $propertiesDestination) -or (Test-Path -LiteralPath $sdkDestination)) { throw 'Destino privado ja existe' }
    $temporaryProperties = $propertiesDestination
    $temporaryLocalProperties = $sdkDestination
    Invoke-Checked 'signing-config' 'java' @((Join-Path $PSScriptRoot 'PrepareSigning.java'),$resolvedProperties,$temporaryProperties,(Join-Path $repoRoot 'android-app/local.properties'),$temporaryLocalProperties)
    Invoke-Checked 'composition' 'py' @('-3','-B','scripts/candidate_catalog.py')
    Invoke-Checked 'composition-tests' 'py' @('-3','-B','-m','unittest','discover','-s','scripts','-p','test_candidate_catalog.py','-v')
    Invoke-Checked 'legal-tests' 'py' @('-3','-B','-m','unittest','discover','-s','scripts','-p','test_documentos_legais.py','-v')
    Invoke-Checked 'hook-tests' 'py' @('-3','-B','-m','unittest','discover','-s','scripts','-p','test_disparar_site_legal.py','-v')
    Invoke-Checked 'r2-tests' 'py' @('-3','-B','-m','unittest','discover','-s','tools/r2','-p','test_*.py','-v')
    Invoke-Checked 'play-tests' 'py' @('-3','-B','scripts/run_play_regressions.py')
    Invoke-Checked 'legal-check' 'py' @('-3','scripts/sincronizar_documentos_legais.py','--check')
    Invoke-Checked 'whitespace' 'git' @('diff','--check')
    Set-Location -LiteralPath (Join-Path $buildCheckout 'android-app')
    if ($Phase -eq 'Rehearsal') {
        Invoke-Checked 'gradle-rehearsal' '.\gradlew.bat' @('testDebugUnitTest','lintDebug','assembleDebug','-PterraAcervoUrl=https://acervo.terrawallpaper.com.br/')
        $apk = Join-Path $buildCheckout 'android-app/app/build/outputs/apk/debug/app-debug.apk'
        Invoke-Checked 'rehearsal-export' 'py' @('-3',(Join-Path $PSScriptRoot 'rehearsal.py'),'--root',$buildCheckout,'--apk',$apk,'--output',$artifactDirectory)
    } else {
    Invoke-Checked 'gradle-final' '.\gradlew.bat' @('clean','testDebugUnitTest','lintDebug','assembleDebug','assembleRelease','bundleRelease','-PterraAcervoUrl=https://acervo.terrawallpaper.com.br/')
    $aab = Join-Path $buildCheckout 'android-app/app/build/outputs/bundle/release/app-release.aab'
    Invoke-Checked 'bundle-validate' 'java' @('-jar',$resolvedBundletool,'validate',('--bundle=' + $aab))
    Invoke-Checked 'manifest' 'java' @('-jar',$resolvedBundletool,'dump','manifest',('--bundle=' + $aab),'--module=base')
    Invoke-Checked 'audit-existing' 'py' @('-3',(Join-Path $buildCheckout 'scripts/auditar_aab_play.py'),$aab,'--bundletool',$resolvedBundletool,'--maior-version-code','7','--saida',(Join-Path $artifactDirectory 'audit-existing.json'))
    # Coleta diagnósticos antes de classificar strict: não perder hash/certificado por autoassinado.
    Invoke-Checked 'upload-certificate' 'keytool' @('-printcert','-rfc','-jarfile',$aab)
    Invoke-Checked 'signature-integrity' 'java' @((Join-Path $PSScriptRoot 'VerifySignature.java'),$aab,$ExpectedUploadSha256)
    $strictExit = Invoke-Native 'signature-strict' 'jarsigner' @('-J-Duser.language=en','-J-Duser.country=US','-verify','-verbose','-certs','-strict',$aab)
    @{ exit_code = $strictExit; certificate_reference = $CertificateReference } | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $artifactDirectory 'strict-result.json') -Encoding UTF8
    Invoke-Checked 'audit-final' 'py' @('-3',(Join-Path $PSScriptRoot 'verify_final_candidate.py'),'--root',$buildCheckout,'--aab',$aab,'--commit',$ExpectedCommit,'--output',$artifactDirectory,'--expected-upload-sha256',$ExpectedUploadSha256)
    }
    Set-Location -LiteralPath $buildCheckout
    Invoke-Checked 'git-status-final' 'git' @('status','--porcelain')
    if ((Get-Content -LiteralPath (Join-Path $artifactDirectory 'git-status-final.log') -Raw).Trim()) { throw 'Estado Git final divergente' }
} catch {
    Write-Output ('FALHA candidato: etapa ' + $script:candidateStage + '; nao compartilhar logs/configuracao privados; devolver somente esta linha')
    exit 1
} finally {
    # Remove somente a cópia temporária criada neste checkout; nunca a chave/original.
    if ($temporaryProperties -and (Test-Path -LiteralPath $temporaryProperties)) {
        try { Remove-Item -LiteralPath $temporaryProperties -ErrorAction Stop }
        catch { Write-Output 'FALHA candidato: limpeza da configuracao temporaria; nao compartilhar arquivo privado'; exit 1 }
    }
    if ($temporaryLocalProperties -and (Test-Path -LiteralPath $temporaryLocalProperties)) {
        try { Remove-Item -LiteralPath $temporaryLocalProperties -ErrorAction Stop }
        catch { Write-Output 'FALHA candidato: limpeza do caminho SDK temporario'; exit 1 }
    }
    Set-Location $initialLocation
}

if ($Phase -eq 'Rehearsal') {
    Write-Output 'FIM preparo ensaio: APK gerado; execucao/capturas/aceite ainda pendentes; sem AAB/instalacao'
    Write-Output ('Pacote do ensaio: ' + $artifactRelative + '/rehearsal-package.zip; seguir README antes de instalar')
} else {
    Write-Output 'FIM candidato: geracao e auditorias locais aprovadas; sem upload/instalacao'
    Write-Output ('Devolver somente: ' + $artifactRelative + '/candidate-receipt.zip; nao enviar a pasta inteira')
    Get-Content -LiteralPath (Join-Path $artifactDirectory 'provenance-final.json') -Raw | Write-Output
}
