# Blows up block/microscope_atlas.png and labels every 8x8 cell with the uv range it covers, so the
# per-face variant (tools/microscope-variants/a_desk_per_face.json) can be painted without doing
# the arithmetic by hand. The same numbers are in docs/TEXTURES.md.
#
# Usage:  pwsh -File tools/preview-atlas.ps1
# Output: docs/img/microscope-atlas.png

Add-Type -AssemblyName System.Drawing

$atlasPath = Join-Path $PSScriptRoot '..\src\main\resources\assets\beyondtime\textures\block\microscope_atlas.png'
$outPath = Join-Path $PSScriptRoot '..\docs\img\microscope-atlas.png'

$zoom = 12
$cells = 4
$cellPixels = 8
$board = $cells * $cellPixels * $zoom      # 384
$margin = 20
$legendX = $margin + $board + 24
$lineHeight = 16

$canvasWidth = $legendX + 560
$canvasHeight = $margin * 2 + $board

$bitmap = New-Object System.Drawing.Bitmap($canvasWidth, $canvasHeight, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($bitmap)
$g.SmoothingMode = 'None'
$g.InterpolationMode = 'NearestNeighbor'
$g.PixelOffsetMode = 'Half'
$g.Clear([System.Drawing.ColorTranslator]::FromHtml('#F5F5F0'))

$atlas = [System.Drawing.Image]::FromFile((Resolve-Path $atlasPath))
$g.DrawImage($atlas, $margin, $margin, $board, $board)
$atlas.Dispose()

$cellOutline = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(160, 20, 20, 20))
$tagBack = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(200, 255, 255, 255))
$tagText = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 20, 20, 20))
$tagFont = New-Object System.Drawing.Font('Consolas', 8)
$legendFont = New-Object System.Drawing.Font('Consolas', 9)
$cjkFont = New-Object System.Drawing.Font('Microsoft YaHei', 9)
$legendBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#222222'))
$mutedBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#999999'))

$usage = @(
    'base side',    'base top',     'base bottom',     'stage top',
    'stage side',   'pillar front', 'pillar other',    'arm',
    'lens side',    'lens bottom',  'lens top',        'spare',
    'spare',        'spare',        'spare',           'spare'
)

for ($index = 0; $index -lt 16; $index++) {
    $column = $index % $cells
    $row = [math]::Floor($index / $cells)
    $x = $margin + $column * $cellPixels * $zoom
    $y = $margin + $row * $cellPixels * $zoom
    $g.DrawRectangle($cellOutline, $x, $y, ($cellPixels * $zoom), ($cellPixels * $zoom))

    # a tag in the corner of every cell, so a cell can be named out loud while painting
    $tag = "$($index):$($column * 4),$($row * 4)"
    $size = $g.MeasureString($tag, $tagFont)
    $g.FillRectangle($tagBack, $x + 2, $y + 2, $size.Width + 2, $size.Height)
    $g.DrawString($tag, $tagFont, $tagText, [single] ($x + 3), [single] ($y + 2))
}

$g.DrawString('cell : uv  ->  used for', $legendFont, $legendBrush, [single] $legendX, 24)

for ($index = 0; $index -lt 16; $index++) {
    $column = $index % $cells
    $row = [math]::Floor($index / $cells)
    $u0 = $column * 4
    $v0 = $row * 4
    $u1 = $u0 + 4
    $v1 = $v0 + 4
    $pixels = "$($u0 * 2),$($v0 * 2) - $($u1 * 2),$($v1 * 2) px"
    $line = "{0,2} : [{1,2},{2,2},{3,2},{4,2}]  {5}  {6}" -f $index, $u0, $v0, $u1, $v1, $pixels, $usage[$index]
    $brush = if ($usage[$index] -eq 'spare') { $mutedBrush } else { $legendBrush }
    $g.DrawString($line, $legendFont, $brush, [single] $legendX, [single] (44 + $index * $lineHeight))
}

$noteY = 44 + 16 * $lineHeight + 6
$g.DrawString('uv 是 0..16 空间，和图片像素无关：', $cjkFont, $legendBrush, [single] $legendX, [single] $noteY)
$g.DrawString('uv = 像素 / (图宽 / 16)      32x32 -> 除以 2      64x64 -> 除以 4', $legendFont, $legendBrush, [single] $legendX, [single] ($noteY + 18))

$g.Dispose()
$bitmap.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()

Write-Host "Wrote $outPath"
