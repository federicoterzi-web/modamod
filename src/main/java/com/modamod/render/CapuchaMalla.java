package com.modamod.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * La capucha del hoodie y de la capa como malla de tela (2026-10-12, "podemos hacer la capucha de malla y mas
 * amplia?"): reemplaza a las cajas de {@link CuelloYCapucha}. Textura lisa de 64x64 de {@code texturaLisa}: el
 * exterior es u 0..32 y el forro (más oscuro) u 32..64, que se usa en el borde que enmarca la cara y en la boca de
 * la capucha caída. Coordenadas en px del marco de la parte, y hacia abajo.
 */
final class CapuchaMalla {
    private CapuchaMalla() {}

    // Anillos de la capucha puesta, de abajo (cuello) a la coronilla, en el marco de la cabeza (y −8..0 es la cabeza):
    // {y, semieje x, profundidad hacia el frente, profundidad hacia atrás, corrimiento hacia atrás, caída de los costados}.
    // 2026-10-12, "para la puesta queria yo para que se vea mas natural" → nada de cúpula perfecta: abajo se pega al
    // cuello y cae sobre los hombros, en el medio se abomba atrás como bolsa y la coronilla cae hacia atrás en punta.
    private static final float[][] ANILLOS = {
        {2.6F, 4.9F, 4.3F, 3.7F, 0.0F, 1.9F},
        {0.6F, 5.3F, 4.7F, 4.7F, 0.0F, 0.7F},
        {-2.4F, 5.6F, 5.0F, 6.1F, 0.2F, 0.0F},
        {-5.4F, 5.6F, 5.1F, 6.8F, 0.4F, 0.0F},
        {-8.0F, 5.2F, 4.9F, 6.6F, 0.8F, 0.0F},
        {-10.0F, 4.0F, 4.0F, 5.4F, 1.5F, 0.0F},
        {-11.3F, 2.1F, 2.2F, 3.2F, 2.4F, 0.0F},
        {-11.5F, 0.0F, 0.0F, 0.0F, 3.4F, 0.0F},
    };
    private static final int SEG = 40, SUBFILAS = 3;
    /** La cara queda por una elipse de este tamaño (px) centrada en y −4: lo de la capucha que cae adentro se corre al borde. */
    private static final float ABRE_X = 4.2F, ABRE_Y = 4.9F, ABRE_C = -4F;

    /** Capucha puesta, en el marco de la cabeza. {@code d} = holgura del top. */
    static void puesta(Identifier tela, float d, ModelPart cabeza, MatrixStack matrices, VertexConsumerProvider vcp, int luz) {
        float inf = 0.75F + d * 0.25F;
        VertexConsumer vc = vcp.getBuffer(ClothingTextureCache.capaDeRender(tela));
        matrices.push();
        cabeza.rotate(matrices);
        Matrix4f m = matrices.peek().getPositionMatrix();
        Matrix3f nm = matrices.peek().getNormalMatrix();
        int filas = (ANILLOS.length - 1) * SUBFILAS;
        Vector3f[][] p = new Vector3f[filas + 1][SEG + 1];
        boolean[][] corrido = new boolean[filas + 1][SEG + 1];
        for (int j = 0; j <= filas; j++) {
            float f = j / (float) SUBFILAS;
            int a = Math.min(ANILLOS.length - 2, (int) f);
            float t = f - a;
            // Interpolación suave entre anillos (la coronilla se redondea).
            float s = t * t * (3F - 2F * t);
            float y = lerp(ANILLOS[a][0], ANILLOS[a + 1][0], t);
            float ax = lerp(ANILLOS[a][1], ANILLOS[a + 1][1], s) + inf;
            float zf = lerp(ANILLOS[a][2], ANILLOS[a + 1][2], s) + inf;
            float zb = lerp(ANILLOS[a][3], ANILLOS[a + 1][3], s) + inf;
            float dz = lerp(ANILLOS[a][4], ANILLOS[a + 1][4], s);
            float cae = lerp(ANILLOS[a][5], ANILLOS[a + 1][5], s);
            for (int k = 0; k <= SEG; k++) {
                // φ = 0 al frente (−z), π atrás; superelipse para que no sea un óvalo perfecto.
                double phi = 2 * Math.PI * k / SEG;
                float sn = (float) Math.sin(phi), cs = (float) Math.cos(phi);
                float x = ax * super_(sn);
                // cs > 0 = mitad del frente (z negativo), cs < 0 = mitad de atrás (z positivo).
                // Los costados caen sobre los hombros y la coronilla se corre hacia atrás (cae en punta).
                float yy = y + cae * (float) Math.pow(Math.abs(sn), 1.5);
                float zz = (cs >= 0F ? -zf * super_(cs) : zb * super_(-cs)) + dz;
                p[j][k] = new Vector3f(x, yy, zz);
                // Lo que cae adentro de la abertura de la cara se corre al borde de la elipse.
                if (p[j][k].z < -0.5F) {
                    float ex = p[j][k].x / ABRE_X, ey = (p[j][k].y - ABRE_C) / ABRE_Y;
                    float r2 = ex * ex + ey * ey;
                    if (r2 < 1F) {
                        float r = (float) Math.sqrt(r2);
                        float k2 = r < 1e-4F ? 0F : 1F / r;
                        if (r < 1e-4F) { ex = 0F; ey = -1F; k2 = 1F; }
                        p[j][k].x = ex * k2 * ABRE_X;
                        p[j][k].y = ey * k2 * ABRE_Y + ABRE_C;
                        corrido[j][k] = true;
                    }
                }
            }
        }
        for (int j = 0; j < filas; j++) {
            for (int k = 0; k < SEG; k++) {
                Vector3f a = p[j][k], b = p[j][k + 1], c = p[j + 1][k + 1], e = p[j + 1][k];
                boolean todos = corrido[j][k] && corrido[j][k + 1] && corrido[j + 1][k + 1] && corrido[j + 1][k];
                if (todos) continue;
                boolean borde = corrido[j][k] || corrido[j][k + 1] || corrido[j + 1][k + 1] || corrido[j + 1][k];
                // Una banda de forro alrededor de la cara: los cuadros vecinos al borde.
                float u0 = borde ? 33F : 1F, u1 = borde ? 62F : 31F;
                float ua = (u0 + (u1 - u0) * k / SEG) / 64F, ub = (u0 + (u1 - u0) * (k + 1) / SEG) / 64F;
                float va = (1F + 30F * j / filas) / 64F, vb = (1F + 30F * (j + 1) / filas) / 64F;
                cuadro(m, nm, vc, luz, 0F, -4F, 0F, a, ua, va, b, ub, va, c, ub, vb, e, ua, vb);
            }
        }
        matrices.pop();
    }

    /** x ↦ x·|x|^(−0,35): empuja un seno/coseno hacia un cuadrado redondeado (exponente de superelipse ~2,6). */
    private static float super_(float v) {
        float a = Math.abs(v);
        if (a < 1e-5F) return 0F;
        return Math.signum(v) * (float) Math.pow(a, 2.0 / 2.6);
    }

    /**
     * Capucha caída sobre la espalda, en el marco del torso: una bolsa ancha que sale de la nuca y se abomba hacia
     * atrás, con la boca de forro arriba. {@code d} = holgura del top.
     */
    static void caida(Identifier tela, float d, ModelPart torso, MatrixStack matrices, VertexConsumerProvider vcp, int luz) {
        VertexConsumer vc = vcp.getBuffer(ClothingTextureCache.capaDeRender(tela));
        matrices.push();
        torso.rotate(matrices);
        Matrix4f m = matrices.peek().getPositionMatrix();
        Matrix3f nm = matrices.peek().getNormalMatrix();
        float atras = 2F + d + 0.2F;
        // {y, semiancho, cuánto sale de la espalda}
        float[][] perfil = {{-1.0F, 4.6F, 1.0F}, {0.4F, 5.4F, 2.2F}, {2.2F, 5.9F, 3.1F}, {4.2F, 5.5F, 3.2F}, {6.0F, 4.3F, 2.2F}, {7.4F, 2.6F, 0.6F}, {8.0F, 0.0F, 0.0F}};
        int filas = (perfil.length - 1) * SUBFILAS, cols = 24;
        Vector3f[][] p = new Vector3f[filas + 1][cols + 1];
        for (int j = 0; j <= filas; j++) {
            float f = j / (float) SUBFILAS;
            int a = Math.min(perfil.length - 2, (int) f);
            float t = f - a, s = t * t * (3F - 2F * t);
            float y = lerp(perfil[a][0], perfil[a + 1][0], t);
            float ax = lerp(perfil[a][1], perfil[a + 1][1], s);
            float sale = lerp(perfil[a][2], perfil[a + 1][2], s);
            for (int k = 0; k <= cols; k++) {
                float q = -1F + 2F * k / cols;                // −1..1 a lo ancho
                float forma = (float) Math.pow(Math.max(0F, 1F - Math.pow(Math.abs(q), 2.4)), 1.0 / 2.4);
                p[j][k] = new Vector3f(ax * q, y, atras + sale * forma);
            }
        }
        for (int j = 0; j < filas; j++) {
            for (int k = 0; k < cols; k++) {
                Vector3f a = p[j][k], b = p[j][k + 1], c = p[j + 1][k + 1], e = p[j + 1][k];
                // La primera franja es la boca: forro.
                boolean boca = j < SUBFILAS - 1;
                float u0 = boca ? 33F : 1F, u1 = boca ? 62F : 31F;
                float ua = (u0 + (u1 - u0) * k / cols) / 64F, ub = (u0 + (u1 - u0) * (k + 1) / cols) / 64F;
                float va = (1F + 30F * j / filas) / 64F, vb = (1F + 30F * (j + 1) / filas) / 64F;
                cuadro(m, nm, vc, luz, 0F, 3F, atras - 1F, a, ua, va, b, ub, va, c, ub, vb, e, ua, vb);
            }
        }
        matrices.pop();
    }

    private static float lerp(float a, float b, float t) { return a + (b - a) * t; }

    /** Un cuadro con la normal hacia afuera del eje {@code (cx, cy, cz)}. */
    private static void cuadro(Matrix4f m, Matrix3f nm, VertexConsumer vc, int luz, float cx, float cy, float cz,
                               Vector3f a, float ua, float va, Vector3f b, float ub, float vb,
                               Vector3f c, float uc, float vc2, Vector3f e, float ue, float ve) {
        float ux = b.x - a.x, uy = b.y - a.y, uz = b.z - a.z;
        float wx = e.x - a.x, wy = e.y - a.y, wz = e.z - a.z;
        float nx = uy * wz - uz * wy, ny = uz * wx - ux * wz, nz = ux * wy - uy * wx;
        if (nx * (a.x - cx) + ny * (a.y - cy) + nz * (a.z - cz) < 0F) { nx = -nx; ny = -ny; nz = -nz; }
        Vector3f n = new Vector3f(nx, ny, nz);
        if (n.lengthSquared() < 1e-10F) n.set(0F, -1F, 0F);
        n.normalize();
        Vector3f nn = nm.transform(n, new Vector3f()).normalize();
        vert(m, vc, luz, nn, a, ua, va);
        vert(m, vc, luz, nn, b, ub, vb);
        vert(m, vc, luz, nn, c, uc, vc2);
        vert(m, vc, luz, nn, e, ue, ve);
    }

    private static void vert(Matrix4f m, VertexConsumer vc, int luz, Vector3f nn, Vector3f p, float u, float v) {
        Vector3f w = m.transformPosition(p.x / 16F, p.y / 16F, p.z / 16F, new Vector3f());
        vc.vertex(w.x, w.y, w.z, 0xFFFFFFFF, u, v, OverlayTexture.DEFAULT_UV, luz, nn.x, nn.y, nn.z);
    }
}
