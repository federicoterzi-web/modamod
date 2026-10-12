package com.modamod.client;

import com.modamod.telar.TelarBlockEntity;
import com.modamod.telar.TelarPrenda;
import com.modamod.telar.TelarScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * El Telar automático (2026-10-07, "produzca prendas basicas a base de lana e hilo" + "se pueda seleccionar varias
 * prendas y vaya alternando, y se le pueda configurar que cantidad de prenda se quiere que saque"): fondo, marcos y
 * barra de progreso dibujados por código (sin texturas), con el acento granate del Telar (2026-10-07, "el color del telar es granate"). A la izquierda
 * lana e hilo, en el medio las seis prendas (se tildan varias) y el lote, a la derecha la prenda terminada; abajo el
 * inventario.
 */
public class TelarScreen extends net.minecraft.client.gui.screen.ingame.HandledScreen<TelarScreenHandler> {

    // Las prendas son íconos tildables (13 entran en dos filas de 7); la cabecera tiene tres insumos a la izquierda.
    private static final int LADO_ICONO = 22, PASO_ICONO = 28, POR_FILA = 7, Y_BOTONES = 92, X_BOTONES = 14;
    private static final int Y_LOTE = 148, X_BARRA = 62, Y_BARRA = 30, ANCHO_BARRA = 124, X_TEXTO = 62, ANCHO_TEXTO = 126;
    private final List<ButtonWidget> botones = new ArrayList<>();

    public TelarScreen(TelarScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = TelarScreenHandler.ANCHO;
        this.backgroundHeight = TelarScreenHandler.ALTO;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = TelarScreenHandler.X_INV;
        this.playerInventoryTitleY = TelarScreenHandler.Y_INV - 12;
    }

    @Override
    protected void init() {
        super.init();
        EstiloPergamino.usarTema(EstiloPergamino.Tema.GRANATE);
        botones.clear();
        TelarPrenda[] prendas = TelarPrenda.values();
        for (int i = 0; i < prendas.length; i++) {
            int bx = this.x + X_BOTONES + (i % POR_FILA) * PASO_ICONO, by = this.y + Y_BOTONES + (i / POR_FILA) * PASO_ICONO;
            int idx = i;
            BotonPrenda b = new BotonPrenda(bx, by, prendas[i],
                    () -> handler.host.elegida(idx),
                    btn -> this.client.interactionManager.clickButton(this.handler.syncId, TelarBlockEntity.BTN_PRENDA_BASE + idx));
            this.addDrawableChild(b);
        }
        // El lote: −10 − + +10 y reiniciar.
        int ly = this.y + Y_LOTE;
        boton("-10", this.x + 40, ly, 24, 16, TelarBlockEntity.BTN_LOTE_MENOS_10);
        boton("-", this.x + 66, ly, 16, 16, TelarBlockEntity.BTN_LOTE_MENOS);
        boton("+", this.x + 84, ly, 16, 16, TelarBlockEntity.BTN_LOTE_MAS);
        boton("+10", this.x + 102, ly, 24, 16, TelarBlockEntity.BTN_LOTE_MAS_10);
        this.addDrawableChild(new EstiloPergamino.BotonLinea(this.x + TelarScreenHandler.ANCHO - 8 - 64, this.y + 4, 64, 14,
                () -> handler.host.linea(),
                b -> this.client.interactionManager.clickButton(this.handler.syncId, TelarBlockEntity.BTN_LINEA)));
        ButtonWidget reiniciar = boton("↻", this.x + 128, ly, 18, 16, TelarBlockEntity.BTN_LOTE_REINICIAR);
        reiniciar.setTooltip(Tooltip.of(Text.translatable("modamod.telar.tooltip.reiniciar")));
    }

    private ButtonWidget boton(String texto, int bx, int by, int w, int h, int id) {
        ButtonWidget b = new EstiloPergamino.BotonPergamino(bx, by, w, h, Text.literal(texto),
                btn -> this.client.interactionManager.clickButton(this.handler.syncId, id));
        this.addDrawableChild(b);
        return b;
    }

    /** Un ícono de prenda que se tilda: marco claro si está elegida, tooltip con el nombre y el costo. */
    private static final class BotonPrenda extends ButtonWidget {
        private final TelarPrenda prenda;
        private final java.util.function.BooleanSupplier elegida;
        private ItemStack icono;

        BotonPrenda(int x, int y, TelarPrenda prenda, java.util.function.BooleanSupplier elegida, PressAction accion) {
            super(x, y, LADO_ICONO, LADO_ICONO, Text.empty(), accion, DEFAULT_NARRATION_SUPPLIER);
            this.prenda = prenda;
            this.elegida = elegida;
            Text nombre = Text.translatable("modamod.telar.prenda." + prenda.clave());
            setTooltip(Tooltip.of(Text.empty().append(nombre).append("\n")
                    .append(Text.translatable("modamod.telar.tooltip.prenda", prenda.lana, prenda.hilo, prenda.cuero))));
        }

        @Override
        protected void renderWidget(DrawContext c, int mx, int my, float delta) {
            if (icono == null) icono = prenda.crear(0xFFFFFF);
            boolean on = elegida.getAsBoolean();
            int x0 = getX(), y0 = getY();
            c.fill(x0 - 1, y0 - 1, x0 + width + 1, y0 + height + 1, on ? EstiloPergamino.tema().claro : 0xFF2A180C);
            c.fill(x0, y0, x0 + width, y0 + height, isHovered() ? 0xFFB59B6A : on ? 0xFFD9C08C : 0xFF8B7355);
            c.drawItem(icono, x0 + 3, y0 + 3);
            if (on) c.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "✔",
                    x0 + width - 7, y0 + height - 9, 0xFF3F7A38, false);
        }
    }

    @Override
    protected void drawBackground(DrawContext c, float delta, int mx, int my) {
        int x0 = this.x, y0 = this.y, w = backgroundWidth, h = backgroundHeight;
        var tema = EstiloPergamino.tema();
        // Pergamino con borde oscuro, filete del acento y esquinas en el acento (como las máquinas hermanas).
        c.fill(x0 - 2, y0 - 2, x0 + w + 2, y0 + h + 2, 0xFF2A180C);
        c.fill(x0 - 1, y0 - 1, x0 + w + 1, y0 + h + 1, tema.claro);
        c.fill(x0, y0, x0 + w, y0 + h, 0xFFE9D8B4);
        for (int i = 0; i < 9; i++) {
            c.fill(x0, y0 + i, x0 + 9 - i, y0 + i + 1, tema.claro);
            c.fill(x0 + w - 9 + i, y0, x0 + w, y0 + i + 1, tema.claro);
            c.fill(x0, y0 + h - 1 - i, x0 + 9 - i, y0 + h - i, tema.claro);
            c.fill(x0 + w - 9 + i, y0 + h - 1 - i, x0 + w, y0 + h - i, tema.claro);
        }
        // Línea de puntos entre la cabecera y los controles.
        for (int px = x0 + 8; px < x0 + w - 8; px += 4) c.fill(px, y0 + 82, px + 2, y0 + 83, tema.oscuro);
        // Marcos de los slots.
        marco(c, x0 + TelarScreenHandler.X_LANA, y0 + TelarScreenHandler.Y_LANA);
        marco(c, x0 + TelarScreenHandler.X_HILO, y0 + TelarScreenHandler.Y_HILO);
        marco(c, x0 + TelarScreenHandler.X_CUERO, y0 + TelarScreenHandler.Y_CUERO);
        marco(c, x0 + TelarScreenHandler.X_SALIDA, y0 + TelarScreenHandler.Y_SALIDA);
        int yi = y0 + TelarScreenHandler.Y_INV, xi = x0 + TelarScreenHandler.X_INV;
        for (int f = 0; f < 3; f++) for (int col = 0; col < 9; col++) marco(c, xi + col * 18, yi + f * 18);
        for (int col = 0; col < 9; col++) marco(c, xi + col * 18, yi + 58);
        // Rótulos de la cabecera: Entrada → barra → Salida.
        TelarBlockEntity be = handler.host;
        contador(c, "modamod.telar.lana", be.lana(), x0 + 32, y0 + TelarScreenHandler.Y_LANA + 4);
        contador(c, "modamod.telar.hilo", be.hilo(), x0 + 32, y0 + TelarScreenHandler.Y_HILO + 4);
        contador(c, "modamod.telar.cuero", be.cuero(), x0 + 32, y0 + TelarScreenHandler.Y_CUERO + 4);
        c.drawText(textRenderer, Text.translatable("modamod.telar.lote"), x0 + 8, y0 + Y_LOTE + 4, EstiloPergamino.TEXTO, false);
        // Barra de progreso de la prenda en curso.
        float nivel = switch (be.estado()) {
            case PROCESANDO -> be.progreso() / (float) be.duracion();
            case LISTO -> 1f;
            default -> 0f;
        };
        int bx = x0 + X_BARRA, by = y0 + Y_BARRA, bw = ANCHO_BARRA;
        c.fill(bx - 1, by - 1, bx + bw + 1, by + 7, 0xFF2A180C);
        c.fill(bx, by, bx + bw, by + 6, 0xFF111111);
        c.fill(bx, by, bx + (int) (bw * nivel), by + 6, 0xFFB3283F);
        // Estado de la cabecera, recortado al ancho del medio: lote, la que sigue y si alcanza.
        Text lote = be.lote() == 0 ? Text.translatable("modamod.telar.lote_sin_limite")
                : Text.translatable("modamod.telar.lote_n", be.lote(), be.restantes());
        linea(c, lote, x0 + X_TEXTO, y0 + Y_BARRA + 12, EstiloPergamino.TEXTO);
        int v = be.prendaVista();
        Text sigue = v < 0 ? Text.translatable("modamod.telar.nada_tildado")
                : Text.translatable("modamod.telar.sigue", Text.translatable("modamod.telar.prenda." + TelarPrenda.values()[v].clave()),
                        TelarPrenda.values()[v].lana, TelarPrenda.values()[v].hilo, TelarPrenda.values()[v].cuero);
        linea(c, sigue, x0 + X_TEXTO, y0 + Y_BARRA + 22, EstiloPergamino.TEXTO);
        if (be.estado() == TelarBlockEntity.Estado.REPOSO && v >= 0) {
            TelarPrenda p = TelarPrenda.values()[v];
            int faltaLana = Math.max(0, p.lana - be.lana());
            int faltaHilo = Math.max(0, p.hilo - be.hilo());
            int faltaCuero = Math.max(0, p.cuero - be.cuero());
            boolean lista = be.creativa() || (faltaLana == 0 && faltaHilo == 0 && faltaCuero == 0);
            Text estado = lista
                    ? Text.translatable("modamod.telar.estado.listo")
                    : Text.translatable("modamod.telar.estado.falta", faltaLana, faltaHilo, faltaCuero);
            linea(c, estado, x0 + X_TEXTO, y0 + Y_BARRA + 32, lista ? 0xFF3F7A38 : 0xFF8A2E2E);
        }
    }

    private void contador(DrawContext c, String clave, int n, int tx, int ty) {
        c.drawText(textRenderer, Text.translatable(clave).append(": " + n + "/64"), tx, ty, EstiloPergamino.TEXTO, false);
    }

    /** Una línea de texto recortada al ancho del medio de la cabecera (sin pisar el slot de Salida). */
    private void linea(DrawContext c, Text t, int tx, int ty, int color) {
        c.drawText(textRenderer, net.minecraft.util.Language.getInstance().reorder(textRenderer.trimToWidth(t, ANCHO_TEXTO)),
                tx, ty, color, false);
    }

    private void marco(DrawContext c, int sx, int sy) {
        c.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF2A180C);
        c.fill(sx, sy, sx + 16, sy + 16, 0xFF8B7355);
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        renderBackground(c, mx, my, delta);
        super.render(c, mx, my, delta);
        drawMouseoverTooltip(c, mx, my);
    }
}
