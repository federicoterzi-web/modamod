package com.modamod.item;

import com.modamod.garment.Parte;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Borcegos (2026-10-08, "modelame unos borcegos y agreguemos un slot de calzado"): un par de botas de cuero con
 * cordones y suela gruesa, en el slot de Trinkets {@code socks/calzado}. Como el sombrero y la banda NO es ropa de
 * cuerpo ({@code Garments.esPrenda} falso) y tiene 3 zonas con color y dibujo (cuero, suela, cordones) que se ponen en
 * la Estilista, que también le pone apliques y correas. La altura de la caña, la suela y cómo se lleva el pantalón son
 * componentes que se eligen con los moldes de la Modeladora. La dibuja {@link com.modamod.render.BorcegosRenderer}.
 */
public class BorcegosItem extends TrinketItem implements ZonasTenibles {

    /** Cuero marrón oscuro, suela casi negra y cordones crema de fábrica. */
    public static final List<Integer> DE_FABRICA = List.of(0x5A3A24, 0x2E2824, 0xD9CDB0);

    public BorcegosItem(Settings settings) {
        super(settings);
    }

    public static BorcegoCana cana(ItemStack stack) {
        BorcegoCana c = stack.get(ModamodComponents.BORCEGO_CANA);
        return c == null ? BorcegoCana.MEDIA : c;
    }

    public static BorcegoSuela suela(ItemStack stack) {
        BorcegoSuela s = stack.get(ModamodComponents.BORCEGO_SUELA);
        return s == null ? BorcegoSuela.CHATA : s;
    }

    public static BorcegoBotamanga botamanga(ItemStack stack) {
        BorcegoBotamanga b = stack.get(ModamodComponents.BORCEGO_BOTAMANGA);
        return b == null ? BorcegoBotamanga.ADENTRO : b;
    }

    public static void setCana(ItemStack stack, BorcegoCana v) {
        if (v == BorcegoCana.MEDIA) stack.remove(ModamodComponents.BORCEGO_CANA);
        else stack.set(ModamodComponents.BORCEGO_CANA, v);
    }

    public static void setSuela(ItemStack stack, BorcegoSuela v) {
        if (v == BorcegoSuela.CHATA) stack.remove(ModamodComponents.BORCEGO_SUELA);
        else stack.set(ModamodComponents.BORCEGO_SUELA, v);
    }

    public static void setBotamanga(ItemStack stack, BorcegoBotamanga v) {
        if (v == BorcegoBotamanga.ADENTRO) stack.remove(ModamodComponents.BORCEGO_BOTAMANGA);
        else stack.set(ModamodComponents.BORCEGO_BOTAMANGA, v);
    }

    public static List<Integer> colores(ItemStack stack) {
        List<Integer> c = stack.get(ModamodComponents.COLORES_BORCEGOS);
        return c == null || c.size() < 3 ? DE_FABRICA : c;
    }

    public static List<SombreroPatron> patrones(ItemStack stack) {
        List<Integer> p = stack.get(ModamodComponents.PATRONES_BORCEGOS);
        SombreroPatron[] v = SombreroPatron.values();
        List<SombreroPatron> out = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int o = p != null && i < p.size() ? p.get(i) : 0;
            out.add(o >= 0 && o < v.length ? v[o] : SombreroPatron.LISO);
        }
        return out;
    }

    @Override public List<Integer> coloresDe(ItemStack stack) { return colores(stack); }

    @Override
    public ItemStack conColoresDe(ItemStack stack, int a, int b, int c) {
        stack.set(ModamodComponents.COLORES_BORCEGOS, List.of(a, b, c));
        return stack;
    }

    @Override public List<SombreroPatron> patronesDe(ItemStack stack) { return patrones(stack); }

    @Override
    public ItemStack conPatronDe(ItemStack stack, int zona, SombreroPatron patron) {
        List<SombreroPatron> actuales = patrones(stack);
        int[] n = {actuales.get(0).ordinal(), actuales.get(1).ordinal(), actuales.get(2).ordinal()};
        n[zona] = patron.ordinal();
        if (n[0] == 0 && n[1] == 0 && n[2] == 0) stack.remove(ModamodComponents.PATRONES_BORCEGOS);
        else stack.set(ModamodComponents.PATRONES_BORCEGOS, List.of(n[0], n[1], n[2]));
        return stack;
    }

    @Override public String claveZona(ItemStack stack, int i) { return "modamod.borcegos.parte." + (i + 1); }

    /** Los apliques y las correas van en el borcego derecho (el izquierdo copia al derecho sin ellos). */
    @Override
    public Parte marco(ItemStack stack) {
        return Parte.PIERNA_DER;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("modamod.borcegos.tooltip", Text.translatable(cana(stack).traduccion()),
                Text.translatable(suela(stack).traduccion()), Text.translatable(botamanga(stack).traduccion()))
                .formatted(Formatting.GRAY));
        List<Integer> c = colores(stack);
        for (int i = 0; i < 3; i++) {
            int color = c.get(i);
            tooltip.add(Text.translatable("modamod.borcegos.parte." + (i + 1)).formatted(Formatting.GRAY)
                    .append(Text.literal(" ■■■").styled(s -> s.withColor(TextColor.fromRgb(color)))));
        }
    }
}
