from collections import deque
from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SOURCE = Path(
    r"C:\Users\feder\.codex\generated_images\01a11ed5-d9e5-7eb2-86f4-0f97c96f0646\call_aykHmqhxYmJE3tfMwQescfC0.png"
)
OUT = ROOT / "src/main/resources/assets/modamod/textures/item"

NAMES = [
    "pattern_rayas",
    "pattern_corazones",
    "pattern_estrellas",
    "pattern_lunares",
    "pattern_vichy",
    "tinte_mezcla",
]


def components(img):
    alpha = img.getchannel("A")
    w, h = img.size
    seen = set()
    found = []
    for y in range(h):
        for x in range(w):
            if (x, y) in seen or alpha.getpixel((x, y)) < 20:
                continue
            q = deque([(x, y)])
            seen.add((x, y))
            xs, ys = [], []
            while q:
                cx, cy = q.popleft()
                xs.append(cx)
                ys.append(cy)
                for nx, ny in ((cx + 1, cy), (cx - 1, cy), (cx, cy + 1), (cx, cy - 1)):
                    if 0 <= nx < w and 0 <= ny < h and (nx, ny) not in seen and alpha.getpixel((nx, ny)) >= 20:
                        seen.add((nx, ny))
                        q.append((nx, ny))
            if len(xs) > 1000:
                found.append((min(xs), min(ys), max(xs) + 1, max(ys) + 1, len(xs)))
    return sorted(found, key=lambda b: b[0])


def crop_to_icon(img, box):
    x0, y0, x1, y1, _ = box
    pad = 10
    crop = img.crop((max(0, x0 - pad), max(0, y0 - pad), min(img.width, x1 + pad), min(img.height, y1 + pad)))
    crop.thumbnail((60, 60), Image.Resampling.LANCZOS)
    icon = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    icon.alpha_composite(crop, ((64 - crop.width) // 2, (64 - crop.height) // 2))
    return icon


def split_bottle_layers(icon):
    rgba = icon.convert("RGBA")
    px = rgba.load()
    w, h = rgba.size
    liquid_alpha = Image.new("L", (w, h), 0)
    la = liquid_alpha.load()

    for y in range(20, 58):
        for x in range(15, 49):
            r, g, b, a = px[x, y]
            if a < 30:
                continue
            bright = (r + g + b) / 3
            spread = max(r, g, b) - min(r, g, b)
            if bright > 120 and spread < 95:
                la[x, y] = a

    # Keep the tintable liquid soft and gray/white; punch that area out of the overlay.
    liquid = Image.new("RGBA", (w, h), (232, 232, 232, 0))
    liquid.putalpha(liquid_alpha)
    overlay = rgba.copy()
    opx = overlay.load()
    for y in range(h):
        for x in range(w):
            if la[x, y] > 0:
                r, g, b, a = opx[x, y]
                opx[x, y] = (r, g, b, min(a, 80))
    return liquid, overlay


def make_review(files):
    cell = 80
    review = Image.new("RGBA", (cell * len(files), cell), (12, 12, 12, 255))
    for i, name in enumerate(files):
        if name == "tinte_mezcla":
            mask = Image.open(OUT / "tinte_mezcla_liquido.png").convert("RGBA")
            tint = Image.new("RGBA", (64, 64), (91, 190, 145, 255))
            tint.putalpha(mask.getchannel("A"))
            icon = Image.alpha_composite(tint, Image.open(OUT / "tinte_mezcla.png").convert("RGBA"))
        else:
            icon = Image.open(OUT / f"{name}.png").convert("RGBA")
        review.alpha_composite(icon.resize((64, 64), Image.Resampling.NEAREST), (i * cell + 8, 8))
    review.save(ROOT / "tinturas_visible_review.png")


def main():
    img = Image.open(SOURCE).convert("RGBA")
    boxes = components(img)
    if len(boxes) != len(NAMES):
        raise SystemExit(f"Expected {len(NAMES)} icons, found {len(boxes)} components: {boxes}")

    for name, box in zip(NAMES, boxes):
        icon = crop_to_icon(img, box)
        if name == "tinte_mezcla":
            liquid, overlay = split_bottle_layers(icon)
            liquid.save(OUT / "tinte_mezcla_liquido.png")
            overlay.save(OUT / "tinte_mezcla.png")
        else:
            icon.save(OUT / f"{name}.png")

    make_review(NAMES)


if __name__ == "__main__":
    main()
