"""Limpieza del botón físico de línea de producción en las 5 máquinas automáticas (2026-10-08).

Idempotente: saca los huesos físicos `boton_linea` y `led_linea` si todavía quedaron de una pasada anterior.
La línea ahora se indica con el LED amarillo existente del panel; el click lo maneja util/BotonLinea.
Uso: python tools/agregar_boton_linea.py
"""
import json
import os

RAIZ = os.path.join(os.path.dirname(__file__), "..")
RES = os.path.join(RAIZ, "src", "main", "resources", "assets", "modamod")

MAQUINAS = ("garment_shaper", "dye_station", "sublimator", "estilista", "telar")


def main():
    for geo_nombre in MAQUINAS:
        ruta = os.path.join(RES, "geo", geo_nombre + ".geo.json")
        data = json.load(open(ruta, encoding="utf-8"))
        g = data["minecraft:geometry"][0]
        g["bones"] = [b for b in g["bones"] if b["name"] not in ("boton_linea", "led_linea")]
        json.dump(data, open(ruta, "w", encoding="utf-8"), indent=2)
        print("ok", geo_nombre)


if __name__ == "__main__":
    main()
