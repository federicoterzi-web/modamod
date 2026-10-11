package com.modamod.item;

import net.minecraft.util.StringIdentifiable;

/** Suela de los borcegos (2026-10-08): chata o de plataforma (más gruesa, con la puntera levantada). Por ordinal: nuevos al final. */
public enum BorcegoSuela implements StringIdentifiable {
    CHATA("chata", 1),
    PLATAFORMA("plataforma", 2);

    public final String clave;
    /** Filas de pierna (px) que ocupa la suela. */
    public final int filas;

    BorcegoSuela(String clave, int filas) {
        this.clave = clave;
        this.filas = filas;
    }

    @Override
    public String asString() { return clave; }

    public String traduccion() { return "modamod.borcegos.suela." + clave; }
}
