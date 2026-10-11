from collections import deque
from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SOURCE = Path(
    r"C:\Users\feder\.codex\generated_images\01a11ed5-d9e5-7eb2-86f4-0f97c96f0646\call_AlxNCr45nKansBttqMTozs2S.png"
)
OUT = ROOT / "src/main/resources/assets/modamod/textures/item"

NAMES = [
    "molde_mascara_cuadrado",
    "molde_mascara_franja",
    "molde_mascara_circulo",
    "molde_mascara_estrella",
    "molde_mascara_triangulo",
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


def crop_icon(img, box):
    x0, y0, x1, y1, _ = box
    pad = 12
    crop = img.crop((max(0, x0 - pad), max(0, y0 - pad), min(img.width, x1 + pad), min(img.height, y1 + pad)))
    crop.thumbnail((62, 62), Image.Resampling.LANCZOS)
    icon = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    icon.alpha_composite(crop, ((64 - crop.width) // 2, (64 - crop.height) // 2))
    return icon


def make_review():
    cell = 80
    review = Image.new("RGBA", (cell * len(NAMES), cell), (12, 12, 12, 255))
    for i, name in enumerate(NAMES):
        icon = Image.open(OUT / f"{name}.png").convert("RGBA")
        review.alpha_composite(icon.resize((64, 64), Image.Resampling.NEAREST), (i * cell + 8, 8))
    review.save(ROOT / "sublimado_mascaras_visual_review.png")


def main():
    img = Image.open(SOURCE).convert("RGBA")
    boxes = components(img)
    if len(boxes) != len(NAMES):
        raise SystemExit(f"Expected {len(NAMES)} icons, found {len(boxes)}: {boxes}")
    for name, box in zip(NAMES, boxes):
        crop_icon(img, box).save(OUT / f"{name}.png")
    make_review()


if __name__ == "__main__":
    main()
