# Generates the placeholder textures that the mod ships with.
#
# Every texture here is drawn by this script, and every one of them is meant to be replaced by the
# project owner. Run it again only if you deleted a placeholder and want it back; it overwrites.
#
# Sizes are fixed by docs/MICROSCOPE.md:
#   block/item textures  32x32, may use transparency
#   container background 256x256, with the visible 176x166 panel in the top left corner
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

# Draws one 18x18 slot well, the way vanilla dialogue slots look.
function Add-Slot($g, [int] $x, [int] $y) {
    $g.FillRectangle((New-Brush '#8B8B8B'), $x, $y, 16, 16)
    $g.DrawLine((New-Pen '#373737'), $x, $y, ($x + 15), $y)
    $g.DrawLine((New-Pen '#373737'), $x, $y, $x, ($y + 15))
    $g.DrawLine((New-Pen '#FFFFFF'), $x, ($y + 15), ($x + 15), ($y + 15))
    $g.DrawLine((New-Pen '#FFFFFF'), ($x + 15), $y, ($x + 15), ($y + 15))
}

# --- block/microscope.png: the body of the instrument, a 32x32 brass-and-iron panel ---------------
$body = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($body)
$g.SmoothingMode = 'None'
$g.FillRectangle((New-Brush '#4A4A56'), 0, 0, 32, 32)
$g.FillRectangle((New-Brush '#55555F'), 1, 1, 30, 30)
$g.FillRectangle((New-Brush '#3C3C46'), 0, 0, 32, 2)
$g.FillRectangle((New-Brush '#2E2E36'), 0, 30, 32, 2)
$g.FillRectangle((New-Brush '#C9A227'), 4, 12, 24, 6)     # brass band across the middle
$g.FillRectangle((New-Brush '#A8871C'), 4, 16, 24, 2)
$g.FillRectangle((New-Brush '#E0C25A'), 4, 12, 24, 1)
$g.FillRectangle((New-Brush '#6A6A78'), 8, 4, 2, 6)       # a couple of screws
$g.FillRectangle((New-Brush '#6A6A78'), 22, 22, 2, 6)
$g.Dispose()
$body.Save((Join-Path $root 'block\microscope.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$body.Dispose()

# --- block/microscope_glass.png: lens glass, 32x32 with a transparent border ----------------------
$glass = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($glass)
$g.SmoothingMode = 'None'
$g.FillRectangle((New-Brush '#9FE8F5' 230), 0, 0, 32, 32)
$g.FillRectangle((New-Brush '#D6F6FC' 235), 4, 4, 24, 10)
$g.FillRectangle((New-Brush '#6FB6C6' 235), 0, 0, 32, 2)
$g.FillRectangle((New-Brush '#6FB6C6' 235), 0, 30, 32, 2)
$g.FillRectangle((New-Brush '#6FB6C6' 235), 0, 0, 2, 32)
$g.FillRectangle((New-Brush '#6FB6C6' 235), 30, 0, 2, 32)
$g.Dispose()
$glass.Save((Join-Path $root 'block\microscope_glass.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$glass.Dispose()

# --- item/petri_dish.png: a transparent 32x32 dish ------------------------------------------------
$dish = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($dish)
$g.SmoothingMode = 'AntiAlias'
$g.FillEllipse((New-Brush '#D8F3DC' 200), 4, 6, 24, 20)
$g.DrawEllipse((New-Pen '#BEE9F2' 3), 4, 6, 24, 20)
$g.DrawEllipse((New-Pen '#7FC8D8'), 8, 9, 16, 14)
$g.Dispose()
$dish.Save((Join-Path $root 'item\petri_dish.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$dish.Dispose()

# --- gui/microscope.png: container background -----------------------------------------------------
# 256x256, like every vanilla container background, with the visible panel in the top left corner.
# The slot coordinates here must match MicroscopeMenu.DISH_SLOT_X/_Y and INVENTORY_X/_Y.
$gui = New-Image 256 256
$g = [System.Drawing.Graphics]::FromImage($gui)
$g.SmoothingMode = 'None'
$g.FillRectangle((New-Brush '#C6C6C6'), 0, 0, 176, 166)
$g.DrawRectangle((New-Pen '#FFFFFF'), 0, 0, 175, 165)
$g.DrawRectangle((New-Pen '#555555'), 1, 1, 173, 163)

# sample report area, matching MicroscopeScreen.REPORT_X / _Y
$g.FillRectangle((New-Brush '#B0B0B0'), 48, 18, 120, 48)
$g.DrawRectangle((New-Pen '#8B8B8B'), 48, 18, 120, 48)

# dish slot, matching MicroscopeMenu.DISH_SLOT_X / _Y
Add-Slot $g 26 21

# player inventory, matching MicroscopeMenu.INVENTORY_X / _Y
for ($row = 0; $row -lt 3; $row++) {
    for ($col = 0; $col -lt 9; $col++) {
        Add-Slot $g (8 + $col * 18) (84 + $row * 18)
    }
}

for ($col = 0; $col -lt 9; $col++) {
    Add-Slot $g (8 + $col * 18) 142
}

$g.Dispose()
$gui.Save((Join-Path $root 'gui\microscope.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$gui.Dispose()

Write-Host "Placeholder textures written to $root"
