# Exportacao deterministica de miniaturas; preserva os arquivos originais.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$terraRoot = Split-Path -Parent $PSScriptRoot
$terraRows = Get-Content -LiteralPath (Join-Path $terraRoot 'docs/loja/catalogo-2026-10-10/ARTES.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$terraOutput = Join-Path $terraRoot 'android-app/app/src/main/assets/atmosfera/miniaturas'
New-Item -ItemType Directory -Path $terraOutput -Force | Out-Null
foreach ($terraArt in $terraRows.artes) {
    if ($terraArt.tipo -eq 'excluida' -or $terraArt.status_local -ne 'arquivos_presentes_nao_testada') { continue }
    $terraImage = [System.Drawing.Image]::FromFile((Join-Path $terraRoot $terraArt.fundo_caminho))
    $terraBitmap = $null; $terraGraphics = $null
    try {
        $terraWidth = [Math]::Min(240, $terraImage.Width)
        $terraHeight = [int][Math]::Round($terraImage.Height * $terraWidth / $terraImage.Width)
        $terraBitmap = New-Object System.Drawing.Bitmap($terraWidth, $terraHeight)
        $terraGraphics = [System.Drawing.Graphics]::FromImage($terraBitmap)
        $terraGraphics.InterpolationMode = if ($terraArt.variante_id -eq 'pixel') { [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor } else { [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic }
        $terraGraphics.DrawImage($terraImage, 0, 0, $terraWidth, $terraHeight)
        $terraPath = Join-Path $terraOutput ($terraArt.cenario_id + '__' + $terraArt.variante_id + '.jpg')
        $terraCodec = [System.Drawing.Imaging.ImageCodecInfo]::GetImageEncoders() | Where-Object { $_.MimeType -eq 'image/jpeg' }
        $terraParams = New-Object System.Drawing.Imaging.EncoderParameters(1)
        try {
            $terraParams.Param[0] = New-Object System.Drawing.Imaging.EncoderParameter([System.Drawing.Imaging.Encoder]::Quality, [long]85)
            $terraBitmap.Save($terraPath, $terraCodec, $terraParams)
        } finally { $terraParams.Dispose() }
    } finally {
        if ($terraGraphics) { $terraGraphics.Dispose() }
        if ($terraBitmap) { $terraBitmap.Dispose() }
        $terraImage.Dispose()
    }
}
Write-Output 'Miniaturas exportadas; nenhuma arte aprovada ou publicada.'
