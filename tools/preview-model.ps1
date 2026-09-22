# Draws orthographic front / side / top views of the microscope model variants so the shapes can be
# compared without launching the game. Only understands what the microscope models use: boxes whose
# north face names a texture variable; variables containing "lens" or "glass" are drawn as glass.
#
# Usage:  pwsh -File tools/preview-model.ps1
# Output: docs/img/microscope-shapes.png

Add-Type -AssemblyName System.Drawing

$scale = 12                      # pixels per model unit
$unit = 16                       # model units per block face
$view = $unit * $scale           # 192
$gap = 24
$margin = 12
$labelHeight = 18

$bodyColor = [System.Drawing.ColorTranslator]::FromHtml('#C9A227')
$glassColor = [System.Drawing.ColorTranslator]::FromHtml('#7FC8D8')
$outlineColor = [System.Drawing.ColorTranslator]::FromHtml('#2B2B33')
$gridColor = [System.Drawing.Color]::FromArgb(60, 0, 0, 0)
$frameColor = [System.Drawing.ColorTranslator]::FromHtml('#888888')

$variants = @(
    @{ Title = 'A  desk (in use)'; File = 'a_desk_per_part.json' },
    @{ Title = 'B  tower';         File = 'b_tower.json' },
    @{ Title = 'C  low bench';     File = 'c_low_bench.json' }
)

$viewNames = @('front', 'side (west, north left)', 'top (north up)')

$variantsDir = Join-Path $PSScriptRoot 'microscope-variants'
$outDir = Join-Path $PSScriptRoot '..\docs\img'
$null = New-Item -ItemType Directory -Force -Path $outDir

$headerHeight = 20
$width = $margin * 2 + $view * 3 + $gap * 2
$height = $margin * 2 + $headerHeight + ($labelHeight + $view) * $variants.Count + $gap * ($variants.Count - 1)

$canvas = New-Object System.Drawing.Bitmap($width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($canvas)
$g.SmoothingMode = 'None'
$g.Clear([System.Drawing.ColorTranslator]::FromHtml('#F5F5F0'))
$font = New-Object System.Drawing.Font('Segoe UI', 9)
$textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#333333'))

for ($v = 0; $v -lt $variants.Count; $v++) {
    $model = Get-Content (Join-Path $variantsDir $variants[$v].File) -Raw | ConvertFrom-Json
    $rowTop = $margin + $headerHeight + $v * ($labelHeight + $view + $gap)

    for ($viewIndex = 0; $viewIndex -lt 3; $viewIndex++) {
        $left = $margin + $viewIndex * ($view + $gap)
        $top = $rowTop + $labelHeight

        if ($v -eq 0) {
            $g.DrawString($viewNames[$viewIndex], $font, $textBrush, $left, $margin - 2)
        }

        $g.DrawString($variants[$v].Title, $font, $textBrush, $left, $rowTop - 2)

        # the block outline and a 4-unit grid
        $g.DrawRectangle((New-Object System.Drawing.Pen($frameColor)), $left, $top, $view, $view)
        for ($i = 4; $i -lt $unit; $i += 4) {
            $offset = [int]($i * $scale)
            $g.DrawLine((New-Object System.Drawing.Pen($gridColor)), ($left + $offset), $top, ($left + $offset), ($top + $view))
            $g.DrawLine((New-Object System.Drawing.Pen($gridColor)), $left, ($top + $offset), ($left + $view), ($top + $offset))
        }

        # order the boxes so that the ones nearest the camera are painted last
        $boxes = @()
        foreach ($element in $model.elements) {
            $fx = [int]$element.from[0]; $fy = [int]$element.from[1]; $fz = [int]$element.from[2]
            $tx = [int]$element.to[0];   $ty = [int]$element.to[1];   $tz = [int]$element.to[2]
            $variable = $element.faces.north.texture
            $colour = if ($variable -match 'lens|glass') { $glassColor } else { $bodyColor }

            if ($viewIndex -eq 0) {
                # looking from the north: horizontal = x, vertical = y, depth = z
                $boxes += [pscustomobject]@{ X = $fx; Y = ($unit - $ty); W = ($tx - $fx); H = ($ty - $fy); Depth = $tz; Colour = $colour }
            } elseif ($viewIndex -eq 1) {
                # looking from the west: horizontal = z, vertical = y, depth = x
                $boxes += [pscustomobject]@{ X = $fz; Y = ($unit - $ty); W = ($tz - $fz); H = ($ty - $fy); Depth = $fx; Colour = $colour }
            } else {
                # looking down: horizontal = x, vertical = z, depth = y
                $boxes += [pscustomobject]@{ X = $fx; Y = $fz; W = ($tx - $fx); H = ($tz - $fz); Depth = ($unit - $fy); Colour = $colour }
            }
        }

        $pen = New-Object System.Drawing.Pen($outlineColor)
        foreach ($box in ($boxes | Sort-Object -Property Depth -Descending)) {
            $rect = New-Object System.Drawing.Rectangle(
                ($left + [int]($box.X * $scale)), ($top + [int]($box.Y * $scale)),
                [int]($box.W * $scale), [int]($box.H * $scale))
            $g.FillRectangle((New-Object System.Drawing.SolidBrush($box.Colour)), $rect)
            $g.DrawRectangle($pen, $rect)
        }
    }
}

$g.Dispose()
$target = Join-Path $outDir 'microscope-shapes.png'
$canvas.Save($target, [System.Drawing.Imaging.ImageFormat]::Png)
$canvas.Dispose()
Write-Host "Wrote $target"
