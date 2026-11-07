package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class LadderSoundSequence {

    private final ServerPlayer player;
    private final BlockPos ladderPos;

    private final int direction;
    private final double startY;

    private int remainingSounds;
    private int ticksUntilNextSound;
    private int soundsPlayed;

    public LadderSoundSequence(ServerPlayer player, BlockPos ladderPos) {
        this.player = player;
        this.ladderPos = ladderPos;

        this.direction = player.getRandom().nextBoolean() ? 1 : -1;

        this.startY = ladderPos.getY() + 0.5;

        this.remainingSounds = 2 + player.getRandom().nextInt(4);

        this.ticksUntilNextSound = 0;
        this.soundsPlayed = 0;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (ticksUntilNextSound > 0) {
            ticksUntilNextSound--;
            return false;
        }

        playLadderSound();

        remainingSounds--;
        soundsPlayed++;

        if (remainingSounds <= 0) {
            return true;
        }

        ticksUntilNextSound = 5 + player.getRandom().nextInt(5);

        return false;
    }

    private void playLadderSound() {
        double x = ladderPos.getX() + 0.5;
        double z = ladderPos.getZ() + 0.5;

        double y = startY + (soundsPlayed * 0.4 * direction);

        float volume = 0.3F;
        float pitch = 0.85F + player.getRandom().nextFloat() * 0.3F;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(
                                SoundEvents.LADDER_STEP
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
