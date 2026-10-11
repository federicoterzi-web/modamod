"""Ícono de los borcegos (2026-10-08, "modelame unos borcegos y agreguemos un slot de calzado"): una bota de caña media
con cordones y suela gruesa, de lado, dibujada por código en la tinta marrón del "taller de sastrería". Escribe
`textures/item/borcegos.png` (64x64) y el ícono gris del slot `textures/gui/slot/calzado.png` (16x16).

Uso: python tools/generar_icono_borcegos.py
"""
import os
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, os.path.dirname(__file__))
from generar_icono_banda import _slot, ORO, ORO_LUZ, TINTA, CUERO, CUERO_LUZ   # noqa: E402

RAIZ = Path(__file__).resolve().parent.parent / "src/main/resources/assets/modamod/textures"
SUELA = (52, 44, 40, 255)
SUELA_LUZ = (92, 82, 76, 255)
SS = 8


def item():
    k = SS
    im = Image.new("RGBA", (64 * k, 64 * k), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    g = 2 * k
    # silueta (caña + empeine + puntera), mirando a la izquierda
    bota = [(30, 8), (50, 8), (50, 38), (58, 46), (58, 52), (8, 52), (8, 44), (14, 40), (24, 38), (30, 30)]
    bota = [(x * k, y * k) for x, y in bota]
    d.polygon([(x + (g if x > 32 * k else -g), y + (g if y > 30 * k else -g)) for x, y in bota], fill=TINTA)
    d.polygon(bota, fill=CUERO)
    d.rectangle((30 * k, 8 * k, 50 * k, 12 * k), fill=CUERO_LUZ)           # borde de la caña
    d.polygon([(8 * k, 44 * k), (14 * k, 40 * k), (24 * k, 38 * k), (26 * k, 44 * k)], fill=CUERO_LUZ)   # puntera
    # suela gruesa
    d.rectangle((6 * k, 48 * k, 60 * k, 56 * k), fill=SUELA)
    d.rectangle((6 * k, 48 * k, 60 * k, 50 * k), fill=SUELA_LUZ)
    # cordones: tiras cruzadas sobre el empeine y la caña
    for y in range(14, 40, 5):
        d.rectangle((30 * k, y * k, 40 * k, (y + 2) * k), fill=ORO)
        d.rectangle((30 * k, y * k, 40 * k, (y + 0.8) * k), fill=ORO_LUZ)
    d.rectangle((33 * k, 12 * k, 37 * k, 40 * k), fill=TINTA)
    for y in range(14, 40, 5):
        d.ellipse((30 * k, y * k, 32 * k, (y + 2) * k), fill=ORO_LUZ)
        d.ellipse((38 * k, y * k, 40 * k, (y + 2) * k), fill=ORO_LUZ)
    return im.resize((64, 64), Image.LANCZOS)


def calzado(d, k):
    d.rectangle((9 * k, 2 * k, 13 * k, 10 * k), fill=255)             # caña
    d.rectangle((3 * k, 8 * k, 13 * k, 12 * k), fill=255)             # empeine
    d.rectangle((2 * k, 11 * k, 14 * k, 14 * k), fill=255)            # suela
    d.rectangle((10 * k, 3 * k, 12 * k, 8 * k), fill=0)               # hueco de la caña


if __name__ == "__main__":
    item().save(RAIZ / "item/borcegos.png")
    _slot(calzado).save(RAIZ / "gui/slot/calzado.png")
    print("ok")
