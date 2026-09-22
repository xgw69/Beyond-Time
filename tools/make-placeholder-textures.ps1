# Generates the placeholder textures that the mod ships with.
#
# Every texture here is drawn by this script, and every one of them is meant to be replaced by the
# project owner. **Files that already exist are left alone** - the owner's art is never overwritten
# by accident. Pass -Force to redraw a file you want back.
#
# Sizes are fixed by docs/TEXTURES.md:
#   block/item/microbe textures  32x32, may use transparency
#   container background         256x256, with the visible 256x248 panel in the top left corner
#
# Usage:  pwsh -File tools/make-placeholder-textures.ps1           # only fill in what is missing
#         pwsh -File tools/make-placeholder-textures.ps1 -Force    # redraw everything

param([switch] $Force)

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

# Writes a texture unless it is already there, so a painted texture is never clobbered.
$skipped = New-Object 'System.Collections.Generic.List[string]'

function Save-Texture($image, [string] $path) {
    if ((Test-Path $path) -and -not $script:Force) {
        $script:skipped.Add($path)
        return
    }

    $image.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}

# Draws one 18x18 slot well, the way vanilla dialogue slots look.
function Add-Slot($g, [int] $x, [int] $y) {
    $g.FillRectangle((New-Brush '#8B8B8B'), $x, $y, 16, 16)
    $g.DrawLine((New-Pen '#373737'), $x, $y, ($x + 15), $y)
    $g.DrawLine((New-Pen '#373737'), $x, $y, $x, ($y + 15))
    $g.DrawLine((New-Pen '#FFFFFF'), $x, ($y + 15), ($x + 15), ($y + 15))
    $g.DrawLine((New-Pen '#FFFFFF'), ($x + 15), $y, ($x + 15), ($y + 15))
}

# Turns "16,3;29,16;16,29" into the PointF[] that FillPolygon wants.
function New-Polygon([string] $spec) {
    $points = New-Object 'System.Collections.Generic.List[System.Drawing.PointF]'
    foreach ($pair in $spec.Split(';')) {
        $xy = $pair.Split(',')
        $points.Add((New-Object System.Drawing.PointF([single] $xy[0], [single] $xy[1])))
    }

    return , $points.ToArray()
}

function Add-Polygon($g, [string] $spec, $brush, $pen) {
    $points = New-Polygon $spec
    $g.FillPolygon($brush, $points)
    $g.DrawPolygon($pen, $points)
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
Save-Texture $body (Join-Path $root 'block\microscope.png')
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
Save-Texture $glass (Join-Path $root 'block\microscope_glass.png')
$glass.Dispose()

# --- block/microscope_<part>.png: one 32x32 texture per part -------------------------------------
#
# Only used by tools/microscope-variants/a_desk_per_part.json. They are generated anyway so that
# copying that variant over the real model works straight away, without a missing-texture cube.

$parts = @(
    @{ file = 'microscope_base.png';   fill = '#4A4A56'; accent = '#6A6A78'; style = 'plate' },
    @{ file = 'microscope_stage.png';  fill = '#26262C'; accent = '#3A3A44'; style = 'stage' },
    @{ file = 'microscope_pillar.png'; fill = '#C9A227'; accent = '#7A6215'; style = 'ticks' },
    @{ file = 'microscope_arm.png';    fill = '#A8871C'; accent = '#6E5910'; style = 'bar' },
    @{ file = 'microscope_lens.png';   fill = '#7FC8D8'; accent = '#3E7F92'; style = 'rings' }
)

foreach ($part in $parts) {
    $image = New-Image 32 32
    $g = [System.Drawing.Graphics]::FromImage($image)
    $g.SmoothingMode = 'None'
    $g.FillRectangle((New-Brush $part.fill), 0, 0, 32, 32)
    $g.DrawRectangle((New-Pen $part.accent), 0, 0, 31, 31)

    switch ($part.style) {
        'plate' {
            foreach ($corner in @(@(4, 4), @(24, 4), @(4, 24), @(24, 24))) {
                $g.FillRectangle((New-Brush $part.accent), $corner[0], $corner[1], 4, 4)
            }
        }
        'stage' {
            $g.FillEllipse((New-Brush $part.accent), 8, 8, 16, 16)
            $g.DrawEllipse((New-Pen '#14141A' 2), 8, 8, 16, 16)
        }
        'ticks' {
            for ($y = 3; $y -lt 32; $y += 4) {
                $g.DrawLine((New-Pen $part.accent), 4, $y, 28, $y)
            }
        }
        'bar' {
            $g.FillRectangle((New-Brush $part.accent), 0, 12, 32, 8)
            $g.DrawLine((New-Pen '#E0C25A'), 0, 12, 32, 12)
        }
        'rings' {
            foreach ($y in @(6, 14, 22)) {
                $g.DrawLine((New-Pen $part.accent 2), 0, $y, 32, $y)
            }
        }
    }

    $g.Dispose()
    Save-Texture $image (Join-Path $root "block\$($part.file)")
    $image.Dispose()
}

# --- block/microscope_atlas.png: a 4x4 grid of 8x8 cells for the per-face variant ----------------
#
# Every cell is flat coloured with a darker border, so it is obvious which part of the texture a
# face is reading once the atlas is in use. The cell -> uv table is in docs/TEXTURES.md.
$cellSize = 8
$atlasCells = @(
    '#C9A227', '#B8912A', '#8A6C1B', '#2E2E36',
    '#A8871C', '#E0C25A', '#9A7C22', '#6A6A78',
    '#7FC8D8', '#4E8FA0', '#BEE9F2', '#55555F',
    '#3C3C46', '#4A4A56', '#5A5A66', '#6C6C7A'
)

$atlas = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($atlas)
$g.SmoothingMode = 'None'
for ($index = 0; $index -lt $atlasCells.Count; $index++) {
    $x = ($index % 4) * $cellSize
    $y = [math]::Floor($index / 4) * $cellSize
    $g.FillRectangle((New-Brush $atlasCells[$index]), $x, $y, $cellSize, $cellSize)
    $g.DrawRectangle((New-Pen '#14141A'), $x, $y, ($cellSize - 1), ($cellSize - 1))
}

$g.Dispose()
Save-Texture $atlas (Join-Path $root 'block\microscope_atlas.png')
$atlas.Dispose()

# --- item/petri_dish.png: a transparent 32x32 dish ------------------------------------------------
$dish = New-Image 32 32
$g = [System.Drawing.Graphics]::FromImage($dish)
$g.SmoothingMode = 'AntiAlias'
$g.FillEllipse((New-Brush '#D8F3DC' 200), 4, 6, 24, 20)
$g.DrawEllipse((New-Pen '#BEE9F2' 3), 4, 6, 24, 20)
$g.DrawEllipse((New-Pen '#7FC8D8'), 8, 9, 16, 14)
$g.Dispose()
Save-Texture $dish (Join-Path $root 'item\petri_dish.png')
$dish.Dispose()

# --- textures/microbe/*.png: one 32x32 icon per microbe -------------------------------------------
#
# These only ever appear as a small icon in the microscope screen, never in the world (rule R3).
# The shapes and colours below are placeholders that make the eleven microbes tell each other apart
# at a glance; the real art is the project owner's.
#
# false_ancient_water is drawn exactly like ancient_water on purpose: the whole point of 伪古水菌 is
# that early on it is indistinguishable, and the microscope folds it into 古水菌 anyway.
$microbes = @(
    @{ id = 'ancient_water';       fill = '#7FD1E8'; edge = '#2E7D9A'; shape = 'circle' },
    @{ id = 'stone';               fill = '#9E9E9E'; edge = '#4A4A4A'; shape = 'hexagon' },
    @{ id = 'fae';                 fill = '#E1BEE7'; edge = '#7B1FA2'; shape = 'star' },
    @{ id = 'fire';                fill = '#FFB74D'; edge = '#BF360C'; shape = 'flame' },
    @{ id = 'crimson_mold';        fill = '#C62828'; edge = '#5D0F0F'; shape = 'spiky' },
    @{ id = 'ender';               fill = '#B39DDB'; edge = '#311B92'; shape = 'ring' },
    @{ id = 'copper';              fill = '#E8A87C'; edge = '#8D5524'; shape = 'diamond' },
    @{ id = 'wood_mold';           fill = '#A1887F'; edge = '#4E342E'; shape = 'square' },
    @{ id = 'glimmer';             fill = '#FFF3B0'; edge = '#B58900'; shape = 'sparkle' },
    @{ id = 'bizarre';             fill = '#E040FB'; edge = '#4A148C'; shape = 'cross' },
    @{ id = 'false_ancient_water'; fill = '#7FD1E8'; edge = '#2E7D9A'; shape = 'circle' },
    @{ id = 'unknown';             fill = '#616161'; edge = '#212121'; shape = 'question' }
)

$microbeDir = Join-Path $root 'microbe'
New-Item -ItemType Directory -Force -Path $microbeDir | Out-Null

foreach ($microbe in $microbes) {
    $icon = New-Image 32 32
    $g = [System.Drawing.Graphics]::FromImage($icon)
    $g.SmoothingMode = 'AntiAlias'
    $fill = New-Brush $microbe.fill
    $edge = New-Pen $microbe.edge 2
    $thin = New-Pen $microbe.edge 1

    switch ($microbe.shape) {
        'circle' {
            $g.FillEllipse($fill, 4, 4, 24, 24)
            $g.DrawEllipse($edge, 4, 4, 24, 24)
            $g.DrawEllipse($thin, 11, 11, 10, 10)
        }
        'hexagon' {
            Add-Polygon $g '16,3;28,10;28,22;16,29;4,22;4,10' $fill $edge
            $g.DrawLine($thin, 10, 12, 22, 12)
            $g.DrawLine($thin, 10, 20, 22, 20)
        }
        'star' {
            Add-Polygon $g '16,2;20,12;30,16;20,20;16,30;12,20;2,16;12,12' $fill $edge
        }
        'flame' {
            Add-Polygon $g '16,2;27,28;5,28' $fill $edge
            Add-Polygon $g '16,12;22,26;10,26' (New-Brush '#FFF176') $thin
        }
        'spiky' {
            Add-Polygon $g '16,2;20,10;29,7;24,15;30,22;21,21;16,30;11,21;2,22;8,15;3,7;12,10' $fill $edge
        }
        'ring' {
            $g.DrawEllipse((New-Pen $microbe.edge 5), 6, 6, 20, 20)
            $g.FillEllipse((New-Brush '#00E676'), 13, 13, 6, 6)
        }
        'diamond' {
            Add-Polygon $g '16,2;30,16;16,30;2,16' $fill $edge
            $g.FillEllipse((New-Brush '#6D4C41'), 12, 12, 3, 3)
            $g.FillEllipse((New-Brush '#6D4C41'), 18, 18, 3, 3)
        }
        'square' {
            $g.FillRectangle($fill, 4, 4, 24, 24)
            $g.DrawRectangle($edge, 4, 4, 23, 23)
            $g.DrawLine($thin, 4, 12, 28, 12)
            $g.DrawLine($thin, 4, 20, 28, 20)
            $g.DrawLine($thin, 12, 4, 12, 28)
            $g.DrawLine($thin, 20, 4, 20, 28)
        }
        'sparkle' {
            Add-Polygon $g '16,4;18,14;28,16;18,18;16,28;14,18;4,16;14,14' $fill $thin
            $g.DrawLine($thin, 16, 0, 16, 6)
            $g.DrawLine($thin, 16, 26, 16, 32)
            $g.DrawLine($thin, 0, 16, 6, 16)
            $g.DrawLine($thin, 26, 16, 32, 16)
        }
        'cross' {
            Add-Polygon $g '13,2;19,2;19,13;30,13;30,19;19,19;19,30;13,30;13,19;2,19;2,13;13,13' $fill $edge
        }
        'question' {
            $g.DrawArc((New-Pen $microbe.fill 5), 8, 5, 16, 16, 160, 220)
            $g.DrawLine((New-Pen $microbe.fill 5), 16, 20, 16, 24)
            $g.FillEllipse($fill, 13, 26, 6, 6)
        }
    }

    $g.Dispose()
    Save-Texture $icon (Join-Path $microbeDir "$($microbe.id).png")
    $icon.Dispose()
}

# --- gui/microscope.png: container background -----------------------------------------------------
# 256x256, like every vanilla container background, with the visible panel in the top left corner.
# The slot coordinates here must match MicroscopeMenu.DISH_SLOT_X/_Y and INVENTORY_X/_Y, and the
# recesses must match MicroscopeScreen.LIST_X/_Y and the report grid.
$gui = New-Image 256 256
$g = [System.Drawing.Graphics]::FromImage($gui)
$g.SmoothingMode = 'None'
$g.FillRectangle((New-Brush '#C6C6C6'), 0, 0, 256, 248)
$g.DrawRectangle((New-Pen '#FFFFFF'), 0, 0, 255, 247)
$g.DrawRectangle((New-Pen '#555555'), 1, 1, 253, 245)

# the stage: the dish slot plus the two lines of source / plate count beside it
$g.FillRectangle((New-Brush '#B0B0B0'), 6, 18, 110, 28)
$g.DrawRectangle((New-Pen '#8B8B8B'), 6, 18, 110, 28)

# separator between the stage and the report
$g.DrawLine((New-Pen '#8B8B8B'), 8, 48, 248, 48)

# the report grid: 3 columns x 4 rows of 78x20 cells, starting at 10,66
$g.FillRectangle((New-Brush '#B0B0B0'), 6, 50, 244, 98)
$g.DrawRectangle((New-Pen '#8B8B8B'), 6, 50, 244, 98)

# dish slot, matching MicroscopeMenu.DISH_SLOT_X / _Y
Add-Slot $g 10 22

# player inventory, matching MicroscopeMenu.INVENTORY_X / _Y
for ($row = 0; $row -lt 3; $row++) {
    for ($col = 0; $col -lt 9; $col++) {
        Add-Slot $g (47 + $col * 18) (166 + $row * 18)
    }
}

for ($col = 0; $col -lt 9; $col++) {
    Add-Slot $g (47 + $col * 18) 224
}

$g.Dispose()
Save-Texture $gui (Join-Path $root 'gui\microscope.png')
$gui.Dispose()

Write-Host "Placeholder textures are in $root"
if ($skipped.Count -gt 0) {
    Write-Host "  kept $($skipped.Count) existing file(s) - pass -Force to overwrite them:"
    foreach ($path in $skipped) {
        Write-Host "    $path"
    }
}
