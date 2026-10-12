package com.modamod.receta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.modamod.item.TopCorte;
import com.modamod.sublimadora.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

/**
 * Hoodie sin forma: una remera + lanas (2026-10-12, planilla de recetas). Sale con el corte de fábrica del hoodie
 * y el color promedio de las lanas y de la remera (si estaba teñida).
 */
public class RecetaTenidaSinForma extends ShapelessRecipe {
    final List<Ingredient> lista;

    public RecetaTenidaSinForma(List<Ingredient> lista) {
        super("", CraftingRecipeCategory.EQUIPMENT, TopCorte.hoodie(), listaDefecto(lista));
        this.lista = lista;
    }

    private static DefaultedList<Ingredient> listaDefecto(List<Ingredient> l) {
        DefaultedList<Ingredient> d = DefaultedList.of();
        d.addAll(l);
        return d;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput in, RegistryWrapper.WrapperLookup registros) {
        int extra = -1;
        for (ItemStack s : in.getStacks()) {
            if (s.isOf(ModItems.REMERA)) {
                DyedColorComponent c = s.get(DataComponentTypes.DYED_COLOR);
                if (c != null) extra = c.rgb();
            }
        }
        ItemStack r = TopCorte.hoodie();
        r.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(ModamodRecetas.colorDeLanas(in, extra), true));
        return r;
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModamodRecetas.TENIDA_SIN_FORMA; }

    public static class Serializer implements RecipeSerializer<RecetaTenidaSinForma> {
        private static final MapCodec<RecetaTenidaSinForma> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.lista)
        ).apply(i, RecetaTenidaSinForma::new));
        private static final PacketCodec<RegistryByteBuf, RecetaTenidaSinForma> PAQUETE =
                Ingredient.PACKET_CODEC.collect(net.minecraft.network.codec.PacketCodecs.toList())
                        .xmap(RecetaTenidaSinForma::new, r -> r.lista).cast();

        @Override public MapCodec<RecetaTenidaSinForma> codec() { return CODEC; }
        @Override public PacketCodec<RegistryByteBuf, RecetaTenidaSinForma> packetCodec() { return PAQUETE; }
    }
}
