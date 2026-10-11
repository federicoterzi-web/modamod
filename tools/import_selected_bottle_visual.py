from collections import deque
from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SRC = Path(
    r"C:\Users\feder\.codex\generated_images\01a11ed5-d9e5-7eb2-86f4-0f97c96f0646\call_gCbliVgYApi74ysCwzan30yh.png"
)
OUT = ROOT / "src/main/resources/assets/modamod/textures/item"


def find_components(img):
    alpha = img.getchannel("A")
    w, h = img.size
    seen = set()
    comps = []
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
            if len(xs) > 5000:
                comps.append((min(xs), min(ys), max(xs) + 1, max(ys) + 1, len(xs)))
    return sorted(comps, key=lambda b: b[0])


def main():
    img = Image.open(SRC).convert("RGBA")
    box = find_components(img)[2]
    pad = 18
    crop = img.crop(
        (
            max(0, box[0] - pad),
            max(0, box[1] - pad),
            min(img.width, box[2] + pad),
            min(img.height, box[3] + pad),
        )
    )
    crop.thumbnail((62, 62), Image.Resampling.LANCZOS)
    icon = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    icon.alpha_composite(crop, ((64 - crop.width) // 2, (64 - crop.height) // 2))

    liquid = Image.new("RGBA", (64, 64), (235, 235, 235, 0))
    overlay = icon.copy()
    src_px = icon.load()
    liquid_px = liquid.load()
    overlay_px = overlay.load()

    for y in range(64):
        for x in range(64):
            r, g, b, a = src_px[x, y]
            is_liquid = a > 20 and g > 115 and g > r + 18 and g > b + 8
            if is_liquid:
                value = max(185, min(255, int((r + g + b) / 3) + 35))
                liquid_px[x, y] = (value, value, value, a)
                overlay_px[x, y] = (r, g, b, 0)

    liquid.save(OUT / "tinte_mezcla_liquido.png")
    overlay.save(OUT / "tinte_mezcla.png")

    tint = Image.new("RGBA", (64, 64), (85, 190, 145, 255))
    tint.putalpha(liquid.getchannel("A"))
    composed = Image.alpha_composite(tint, overlay)
    review = Image.new("RGBA", (96, 80), (12, 12, 12, 255))
    review.alpha_composite(composed.resize((64, 64), Image.Resampling.NEAREST), (16, 8))
    review.save(ROOT / "tinte_mezcla_mask_review.png")


if __name__ == "__main__":
    main()
