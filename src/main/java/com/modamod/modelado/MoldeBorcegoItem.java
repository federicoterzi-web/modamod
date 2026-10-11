package com.modamod.modelado;

import com.modamod.item.BorcegoBotamanga;
import com.modamod.item.BorcegoCana;
import com.modamod.item.BorcegoSuela;
import com.modamod.item.PrendaLore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Moldes de los borcegos (2026-10-08, "modelame unos borcegos y agreguemos un slot de calzado"): la altura de la caña
 * (baja, media, alta), la suela (chata o de plataforma) y cómo se lleva el pantalón (botamanga por dentro o por fuera)
 * son moldes de la categoría Borcegos de la Modeladora, cada uno en su pin. Exclusivos de los borcegos.
 */
public class MoldeBorcegoItem extends Item {

    public enum Tipo {
        CANA_BAJA(BorcegoCana.BAJA, null, null), CANA_MEDIA(BorcegoCana.MEDIA, null, null),
        CANA_ALTA(BorcegoCana.ALTA, null, null),
        SUELA_CHATA(null, BorcegoSuela.CHATA, null), SUELA_PLATAFORMA(null, BorcegoSuela.PLATAFORMA, null),
        BOTAMANGA_ADENTRO(null, null, BorcegoBotamanga.ADENTRO), BOTAMANGA_AFUERA(null, null, BorcegoBotamanga.AFUERA);

        public final BorcegoCana cana;
        public final BorcegoSuela suela;
        public final BorcegoBotamanga botamanga;

        Tipo(BorcegoCana cana, BorcegoSuela suela, BorcegoBotamanga botamanga) {
            this.cana = cana;
            this.suela = suela;
            this.botamanga = botamanga;
        }

        public boolean esCana() { return cana != null; }
        public boolean esSuela() { return suela != null; }
        public boolean esBotamanga() { return botamanga != null; }
    }

    public final Tipo tipo;

    public MoldeBorcegoItem(Settings settings, Tipo tipo) {
        super(settings);
        this.tipo = tipo;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        PrendaLore.molde(tooltip, "borcegos", PrendaLore.Maquina.MODELADORA, "borcegos");
        tooltip.add(Text.translatable("modamod.sublimadora.molde.ayuda").formatted(Formatting.DARK_GRAY));
    }
}
