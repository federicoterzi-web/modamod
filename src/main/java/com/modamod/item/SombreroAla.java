package com.modamod.item;

import net.minecraft.util.StringIdentifiable;

/** Ala del sombrero de bruja (2026-10-04). Por ordinal: nuevos al final. */
public enum SombreroAla implements StringIdentifiable {
    ANCHA("ancha", 19f),
    CORTA("corta", 13f),
    /** Vincha (2026-10-11, "la cofia o vincha se puede adaptar al sombrero"): un arco sobre la cabeza con un moño; sin cono. */
    VINCHA("vincha", 0f),
    /** Cofia de maid: una tira de tela al frente de la cabeza con volado y un moño; sin cono. */
    COFIA("cofia", 0f);

    public final String clave;
    /** Lado del ala en píxeles del modelo. */
    public final float lado;

    SombreroAla(String clave, float lado) {
        this.clave = clave;
        this.lado = lado;
    }

    /** ¿Es una vincha o una cofia (sin cono ni ala)? */
    public boolean esDiadema() { return this == VINCHA || this == COFIA; }

    @Override
    public String asString() { return clave; }

    public String traduccion() { return "modamod.sombrero.ala." + clave; }
}
