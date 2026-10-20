#!/usr/bin/env python3
"""Draw the Beyond-Time machine textures.

The style is deliberately close to vanilla/modded Minecraft machinery: 32x32 pixels, hard
one-pixel outlines, a warm brass + cold iron + teal glass palette, and no anti-aliasing. The
basic analyzer is a full cube, so it gets six separate face textures (rule R10); the sampler
and the sealed sample are flat 32x32 item icons.

Run from the project root:

    python tools/make_machine_textures.py

The script overwrites only the files it owns. It also writes preview sheets under docs/img/.
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
TEX = ROOT / "src/main/resources/assets/beyondtime/textures"
IMG = ROOT / "docs/img"

S = 32
T = (0, 0, 0, 0)

# Palette: dark iron, brass, glass, parchment.
IRON_0 = (30, 33, 37, 255)
IRON_1 = (47, 52, 58, 255)
IRON_2 = (65, 71, 78, 255)
IRON_3 = (91, 99, 107, 255)
IRON_4 = (126, 135, 144, 255)
BRASS_0 = (74, 47, 20, 255)
BRASS_1 = (120, 79, 31, 255)
BRASS_2 = (166, 116, 48, 255)
BRASS_3 = (205, 154, 71, 255)
BRASS_4 = (238, 201, 120, 255)
GLASS_0 = (17, 43, 47, 255)
GLASS_1 = (30, 76, 82, 255)
GLASS_2 = (58, 124, 130, 255)
GLASS_3 = (104, 181, 184, 255)
GLASS_4 = (188, 238, 232, 255)
RED_0 = (91, 29, 26, 255)
RED_1 = (148, 45, 36, 255)
RED_2 = (196, 70, 52, 255)
PAPER_0 = (164, 145, 111, 255)
PAPER_1 = (203, 187, 154, 255)
PAPER_2 = (232, 220, 193, 255)
WHITE = (238, 243, 240, 255)
GREEN = (92, 170, 96, 255)


def canvas() -> Image.Image:
    return Image.new("RGBA", (S, S), T)


def rect(d: ImageDraw.ImageDraw, x0: int, y0: int, x1: int, y1: int, color) -> None:
    """Fill [x0, x1) x [y0, y1)."""
    if x1 > x0 and y1 > y0:
        d.rectangle([x0, y0, x1 - 1, y1 - 1], fill=color)


def px(d: ImageDraw.ImageDraw, x: int, y: int, color) -> None:
    if 0 <= x < S and 0 <= y < S:
        d.point((x, y), fill=color)


def line(d: ImageDraw.ImageDraw, x0: int, y0: int, x1: int, y1: int, color) -> None:
    d.line([x0, y0, x1, y1], fill=color)


def bevel_box(d: ImageDraw.ImageDraw, x0: int, y0: int, x1: int, y1: int,
              fill, light, dark, border=None) -> None:
    """A vanilla-ish raised box."""
    rect(d, x0, y0, x1, y1, fill)
    line(d, x0, y0, x1 - 1, y0, light)
    line(d, x0, y0, x0, y1 - 1, light)
    line(d, x0, y1 - 1, x1 - 1, y1 - 1, dark)
    line(d, x1 - 1, y0, x1 - 1, y1 - 1, dark)
    if border is not None:
        d.rectangle([x0 - 1, y0 - 1, x1, y1], outline=border)


def rivet(d: ImageDraw.ImageDraw, x: int, y: int, color=BRASS_3) -> None:
    px(d, x, y, color)
    px(d, x + 1, y + 1, BRASS_0)


def plate_base(d: ImageDraw.ImageDraw, edge=IRON_1) -> None:
    rect(d, 0, 0, S, S, IRON_2)
    # Dark outline and inner panel bevel.
    d.rectangle([0, 0, S - 1, S - 1], outline=IRON_0)
    d.rectangle([1, 1, S - 2, S - 2], outline=edge)
    # Subtle dithering so the plate is not a flat colour.
    for y in range(2, S - 2, 3):
        for x in range(2, S - 2, 5):
            if (x + y) % 4 == 0:
                px(d, x, y, IRON_3)
    for x, y in ((2, 2), (29, 2), (2, 29), (29, 29)):
        rivet(d, x, y)


def vent(d: ImageDraw.ImageDraw, x0: int, y0: int, x1: int, rows: int) -> None:
    for i in range(rows):
        y = y0 + i * 3
        line(d, x0, y, x1, y, IRON_0)
        line(d, x0, y + 1, x1, y + 1, IRON_3)


def sampler() -> Image.Image:
    """A brass coring tool: glass barrel, dark grip, a needle, a red tissue window."""
    im = canvas()
    d = ImageDraw.Draw(im)

    # Needle
    rect(d, 15, 1, 17, 7, IRON_3)
    px(d, 15, 1, IRON_4)
    px(d, 16, 0, WHITE)
    line(d, 17, 1, 17, 7, IRON_1)

    # Brass collar and barrel
    rect(d, 12, 7, 20, 10, BRASS_1)
    d.rectangle([12, 7, 19, 9], outline=BRASS_0)
    line(d, 12, 7, 19, 7, BRASS_3)
    rect(d, 11, 10, 21, 24, BRASS_2)
    d.rectangle([11, 10, 20, 23], outline=BRASS_0)
    line(d, 11, 10, 20, 10, BRASS_4)
    line(d, 11, 10, 11, 23, BRASS_3)
    line(d, 20, 11, 20, 23, BRASS_1)

    # Glass window with the tissue core
    rect(d, 13, 13, 19, 21, GLASS_1)
    d.rectangle([13, 13, 18, 20], outline=GLASS_0)
    line(d, 13, 13, 18, 13, GLASS_3)
    line(d, 13, 13, 13, 20, GLASS_3)
    rect(d, 14, 15, 18, 20, RED_1)
    rect(d, 15, 16, 17, 19, RED_2)
    px(d, 14, 15, RED_0)
    px(d, 15, 15, (220, 120, 92, 255))
    px(d, 18, 15, GLASS_4)

    # Barrel bands
    for y in (12, 22):
        line(d, 11, y, 20, y, BRASS_4)
        line(d, 11, y + 1, 20, y + 1, BRASS_0)

    # Lower collar and grip
    rect(d, 12, 24, 20, 26, BRASS_1)
    d.rectangle([12, 24, 19, 25], outline=BRASS_0)
    rect(d, 10, 26, 22, 31, IRON_1)
    d.rectangle([10, 26, 21, 30], outline=IRON_0)
    line(d, 10, 26, 21, 26, IRON_3)
    line(d, 10, 26, 10, 30, IRON_3)
    # Grip ridges
    for x in (12, 15, 18):
        line(d, x, 27, x, 30, IRON_0)
        line(d, x + 1, 27, x + 1, 30, IRON_2)

    # A tiny brass ring at the very bottom
    rect(d, 13, 30, 19, 32, BRASS_2)
    line(d, 13, 30, 18, 30, BRASS_4)
    return im


def sealed_sample() -> Image.Image:
    """A glass vial with a brass cap and a dark red tissue core."""
    im = canvas()
    d = ImageDraw.Draw(im)

    # Cap
    rect(d, 12, 4, 20, 8, BRASS_2)
    d.rectangle([12, 4, 19, 7], outline=BRASS_0)
    line(d, 12, 4, 19, 4, BRASS_4)
    line(d, 12, 5, 12, 7, BRASS_3)
    # Cap ridges
    for x in (14, 17):
        line(d, x, 4, x, 7, BRASS_1)

    # Neck
    rect(d, 14, 8, 18, 12, GLASS_2)
    d.rectangle([14, 8, 17, 11], outline=GLASS_0)
    line(d, 14, 8, 14, 11, GLASS_4)

    # Body
    rect(d, 10, 12, 22, 29, GLASS_2)
    d.rectangle([10, 12, 21, 28], outline=GLASS_0)
    line(d, 10, 12, 21, 12, GLASS_3)
    line(d, 10, 12, 10, 28, GLASS_4)
    line(d, 21, 13, 21, 28, GLASS_1)
    # Glass highlights
    line(d, 12, 14, 12, 26, GLASS_3)
    line(d, 19, 15, 19, 27, GLASS_1)

    # Tissue core, slightly irregular
    rect(d, 13, 17, 19, 26, RED_1)
    rect(d, 14, 18, 18, 25, RED_2)
    rect(d, 15, 20, 17, 23, (176, 58, 44, 255))
    px(d, 13, 17, RED_0)
    px(d, 14, 17, (220, 120, 92, 255))
    px(d, 18, 17, RED_0)
    # Bubbles
    px(d, 15, 16, GLASS_4)
    px(d, 18, 15, GLASS_3)

    # Paper label
    rect(d, 11, 13, 13, 16, PAPER_2)
    rect(d, 18, 13, 21, 15, PAPER_2)
    px(d, 12, 14, RED_1)

    # Base highlight
    line(d, 11, 28, 20, 28, GLASS_3)
    return im


def analyzer_up() -> Image.Image:
    im = canvas()
    d = ImageDraw.Draw(im)
    plate_base(d)

    # Brass sample port
    d.ellipse([8, 8, 23, 23], fill=BRASS_2, outline=BRASS_0)
    d.ellipse([10, 10, 21, 21], fill=BRASS_3)
    d.ellipse([12, 12, 19, 19], fill=BRASS_1, outline=BRASS_0)
    d.ellipse([14, 14, 17, 17], fill=GLASS_1)
    d.ellipse([15, 15, 16, 16], fill=GLASS_4)
    # Port screws
    for x, y in ((9, 9), (22, 9), (9, 22), (22, 22)):
        px(d, x, y, BRASS_4)

    # Vents in the corners
    for x0, y0 in ((3, 4), (24, 4), (3, 25), (24, 25)):
        for i in range(3):
            line(d, x0, y0 + i * 2, x0 + 4, y0 + i * 2, IRON_0)
            line(d, x0, y0 + i * 2 + 1, x0 + 4, y0 + i * 2 + 1, IRON_3)

    # Front marker (the block model is rotated, this is the model's north edge)
    line(d, 2, 0, 29, 0, BRASS_3)
    return im


def analyzer_down() -> Image.Image:
    im = canvas()
    d = ImageDraw.Draw(im)
    plate_base(d)
    # Recessed base plate
    d.rectangle([4, 4, 27, 27], fill=IRON_1, outline=IRON_0)
    line(d, 5, 5, 26, 5, IRON_3)
    line(d, 5, 5, 5, 26, IRON_3)
    # Cross brace
    rect(d, 14, 5, 18, 26, IRON_2)
    rect(d, 5, 14, 26, 18, IRON_2)
    line(d, 14, 5, 14, 26, IRON_3)
    line(d, 5, 14, 26, 14, IRON_3)
    # Feet
    for x, y in ((2, 2), (29, 2), (2, 29), (29, 29)):
        rect(d, x - 1, y - 1, x + 2, y + 2, IRON_0)
        px(d, x, y, BRASS_2)
    return im


def analyzer_front() -> Image.Image:
    im = canvas()
    d = ImageDraw.Draw(im)
    plate_base(d)

    # Brass header
    rect(d, 2, 2, 30, 7, BRASS_1)
    d.rectangle([2, 2, 29, 6], outline=BRASS_0)
    line(d, 2, 2, 29, 2, BRASS_4)
    line(d, 2, 3, 29, 3, BRASS_3)
    # Indicator light
    rect(d, 24, 4, 27, 6, IRON_0)
    rect(d, 25, 4, 26, 5, RED_2)
    px(d, 25, 4, (240, 140, 110, 255))

    # Viewing window with a brass frame
    d.rectangle([6, 9, 25, 21], fill=BRASS_1, outline=BRASS_0)
    d.rectangle([7, 10, 24, 20], fill=GLASS_1, outline=GLASS_0)
    # Glass gradient and reflection
    rect(d, 8, 11, 23, 19, GLASS_2)
    rect(d, 9, 12, 22, 18, GLASS_1)
    line(d, 8, 11, 23, 11, GLASS_3)
    line(d, 8, 11, 8, 19, GLASS_3)
    line(d, 9, 13, 14, 13, GLASS_4)
    line(d, 9, 14, 10, 14, GLASS_4)
    # A tiny vial silhouette behind the glass
    rect(d, 14, 12, 18, 18, (34, 80, 85, 255))
    rect(d, 15, 13, 17, 16, RED_1)
    rect(d, 15, 12, 17, 13, BRASS_2)

    # Sample slot
    d.rectangle([9, 23, 22, 27], fill=IRON_1, outline=IRON_0)
    line(d, 10, 24, 21, 24, IRON_0)
    line(d, 10, 25, 21, 25, IRON_3)
    rect(d, 18, 24, 21, 26, GREEN)

    # Brass footer strip
    rect(d, 2, 28, 30, 31, BRASS_1)
    line(d, 2, 28, 29, 28, BRASS_3)
    line(d, 2, 29, 29, 29, BRASS_0)

    # Corner brackets
    for x, y, dx, dy in ((1, 8, 1, 1), (30, 8, -1, 1), (1, 22, 1, -1), (30, 22, -1, -1)):
        line(d, x, y, x + 3 * dx, y, BRASS_3)
        line(d, x, y, x, y + 3 * dy, BRASS_3)
    return im


def analyzer_back() -> Image.Image:
    im = canvas()
    d = ImageDraw.Draw(im)
    plate_base(d)

    # Brass spine
    rect(d, 14, 2, 18, 30, BRASS_1)
    line(d, 14, 2, 14, 29, BRASS_3)
    line(d, 18, 2, 18, 29, BRASS_0)
    for y in (5, 11, 17, 23):
        line(d, 14, y, 18, y, BRASS_0)
        px(d, 16, y, BRASS_4)

    # Vent banks
    for x0 in (3, 20):
        for i in range(6):
            y = 4 + i * 4
            line(d, x0, y, x0 + 8, y, IRON_0)
            line(d, x0, y + 1, x0 + 8, y + 1, IRON_3)
            line(d, x0, y + 2, x0 + 8, y + 2, IRON_1)

    # Power socket
    d.ellipse([11, 22, 21, 30], fill=IRON_1, outline=IRON_0)
    d.ellipse([13, 24, 19, 28], fill=IRON_0)
    rect(d, 15, 25, 17, 28, BRASS_2)
    return im


def analyzer_side(mirror: bool = False) -> Image.Image:
    im = canvas()
    d = ImageDraw.Draw(im)
    plate_base(d)

    # Vertical brass rail near the front edge
    x0, x1 = (3, 7) if not mirror else (25, 29)
    rect(d, x0, 2, x1, 30, BRASS_1)
    line(d, x0, 2, x0, 29, BRASS_3)
    line(d, x1 - 1, 2, x1 - 1, 29, BRASS_0)
    for y in (5, 12, 19, 26):
        px(d, (x0 + x1) // 2, y, BRASS_4)

    # Vent block
    vx0, vx1 = (10, 28) if not mirror else (4, 22)
    d.rectangle([vx0, 8, vx1, 24], fill=IRON_1, outline=IRON_0)
    for i in range(4):
        y = 10 + i * 4
        line(d, vx0 + 2, y, vx1 - 2, y, IRON_0)
        line(d, vx0 + 2, y + 1, vx1 - 2, y + 1, IRON_3)

    # Little service hatch
    d.rectangle([vx0 + 2, 25, vx1 - 2, 29], fill=IRON_2, outline=IRON_0)
    px(d, vx1 - 4, 27, BRASS_3)
    return im


def gui_background() -> Image.Image:
    im = Image.new("RGBA", (256, 256), T)
    d = ImageDraw.Draw(im)

    W, H = 200, 240
    # Drop shadow
    rect(d, 3, 3, W + 3, H + 3, (0, 0, 0, 70))
    # Parchment panel
    rect(d, 0, 0, W, H, PAPER_1)
    d.rectangle([0, 0, W - 1, H - 1], outline=BRASS_0)
    d.rectangle([1, 1, W - 2, H - 2], outline=BRASS_2)
    d.rectangle([2, 2, W - 3, H - 3], outline=PAPER_0)
    # Inner paper field
    rect(d, 4, 4, W - 4, H - 4, PAPER_2)

    # Header strip
    rect(d, 4, 4, W - 4, 17, BRASS_2)
    line(d, 4, 4, W - 5, 4, BRASS_4)
    line(d, 4, 16, W - 5, 16, BRASS_0)
    for x in range(8, W - 8, 12):
        px(d, x, 12, BRASS_3)

    # Small double-helix mark in the header, right side
    hx, hy = 176, 6
    for i in range(6):
        px(d, hx + (i % 2), hy + i, BRASS_4)
        px(d, hx + 5 - (i % 2), hy + i, BRASS_4)
        if i % 2 == 0:
            line(d, hx + 1, hy + i, hx + 4, hy + i, BRASS_0)

    # Sample well (8,20, 18x18)
    sx, sy = 8, 20
    rect(d, sx, sy, sx + 18, sy + 18, IRON_0)
    d.rectangle([sx, sy, sx + 17, sy + 17], outline=BRASS_0)
    line(d, sx + 1, sy + 1, sx + 16, sy + 1, IRON_3)
    line(d, sx + 1, sy + 1, sx + 1, sy + 16, IRON_3)
    line(d, sx + 1, sy + 16, sx + 16, sy + 16, IRON_1)
    line(d, sx + 16, sy + 1, sx + 16, sy + 16, IRON_1)

    # A brass arrow from the sample well toward the report
    ax, ay = 30, 38
    line(d, ax, ay, ax + 152, ay, BRASS_1)
    line(d, ax, ay - 1, ax + 152, ay - 1, BRASS_3)

    # Report field (8,44, 184x104)
    rx, ry, rw, rh = 8, 44, 184, 104
    rect(d, rx, ry, rx + rw, ry + rh, (239, 228, 202, 255))
    d.rectangle([rx, ry, rx + rw - 1, ry + rh - 1], outline=BRASS_1)
    d.rectangle([rx + 1, ry + 1, rx + rw - 2, ry + rh - 2], outline=(224, 210, 180, 255))
    # Notebook rules, faint, one per report line (text top is ry + 4 + i*10).
    for i in range(1, 10):
        y = ry + 14 + i * 10
        if y < ry + rh - 4:
            line(d, rx + 4, y, rx + rw - 5, y, (226, 213, 185, 255))
    # A red margin line
    line(d, rx + 3, ry + 3, rx + 3, ry + rh - 4, (198, 170, 146, 255))

    # Wax seal, bottom right of the report
    cx, cy = rx + rw - 16, ry + rh - 15
    d.ellipse([cx - 6, cy - 6, cx + 6, cy + 6], fill=RED_1, outline=RED_0)
    d.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], outline=(176, 62, 48, 255))
    px(d, cx - 2, cy - 2, (216, 118, 92, 255))
    line(d, cx - 1, cy - 1, cx + 2, cy + 2, RED_0)
    line(d, cx + 1, cy - 1, cx - 2, cy + 2, RED_0)

    # Separator above inventory
    line(d, 8, 150, W - 8, 150, BRASS_1)
    line(d, 8, 151, W - 8, 151, PAPER_0)

    # Player inventory slot wells: 9 x 3, then hotbar
    inv_x = (W - 9 * 18) // 2
    for row in range(3):
        for col in range(9):
            slot_well(d, inv_x + col * 18, 156 + row * 18)
    for col in range(9):
        slot_well(d, inv_x + col * 18, 214)

    # Brass corner brackets
    for x, y, dx, dy in ((1, 1, 1, 1), (W - 2, 1, -1, 1), (1, H - 2, 1, -1), (W - 2, H - 2, -1, -1)):
        line(d, x, y, x + 6 * dx, y, BRASS_3)
        line(d, x, y, x, y + 6 * dy, BRASS_3)

    return im


def slot_well(d: ImageDraw.ImageDraw, x: int, y: int) -> None:
    rect(d, x, y, x + 18, y + 18, IRON_0)
    d.rectangle([x, y, x + 17, y + 17], outline=BRASS_0)
    line(d, x + 1, y + 1, x + 16, y + 1, IRON_3)
    line(d, x + 1, y + 1, x + 1, y + 16, IRON_3)
    line(d, x + 1, y + 16, x + 16, y + 16, IRON_1)
    line(d, x + 16, y + 1, x + 16, y + 16, IRON_1)


def preview() -> None:
    """Write a 4x zoomed contact sheet of the machine textures."""
    names = [
        ("sampler", sampler()),
        ("sealed_tissue_sample", sealed_sample()),
        ("basic_analyzer_up", analyzer_up()),
        ("basic_analyzer_down", analyzer_down()),
        ("basic_analyzer_north", analyzer_front()),
        ("basic_analyzer_south", analyzer_back()),
        ("basic_analyzer_west", analyzer_side(False)),
        ("basic_analyzer_east", analyzer_side(True)),
    ]
    cell = 32 * 4
    cols = 4
    rows = (len(names) + cols - 1) // cols
    sheet = Image.new("RGBA", (cols * cell, rows * (cell + 20) + 8), (34, 34, 34, 255))
    d = ImageDraw.Draw(sheet)
    for index, (name, image) in enumerate(names):
        col = index % cols
        row = index // cols
        x = col * cell + 4
        y = row * (cell + 20) + 4
        sheet.alpha_composite(image.resize((cell, cell), Image.Resampling.NEAREST), (x, y))
        d.text((x, y + cell + 3), name, fill=(230, 230, 230, 255))
    sheet.save(IMG / "machine-textures.png")

    gui = gui_background()
    gui.resize((512, 512), Image.Resampling.NEAREST).save(IMG / "basic-analyzer-gui.png")


def main() -> None:
    (TEX / "item").mkdir(parents=True, exist_ok=True)
    (TEX / "block").mkdir(parents=True, exist_ok=True)
    (TEX / "gui").mkdir(parents=True, exist_ok=True)
    IMG.mkdir(parents=True, exist_ok=True)

    sampler().save(TEX / "item/sampler.png")
    sealed_sample().save(TEX / "item/sealed_tissue_sample.png")
    analyzer_up().save(TEX / "block/basic_analyzer_up.png")
    analyzer_down().save(TEX / "block/basic_analyzer_down.png")
    analyzer_front().save(TEX / "block/basic_analyzer_north.png")
    analyzer_back().save(TEX / "block/basic_analyzer_south.png")
    analyzer_side(False).save(TEX / "block/basic_analyzer_west.png")
    analyzer_side(True).save(TEX / "block/basic_analyzer_east.png")
    gui_background().save(TEX / "gui/basic_analyzer.png")
    preview()
    print("wrote machine textures and previews")


if __name__ == "__main__":
    main()
