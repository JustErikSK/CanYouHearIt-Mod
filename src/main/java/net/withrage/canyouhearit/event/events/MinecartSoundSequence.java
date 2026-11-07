package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class MinecartSoundSequence {

    private final ServerPlayer player;
    private final double directionX;
    private final double directionZ;

    private double x;
    private double y;
    private double z;

    private int remainingSounds;
    private int ticksUntilNextSound;

    public MinecartSoundSequence(ServerPlayer player, BlockPos railPos) {
        this.player = player;

        this.x = railPos.getX() + 0.5;
        this.y = railPos.getY() + 0.5;
        this.z = railPos.getZ() + 0.5;

        double angle = player.getRandom().nextDouble() * Math.PI * 2;

        this.directionX = Math.cos(angle) * 0.65;
        this.directionZ = Math.sin(angle) * 0.65;

        this.remainingSounds = 3 + player.getRandom().nextInt(4);
        this.ticksUntilNextSound = 0;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (ticksUntilNextSound > 0) {
            ticksUntilNextSound--;
            return false;
        }

        playMinecartSound();

        remainingSounds--;

        if (remainingSounds <= 0) {
            return true;
        }

        x += directionX;
        z += directionZ;

        ticksUntilNextSound = 8 + player.getRandom().nextInt(7);

        return false;
    }

    private void playMinecartSound() {
        float volume = 0.2F * (remainingSounds / 6.0F);
        float pitch = 0.85F + player.getRandom().nextFloat() * 0.15F;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(
                                SoundEvents.MINECART_RIDING
                        ),
                        SoundSource.BLOCKS,
                        x,
                        y,
                        z,
                        volume,
                        pitch,
                        player.getRandom().nextLong()
                )
        );
    }
}
