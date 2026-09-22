# Lays every 32x32 microbe icon out in one image, so placeholder art can be reviewed without
# opening the game. Same grid the microscope screen uses: 6 columns.
#
# Usage:  pwsh -File tools/preview-textures.ps1
# Output: docs/img/microbe-icons.png

Add-Type -AssemblyName System.Drawing

$root = Join-Path $PSScriptRoot '..\src\main\resources\assets\beyondtime\textures\microbe'
$out = Join-Path $PSScriptRoot '..\docs\img\microbe-icons.png'

$ids = @(
    'ancient_water', 'stone', 'fae', 'fire', 'crimson_mold', 'ender',
    'copper', 'wood_mold', 'glimmer', 'bizarre', 'false_ancient_water', 'unknown'
)

$columns = 6
$cell = 32
$rows = [math]::Ceiling($ids.Count / $columns)

$sheet = New-Object System.Drawing.Bitmap(
    ($columns * $cell), ($rows * $cell), [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($sheet)
$g.Clear([System.Drawing.Color]::FromArgb(255, 70, 70, 78))

for ($i = 0; $i -lt $ids.Count; $i++) {
    $path = Join-Path $root "$($ids[$i]).png"
    if (-not (Test-Path $path)) {
        Write-Warning "missing $path"
        continue
    }

    $icon = [System.Drawing.Image]::FromFile((Resolve-Path $path))
    $g.DrawImage($icon, (($i % $columns) * $cell), ([math]::Floor($i / $columns) * $cell), $cell, $cell)
    $icon.Dispose()
}

$g.Dispose()
$sheet.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$sheet.Dispose()

Write-Host "Wrote $out"
