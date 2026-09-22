# Scaffolds the textures (and, for a full cube, the model) of a new block the way this project
# builds blocks: every drawable unit gets its own 32x32 file (rule R10 in docs/DESIGN.md).
#
#   full cube   -> one texture per face:  <block>_up/_down/_north/_south/_west/_east.png
#   other shape -> one texture per part:  <block>_<part>.png   (the geometry is written by hand)
#
# Existing files are never overwritten unless -Force is passed, so painted art is safe.
#
# Usage:
#   pwsh -File tools/new-block.ps1 -Block copper_centrifuge
#   pwsh -File tools/new-block.ps1 -Block cloning_vat -Units base,tank,panel,pipe
#
# See docs/TEXTURES.md section 10 for what to do after this script has run.

param(
    [Parameter(Mandatory = $true)][string] $Block,
    [string[]] $Units,
    [switch] $Force
)

Add-Type -AssemblyName System.Drawing

$namespaces = 'beyondtime'

if ($Block -notmatch '^[a-z0-9_]+$') {
    throw "Block name must be lower_snake_case, got '$Block'"
}

$faces = @('up', 'down', 'north', 'south', 'west', 'east')

# "pwsh -File" hands -Units over as one comma-joined string, while an interactive shell binds a real
# array, so accept both spellings.
$unitList = @($Units) | ForEach-Object { $_ -split ',' } | ForEach-Object { $_.Trim() } | Where-Object { $_ }

$isCube = -not $unitList
if ($isCube) {
    $unitList = $faces
}

foreach ($unit in $unitList) {
    if ($unit -notmatch '^[a-z0-9_]+$') {
        throw "Unit name must be lower_snake_case, got '$unit'"
    }
}

# One hue per unit, so the placeholders can be told apart in game while they are still placeholders.
$palette = @(
    '#8FA98F', '#6F7F92', '#A8926F', '#6F8F8F', '#9C7A9C', '#8C8C8C',
    '#A88C7A', '#7A93A8', '#93A87A', '#A87A93', '#7A7AA8', '#9AA87A'
)

function New-Image([int] $size) {
    return New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function New-Brush([string] $hex) {
    return New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml($hex))
}

function Save-Texture($image, [string] $path, [switch] $Force) {
    if ((Test-Path $path) -and -not $Force) {
        Write-Host "  kept existing $path"
        return
    }

    $image.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "  wrote $path"
}

$textureDir = Join-Path $PSScriptRoot "..\src\main\resources\assets\$namespaces\textures\block"
$modelDir = Join-Path $PSScriptRoot "..\src\main\resources\assets\$namespaces\models\block"
$null = New-Item -ItemType Directory -Force -Path $textureDir, $modelDir

Write-Host "Block '$Block': $($unitList.Count) unit(s) [$($unitList -join ', ')]"

for ($index = 0; $index -lt $unitList.Count; $index++) {
    $unit = $unitList[$index]
    $color = $palette[$index % $palette.Count]

    $image = New-Image 32
    $g = [System.Drawing.Graphics]::FromImage($image)
    $g.SmoothingMode = 'None'
    $g.FillRectangle((New-Brush $color), 0, 0, 32, 32)

    $dark = [System.Drawing.Color]::FromArgb(90, 0, 0, 0)
    $g.DrawRectangle((New-Object System.Drawing.Pen($dark)), 0, 0, 31, 31)

    # index + 1 dots along the top, so unit 1 looks different from unit 2 even in a screenshot
    for ($dot = 0; $dot -le $index; $dot++) {
        $g.FillRectangle((New-Object System.Drawing.SolidBrush($dark)), (3 + $dot * 5), 3, 3, 3)
    }

    $g.Dispose()
    Save-Texture $image (Join-Path $textureDir "$($Block)_$unit.png") -Force:$Force
    $image.Dispose()
}

$slotLines = $unitList | ForEach-Object { '    "' + $_ + '": "' + $namespaces + ':block/' + $Block + '_' + $_ + '"' }
$slots = $slotLines -join ",`n"

if ($isCube) {
    $faceLines = $faces | ForEach-Object { '        "' + $_ + '": { "texture": "#' + $_ + '", "cullface": "' + $_ + '" }' }
    $facesJson = $faceLines -join ",`n"

    $model = @"
{
  "parent": "minecraft:block/block",
  "textures": {
    "particle": "$namespaces`:block/$($Block)_up",
$slots
  },
  "elements": [
    {
      "from": [0, 0, 0],
      "to": [16, 16, 16],
      "faces": {
$facesJson
      }
    }
  ]
}
"@

    $modelPath = Join-Path $modelDir "$Block.json"
    if ((Test-Path $modelPath) -and -not $Force) {
        Write-Host "  kept existing $modelPath"
    } else {
        [System.IO.File]::WriteAllText($modelPath, $model.Replace("`r`n", "`n"), (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "  wrote $modelPath"
    }
}

Write-Host ""
Write-Host "Next steps (docs/TEXTURES.md section 10):"
Write-Host "  1. register the block in src/main/java/com/beyondtime/registry/BTBlocks.java"
if (-not $isCube) {
    Write-Host "  2. write models/block/$Block.json by hand, using these variables:"
    Write-Host "     $slots"
} else {
    Write-Host "  2. models/block/$Block.json is ready - the six faces are wired up"
}
Write-Host "  3. add the blockstate to datagen (src/main/java/com/beyondtime/datagen/BTModelProvider.java)"
Write-Host "  4. run .\gradlew.bat runData, then look at it in tools\dev-client.bat"
