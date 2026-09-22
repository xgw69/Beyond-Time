# Draws the microscope panel the way the game lays it out, with every fixed coordinate labelled,
# so the container background can be painted without guessing. The numbers come from
# MicroscopeMenu and MicroscopeScreen; docs/MICROSCOPE.md 4.4 has the same table as text.
#
# Usage:  pwsh -File tools/preview-gui.ps1
# Output: docs/img/microscope-gui.png

Add-Type -AssemblyName System.Drawing

$panelWidth = 256
$panelHeight = 248
$legendHeight = 46
$scale = 2

$canvasWidth = [math]::Max($panelWidth * $scale, 700)
$bitmap = New-Object System.Drawing.Bitmap(
    $canvasWidth, (($panelHeight + $legendHeight) * $scale), [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($bitmap)
$g.SmoothingMode = 'None'

$panel = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 198, 198, 198))
$recess = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 176, 176, 176))
$slot = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 139, 139, 139))
$line = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 139, 139, 139))
$grid = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 110, 160, 110))
$tagFont = New-Object System.Drawing.Font('Consolas', 6)
$tagBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 20, 80, 20))
$noteFont = New-Object System.Drawing.Font('Consolas', 9)
$noteBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 20, 20, 20))

function Add-Tag($g, $font, $brush, [string] $text, [int] $x, [int] $y) {
    $g.DrawString($text, $font, $brush, [single] $x, [single] $y)
}

$g.Clear([System.Drawing.Color]::White)
$g.ScaleTransform($scale, $scale)
$g.FillRectangle($panel, 0, 0, $panelWidth, $panelHeight)

# --- stage: the dish slot and the two lines of text beside it
$g.FillRectangle($recess, 6, 18, 110, 28)
$g.DrawRectangle($line, 6, 18, 110, 28)
$g.FillRectangle($slot, 10, 22, 16, 16)
Add-Tag $g $tagFont $tagBrush 'dish 10,22' 32 22
Add-Tag $g $tagFont $tagBrush 'info 34,24 / 34,36' 32 34

# --- separator
$g.DrawLine($line, 8, 48, 248, 48)
Add-Tag $g $tagFont $tagBrush 'separator y=48' 176 40

# --- report
$g.FillRectangle($recess, 6, 50, 244, 98)
$g.DrawRectangle($line, 6, 50, 244, 98)
Add-Tag $g $tagFont $tagBrush 'title 10,52' 10 51

for ($column = 0; $column -lt 3; $column++) {
    for ($row = 0; $row -lt 4; $row++) {
        $x = 10 + $column * 78
        $y = 66 + $row * 20
        $g.DrawRectangle($grid, $x, $y, 77, 19)
        $g.FillRectangle($slot, $x, $y + 2, 16, 16)
        Add-Tag $g $tagFont $noteBrush 'name' ($x + 19) ($y + 5)
        Add-Tag $g $tagFont $noteBrush ('#' + ($column * 4 + $row + 1)) ($x + 62) ($y + 5)
    }
}

# --- player inventory
Add-Tag $g $tagFont $tagBrush 'label 47,154' 47 143
for ($row = 0; $row -lt 3; $row++) {
    for ($column = 0; $column -lt 9; $column++) {
        $g.FillRectangle($slot, (47 + $column * 18), (166 + $row * 18), 16, 16)
    }
}

for ($column = 0; $column -lt 9; $column++) {
    $g.FillRectangle($slot, (47 + $column * 18), 224, 16, 16)
}

# --- legend
$notes = @(
    'panel 256x248, background PNG 256x256 (panel in the top left corner)',
    'stage recess 6,18  110x28      report recess 6,50  244x98',
    'report cell 10,66  cell 78x20  3 columns x 4 rows   icon 16x16   column-major',
    'inventory label 47,154   slots 47,166 (3x9)   hotbar 47,224'
)

for ($i = 0; $i -lt $notes.Count; $i++) {
    # Outside the scaled pose, so the legend is readable instead of doubled.
    $g.ResetTransform()
    $g.DrawString($notes[$i], $noteFont, $noteBrush, [single] ($i * 0 + 6), [single] (($panelHeight + 4) * $scale + $i * 12))
    $g.ScaleTransform($scale, $scale)
}

$g.Dispose()

$out = Join-Path $PSScriptRoot '..\docs\img\microscope-gui.png'
$bitmap.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()

Write-Host "Wrote $out"
