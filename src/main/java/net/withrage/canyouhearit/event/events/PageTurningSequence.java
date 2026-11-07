package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class PageTurningSequence {

    private final ServerPlayer player;
    private final BlockPos bookPos;

    private int remainingSounds;
    private int ticksUntilNextSound;

    public PageTurningSequence(ServerPlayer player, BlockPos bookPos) {
        this.player = player;
        this.bookPos = bookPos;

        this.remainingSounds = 1 + player.getRandom().nextInt(3);
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

        playPageTurningSound();

        remainingSounds--;

        if (remainingSounds <= 0) {
            return true;
        }

        ticksUntilNextSound = 15 + player.getRandom().nextInt(16);

        return false;
    }

    private void playPageTurningSound() {
        double x = bookPos.getX() + 0.5;
        double y = bookPos.getY() + 0.5;
        double z = bookPos.getZ() + 0.5;

        float volume = 0.25F;
        float pitch = 0.9F + player.getRandom().nextFloat() * 0.2F;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(
                                SoundEvents.BOOK_PAGE_TURN
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
