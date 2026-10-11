from pathlib import Path

from PIL import Image, ImageDraw

if __name__ == "__main__":
    from import_red_icons import main

    main()
    raise SystemExit


ITEM_DIR = Path("src/main/resources/assets/modamod/textures/item")

RED_NAMES = [
    "molde_red_fina",
    "molde_red_gruesa",
    "molde_red_lisa",
    "molde_red_hexagonal",
    "molde_red_perforada",
    "molde_red_encaje",
    "molde_red_rayas",
    "molde_red_escocesa",
    "molde_red_arnes_x",
    "molde_red_arnes_tirantes",
    "molde_red_arnes_bandas",
]


def add_pin(image: Image.Image, position: tuple[int, int] = (44, 7)) -> Image.Image:
    d = ImageDraw.Draw(image)
    x, y = position
    d.line([x + 4, y + 11, x - 5, y + 22], fill=(215, 205, 176, 255), width=2)
    d.point((x - 5, y + 22), fill=(80, 70, 62, 255))
    d.ellipse([x, y, x + 12, y + 12], fill=(255, 134, 24, 255), outline=(77, 35, 12, 255), width=2)
    d.ellipse([x + 3, y + 3, x + 7, y + 7], fill=(255, 219, 96, 255))
    return image


def base_card() -> tuple[Image.Image, ImageDraw.ImageDraw]:
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)

    d.rectangle([4, 4, 59, 59], fill=(43, 23, 13, 255))
    d.rectangle([6, 6, 57, 57], fill=(132, 87, 48, 255))
    d.rectangle([8, 8, 55, 55], fill=(202, 154, 91, 255))
    d.rectangle([9, 9, 54, 54], outline=(72, 42, 24, 255), width=2)
    d.line([10, 10, 53, 10], fill=(236, 189, 118, 255))
    d.line([10, 53, 53, 53], fill=(88, 52, 29, 255))
    d.rectangle([12, 12, 51, 51], fill=(15, 14, 13, 255))
    d.rectangle([12, 12, 51, 51], outline=(43, 26, 16, 255), width=2)
    return im, d


def draw_diamonds(im: Image.Image, step: int, width: int) -> None:
    layer = Image.new("RGBA", im.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    for x in range(-32, 92, step):
        d.line([x, 12, x + 39, 51], fill=(207, 158, 94, 255), width=width)
        d.line([x, 51, x + 39, 12], fill=(207, 158, 94, 255), width=width)
    mask = Image.new("L", im.size, 0)
    ImageDraw.Draw(mask).rectangle([12, 12, 51, 51], fill=255)
    im.alpha_composite(Image.composite(layer, Image.new("RGBA", im.size, (0, 0, 0, 0)), mask))


def draw_hexagons(d: ImageDraw.ImageDraw) -> None:
    for y in (17, 31, 45):
        for x in (14, 30, 46):
            d.line(
                [
                    (x - 6, y),
                    (x - 3, y - 5),
                    (x + 3, y - 5),
                    (x + 6, y),
                    (x + 3, y + 5),
                    (x - 3, y + 5),
                    (x - 6, y),
                ],
                fill=(205, 157, 94, 255),
                width=3,
            )


def draw_red_icon(name: str) -> Image.Image:
    im, d = base_card()
    if name == "molde_red_fina":
        draw_diamonds(im, 9, 1)
    elif name == "molde_red_gruesa":
        draw_diamonds(im, 18, 4)
    elif name == "molde_red_lisa":
        for y in range(15, 50, 4):
            d.line([14, y, 49, y], fill=(31, 30, 28, 255))
        for x in range(15, 50, 5):
            d.line([x, 14, x, 49], fill=(25, 24, 23, 255))
    elif name == "molde_red_hexagonal":
        draw_hexagons(d)
    elif name == "molde_red_perforada":
        for y in (18, 29, 40):
            for x in (18, 29, 40):
                d.ellipse([x - 3, y - 3, x + 3, y + 3], fill=(203, 154, 91, 255), outline=(64, 39, 24, 255))
    elif name == "molde_red_encaje":
        draw_diamonds(im, 17, 2)
        for y in (22, 39):
            for x in (22, 39):
                d.polygon([(x, y - 3), (x + 3, y), (x, y + 3), (x - 3, y)], fill=(4, 4, 4, 255), outline=(211, 159, 94, 255))
    elif name == "molde_red_rayas":
        for y0, y1 in ((17, 21), (29, 33), (41, 45)):
            d.rectangle([14, y0, 49, y1], fill=(204, 154, 91, 255))
            offset = 18 if y0 == 29 else 26
            for x in range(offset, 48, 13):
                d.rectangle([x, y0, x + 4, y1], fill=(15, 14, 13, 255))
    elif name == "molde_red_escocesa":
        for x in (20, 31, 42):
            d.rectangle([x - 2, 13, x + 2, 50], fill=(203, 154, 91, 255))
        for y in (20, 31, 42):
            d.rectangle([13, y - 2, 50, y + 2], fill=(203, 154, 91, 255))
        for x in (20, 31, 42):
            d.line([x, 13, x, 50], fill=(71, 43, 26, 255))
        for y in (20, 31, 42):
            d.line([13, y, 50, y], fill=(71, 43, 26, 255))
    elif name == "molde_red_arnes_x":
        d.rectangle([12, 12, 51, 51], fill=(202, 154, 91, 255))
        d.line([15, 15, 48, 48], fill=(117, 55, 25, 255), width=6)
        d.line([48, 15, 15, 48], fill=(117, 55, 25, 255), width=6)
        d.ellipse([27, 27, 37, 37], outline=(238, 165, 59, 255), width=3)
    elif name == "molde_red_arnes_tirantes":
        d.rectangle([12, 12, 51, 51], fill=(202, 154, 91, 255))
        for x in (21, 43):
            d.rectangle([x - 3, 13, x + 3, 50], fill=(117, 55, 25, 255))
        d.rectangle([15, 29, 49, 35], fill=(117, 55, 25, 255))
        for x in (21, 43):
            d.ellipse([x - 5, 27, x + 5, 37], outline=(238, 165, 59, 255), width=2)
    elif name == "molde_red_arnes_bandas":
        d.rectangle([12, 12, 51, 51], fill=(202, 154, 91, 255))
        for y in (20, 32, 44):
            d.rectangle([14, y - 3, 49, y + 3], fill=(117, 55, 25, 255))
            d.ellipse([28, y - 5, 38, y + 5], outline=(238, 165, 59, 255), width=2)
    return add_pin(im)


def make_review(path: Path) -> None:
    thumb = 96
    labelh = 22
    pad = 8
    out = Image.new(
        "RGBA",
        (len(RED_NAMES) * (thumb + pad) + pad, thumb + labelh + pad * 2),
        (245, 245, 245, 255),
    )
    draw = ImageDraw.Draw(out)
    for i, name in enumerate(RED_NAMES):
        im = Image.open(ITEM_DIR / f"{name}.png").convert("RGBA")
        bg = Image.new("RGBA", (thumb, thumb), (255, 255, 255, 255))
        checker = ImageDraw.Draw(bg)
        for y in range(0, thumb, 8):
            for x in range(0, thumb, 8):
                if (x // 8 + y // 8) % 2:
                    checker.rectangle([x, y, x + 7, y + 7], fill=(210, 80, 210, 255))
        bg.alpha_composite(im.resize((thumb, thumb), Image.Resampling.NEAREST))
        x = pad + i * (thumb + pad)
        out.alpha_composite(bg, (x, pad))
        draw.text((x, pad + thumb + 2), name.replace("molde_red_", "")[:14], fill=(0, 0, 0, 255))
    out.save(path)


def main() -> None:
    make_review(Path("red_alpha_review_before.png"))
    for name in RED_NAMES:
        path = ITEM_DIR / f"{name}.png"
        draw_red_icon(name).save(path)
    make_review(Path("red_alpha_review_after.png"))


if __name__ == "__main__":
    main()
