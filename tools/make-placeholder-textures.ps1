# Generates the placeholder textures that the mod ships with.
#
# Every texture here is drawn by this script, and every one of them is meant to be replaced by the
# project owner. Run it again only if you deleted a placeholder and want it back; it overwrites.
#
# Usage:  pwsh -File tools/make-placeholder-textures.ps1

Add-Type -AssemblyName System.Drawing

$root = Join-Path $PSScriptRoot '..\src\main\resources\assets\beyondtime\textures'

function New-Image([int] $width, [int] $height) {
    return New-Object System.Drawing.Bitmap($width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function New-Brush([string] $hex, [int] $alpha = 255) {
    $color = [System.Drawing.ColorTranslator]::FromHtml($hex)
    return New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb($alpha, $color))
}

function New-Pen([string] $hex, [float] $width = 1, [int] $alpha = 255) {
    $color = [System.Drawing.ColorTranslator]::FromHtml($hex)
    return New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb($alpha, $color), $width)
}

# --- block/microscope.png: a full 32x32 cube face, dark body with a brass tube -------------------
$microscope = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($microscope)
$g.SmoothingMode = 'None'
$g.PixelOffsetMode = 'Half'
$body = New-Brush '#3F3F4A'
$bodyEdge = New-Pen '#2B2B33'
$brass = New-Brush '#C9A227'
$glass = New-Brush '#9FE8F5'
$g.FillRectangle($body, 0, 0, 32, 32)
$g.DrawLine($bodyEdge, 0, 31, 31, 31)
$g.DrawLine($bodyEdge, 31, 0, 31, 31)
$g.FillRectangle($brass, 12, 8, 8, 18)          # stand column
$g.FillRectangle($brass, 6, 24, 20, 4)          # base
$g.FillRectangle($brass, 8, 10, 12, 3)          # stage arm
$g.FillRectangle($glass, 14, 3, 4, 5)           # eyepiece
$g.FillRectangle($glass, 10, 16, 12, 3)         # stage glass
$g.Dispose()
$microscope.Save((Join-Path $root 'block\microscope.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$microscope.Dispose()

# --- item/petri_dish.png: a transparent 32x32 dish ---------------------------------------------
$dish = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($dish)
$g.SmoothingMode = 'AntiAlias'
$rim = New-Pen '#BEE9F2' 3
$agar = New-Brush '#D8F3DC' 200
$g.FillEllipse($agar, 4, 6, 24, 20)
$g.DrawEllipse($rim, 4, 6, 24, 20)
$g.DrawEllipse((New-Pen '#7FC8D8'), 8, 9, 16, 14)
$g.Dispose()
$dish.Save((Join-Path $root 'item\petri_dish.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$dish.Dispose()

# --- gui/microscope.png: 176x166 container background -------------------------------------------
$gui = New-Image 176 166
$g = [System.Drawing.Graphics]::FromImage($gui)
$g.SmoothingMode = 'None'
$panel = New-Brush '#C6C6C6'
$slotFill = New-Brush '#8B8B8B'
$slotDark = New-Pen '#373737'
$slotLight = New-Pen '#FFFFFF'
$g.FillRectangle($panel, 0, 0, 176, 166)
$g.DrawRectangle((New-Pen '#FFFFFF'), 0, 0, 175, 165)
$g.DrawRectangle((New-Pen '#555555'), 1, 1, 173, 163)

# dish slot, matching MicroscopeMenu.DISH_SLOT_X / _Y
$g.FillRectangle($slotFill, 26, 21, 16, 16)
$g.DrawLine($slotDark, 26, 21, 41, 21)
$g.DrawLine($slotDark, 26, 21, 26, 36)
$g.DrawLine($slotLight, 26, 36, 41, 36)
$g.DrawLine($slotLight, 41, 21, 41, 36)

# sample report area, matching MicroscopeScreen.REPORT_X / _Y
$g.FillRectangle((New-Brush '#B0B0B0'), 48, 18, 120, 48)
$g.DrawRectangle((New-Pen '#8B8B8B'), 48, 18, 120, 48)

# player inventory slots, matching MicroscopeMenu.INVENTORY_X / _Y
for ($row = 0; $row -lt 3; $row++) {
    for ($col = 0; $col -lt 9; $col++) {
        $x = 8 + $col * 18
        $y = 84 + $row * 18
        $g.FillRectangle($slotFill, $x, $y, 16, 16)
        $g.DrawLine($slotDark, $x, $y, ($x + 15), $y)
        $g.DrawLine($slotDark, $x, $y, $x, ($y + 15))
        $g.DrawLine($slotLight, $x, ($y + 15), ($x + 15), ($y + 15))
        $g.DrawLine($slotLight, ($x + 15), $y, ($x + 15), ($y + 15))
    }
}

for ($col = 0; $col -lt 9; $col++) {
    $x = 8 + $col * 18
    $y = 142
    $g.FillRectangle($slotFill, $x, $y, 16, 16)
    $g.DrawLine($slotDark, $x, $y, ($x + 15), $y)
    $g.DrawLine($slotDark, $x, $y, $x, ($y + 15))
    $g.DrawLine($slotLight, $x, ($y + 15), ($x + 15), ($y + 15))
    $g.DrawLine($slotLight, ($x + 15), $y, ($x + 15), ($y + 15))
}

$g.Dispose()
$gui.Save((Join-Path $root 'gui\microscope.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$gui.Dispose()

Write-Host "Placeholder textures written to $root"
