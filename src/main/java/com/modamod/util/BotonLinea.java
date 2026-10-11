package com.modamod.util;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/** Click sobre la zona de LEDs de las máquinas automáticas para alternar la línea. */
public final class BotonLinea {

    private BotonLinea() {}

    /** ¿El click cayó sobre la columna de tres LEDs del panel? {@code frente} es el FACING del bloque. */
    public static boolean golpea(Direction frente, BlockPos pos, BlockHitResult hit) {
        Direction cara = hit.getSide();
        if (cara != frente && cara != frente.getOpposite()) return false;
        double lx = hit.getPos().x - pos.getX();
        double lz = hit.getPos().z - pos.getZ();
        double lateral = switch (cara) {
            case NORTH -> lx;
            case SOUTH -> 1.0 - lx;
            case WEST -> 1.0 - lz;
            default -> lz;      // EAST
        };
        double xModelo = (0.5 - lateral) * 16.0;
        double yModelo = (hit.getPos().y - pos.getY()) * 16.0;
        boolean columnaDerecha = xModelo >= 5.3 && xModelo <= 7.0;
        boolean columnaIzquierda = xModelo >= -7.0 && xModelo <= -5.3; // GeckoLib/modelos pueden espejar X visualmente.
        return (columnaDerecha || columnaIzquierda) && yModelo >= 4.2 && yModelo <= 8.6;
    }

    /** Clic de línea y aviso en la barra de acciones. */
    public static void avisar(net.minecraft.world.World world, BlockPos pos, net.minecraft.entity.player.PlayerEntity jugador, boolean encendida) {
        world.playSound(null, pos, net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(),
                net.minecraft.sound.SoundCategory.BLOCKS, 0.6f, encendida ? 1.2f : 0.8f);
        jugador.sendMessage(net.minecraft.text.Text.translatable(encendida
                ? "modamod.maquina.linea.encendida" : "modamod.maquina.linea.apagada"), true);
    }
}
