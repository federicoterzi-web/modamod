package com.modamod.item;

import net.minecraft.util.StringIdentifiable;

/** Altura de la caña de los borcegos (2026-10-08): px de pierna que cubre contando desde el piso. Por ordinal: nuevos al final. */
public enum BorcegoCana implements StringIdentifiable {
    BAJA("baja", 4),
    MEDIA("media", 6),
    ALTA("alta", 9);

    public final String clave;
    public final int alto;

    BorcegoCana(String clave, int alto) {
        this.clave = clave;
        this.alto = alto;
    }

    @Override
    public String asString() { return clave; }

    public String traduccion() { return "modamod.borcegos.cana." + clave; }
}
