from collections import deque
import os
from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
ITEM_DIR = ROOT / "src/main/resources/assets/modamod/textures/item"
GEN_DIR = Path(r"C:/Users/feder/.codex/generated_images/01a11ed5-d9e5-7eb2-86f4-0f97c96f0646")

TRAMA_SHEET = GEN_DIR / "call_kAHPaJAy1otFFzB1RhWlhsO9.png"
RAYAS_SINGLE = GEN_DIR / "call_aYNCUi2KLpuwIV4yBYBABhV2.png"
ARNES_SHEET = GEN_DIR / "call_BdAGOHviNqSSbGQkYqpXyq5a.png"


def components(path: Path, min_area: int = 1000):
    im = Image.open(path).convert("RGBA")
    alpha = im.getchannel("A")
    w, h = im.size
    pix = alpha.load()
    seen: set[tuple[int, int]] = set()
    boxes = []
    for y in range(h):
        for x in range(w):
            if (x, y) in seen or pix[x, y] < 12:
                continue
            q = deque([(x, y)])
            seen.add((x, y))
            xs: list[int] = []
            ys: list[int] = []
            count = 0
            while q:
                cx, cy = q.popleft()
                xs.append(cx)
                ys.append(cy)
                count += 1
                for nx, ny in ((cx + 1, cy), (cx - 1, cy), (cx, cy + 1), (cx, cy - 1)):
                    if 0 <= nx < w and 0 <= ny < h and (nx, ny) not in seen and pix[nx, ny] >= 12:
                        seen.add((nx, ny))
                        q.append((nx, ny))
            if count >= min_area:
                boxes.append((min(xs), min(ys), max(xs) + 1, max(ys) + 1, count))
    boxes.sort(key=lambda b: b[0])
    return im, boxes


def save_crop(im: Image.Image, bbox, name: str, pad: int = 10) -> None:
    x0, y0, x1, y1, _ = bbox
    cx = (x0 + x1) / 2
    cy = (y0 + y1) / 2
    side = max(x1 - x0, y1 - y0) + pad * 2
    left = round(cx - side / 2)
    top = round(cy - side / 2)
    canvas = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    canvas.alpha_composite(im.crop((left, top, left + side, top + side)), (0, 0))
    dest = ITEM_DIR / f"{name}.png"
    tmp = ITEM_DIR / f".{name}.tmp.png"
    final = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    final.alpha_composite(canvas.resize((58, 58), Image.Resampling.LANCZOS), (3, 3))
    final.save(tmp)
    os.replace(tmp, dest)


def make_review(path: Path) -> None:
    names = [
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
    thumb = 96
    pad = 8
    label_h = 22
    review = Image.new("RGBA", (len(names) * (thumb + pad) + pad, thumb + label_h + pad * 2), (18, 18, 18, 255))
    draw = ImageDraw.Draw(review)
    for i, name in enumerate(names):
        icon = Image.open(ITEM_DIR / f"{name}.png").convert("RGBA")
        bg = Image.new("RGBA", (thumb, thumb), (0, 0, 0, 255))
        bg.alpha_composite(icon.resize((thumb, thumb), Image.Resampling.NEAREST))
        x = pad + i * (thumb + pad)
        review.alpha_composite(bg, (x, pad))
        label = name.replace("molde_red_", "").replace("escocesa", "grilla")[:14]
        draw.text((x, pad + thumb + 2), label, fill=(235, 235, 235, 255))
    review.save(path)


def main() -> None:
    trama_names = [
        "molde_red_fina",
        "molde_red_gruesa",
        "molde_red_lisa",
        "molde_red_hexagonal",
        "molde_red_perforada",
        "molde_red_encaje",
        None,
        "molde_red_escocesa",
    ]
    im, boxes = components(TRAMA_SHEET)
    for name, box in zip(trama_names, boxes):
        if name:
            save_crop(im, box, name, pad=10)

    rayas = Image.open(RAYAS_SINGLE).convert("RGBA")
    x0, y0, x1, y1 = rayas.getchannel("A").getbbox()
    save_crop(rayas, (x0, y0, x1, y1, 0), "molde_red_rayas", pad=8)

    arnes, arnes_boxes = components(ARNES_SHEET)
    for name, box in zip(["molde_red_arnes_x", "molde_red_arnes_tirantes", "molde_red_arnes_bandas"], arnes_boxes):
        save_crop(arnes, box, name, pad=12)

    make_review(ROOT / "red_final_review.png")


if __name__ == "__main__":
    main()
