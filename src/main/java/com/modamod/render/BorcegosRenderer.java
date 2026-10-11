package com.modamod.render;

import com.modamod.item.BorcegoCana;
import com.modamod.item.BorcegoSuela;
import com.modamod.item.BorcegosItem;
import com.modamod.item.SombreroPatron;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Borcegos (2026-10-08, "modelame unos borcegos y agreguemos un slot de calzado"): cajas en el marco de cada pierna
 * (el pie es el fondo de la caja de la pierna: y 12 es el piso, y positivo es hacia abajo, el frente es -Z), con 3 zonas
 * de textura lisa: cuero (caña y puntera), suela (una losa más ancha con la puntera salida y el talón atrás) y
 * cordones (una lengüeta y barritas cruzadas al frente de la caña). Las medidas NO dependen de la ropa: el pantalón
 * se adapta a ellos ({@link GarmentFeatureRenderer}: por dentro se corta donde empieza la caña, por fuera se infla
 * sobre ella), así los apliques y las correas siguen cayendo sobre las mismas cajas. Los apliques y las correas van en
 * el borcego derecho ({@link BorcegosItem#marco}).
 */
public final class BorcegosRenderer {

    private BorcegosRenderer() {}

    /** Lo que sale la caña de la piel de la pierna (px). */
    public static final float T = 0.5f;
    /** Lo que sale de más la suela a los lados y atrás, y la puntera adelante. */
    private static final float SUELA_LADO = 0.3f, SUELA_TALON = 0.5f, SUELA_PUNTA = 1.3f, PUNTERA = 1.0f;
    private static final float ALTO_PUNTERA = 2.2f;

    public static void dibujar(ItemStack borcegos, BipedEntityModel<?> biped, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int luz) {
        List<Integer> colores = BorcegosItem.colores(borcegos);
        List<SombreroPatron> patrones = BorcegosItem.patrones(borcegos);
        List<SombreroRenderer.Caja> cajas = cajas(borcegos);
        for (ModelPart marco : new ModelPart[] {biped.rightLeg, biped.leftLeg}) {
            if (!marco.visible) continue;
            for (int z = 0; z < 3; z++) {
                ModelPart parte = parte(cajas, z, borcegos);
                if (parte == null) continue;
                GarmentFeatureRenderer.dibujarModelPart(parte, CuerpoGeometria.Superficie.CUERPO,
                        SombreroRenderer.textura(colores.get(z), patrones.get(z)), marco, matrices, vertexConsumers, luz);
            }
        }
        // Apliques y correas: en el borcego derecho (el punto es del marco de la pierna derecha).
        ApliqueRenderer.dibujar(borcegos, 0f, biped, matrices, vertexConsumers, luz);
    }

    private static final Map<String, ModelPart> CACHE = new HashMap<>();

    private static ModelPart parte(List<SombreroRenderer.Caja> cajas, int zona, ItemStack borcegos) {
        String key = BorcegosItem.cana(borcegos) + "|" + BorcegosItem.suela(borcegos) + "|" + zona;
        ModelPart c = CACHE.get(key);
        if (c != null) return c;
        List<ModelPart.Cuboid> cubos = new ArrayList<>();
        for (SombreroRenderer.Caja caja : cajas) {
            if (caja.zona() != zona) continue;
            float[] a = caja.min(), b = caja.max();
            cubos.add(SombreroRenderer.caja(a[0], a[1], a[2], b[0] - a[0], b[1] - a[1], b[2] - a[2]));
        }
        if (cubos.isEmpty()) return null;
        c = new ModelPart(cubos, Map.of());
        CACHE.put(key, c);
        return c;
    }

    /** Las cajas de un borcego en el marco de la pierna (el izquierdo es igual): para dibujar y para apuntar con el mouse. */
    public static List<SombreroRenderer.Caja> cajas(ItemStack borcegos) {
        BorcegoCana cana = BorcegosItem.cana(borcegos);
        BorcegoSuela suela = BorcegosItem.suela(borcegos);
        float piso = 12f, sf = suela.filas;
        float arriba = piso - cana.alto;
        float ancho = 2f + T, fondo = 2f + T;
        List<SombreroRenderer.Caja> out = new ArrayList<>();
        // Cuero: la caña y, adelante, la puntera.
        out.add(caja(0, -ancho, arriba, -fondo, ancho, piso - sf, fondo));
        out.add(caja(0, -ancho + 0.2f, piso - sf - ALTO_PUNTERA, -fondo - PUNTERA, ancho - 0.2f, piso - sf, -fondo));
        // Suela: más ancha, con la puntera salida adelante y el talón atrás.
        out.add(caja(1, -ancho - SUELA_LADO, piso - sf, -fondo - SUELA_PUNTA, ancho + SUELA_LADO, piso, fondo + SUELA_TALON));
        // Cordones: la lengüeta en el frente de la caña y barritas cruzadas.
        float yTop = arriba + 0.3f, yBase = piso - sf - 0.2f;
        out.add(caja(2, -1.0f, yTop, -fondo - 0.3f, 1.0f, yBase, -fondo));
        for (float y = arriba + 0.6f; y + 0.35f <= piso - sf - 0.4f; y += 1.6f) {
            out.add(caja(2, -1.8f, y, -fondo - 0.5f, 1.8f, y + 0.35f, -fondo - 0.3f));
        }
        return out;
    }

    private static SombreroRenderer.Caja caja(int zona, float x0, float y0, float z0, float x1, float y1, float z1) {
        return new SombreroRenderer.Caja(zona, new float[] {x0, y0, z0}, new float[] {x1, y1, z1});
    }
}
