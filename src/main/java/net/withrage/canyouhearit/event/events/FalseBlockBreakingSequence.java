package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class FalseBlockBreakingSequence {

    private final ServerPlayer player;
    private final BlockPos blockPos;
    private final SoundEvent hitSound;

    private int remainingHits;
    private int ticksUntilNextHit;

    public FalseBlockBreakingSequence(ServerPlayer player, BlockPos blockPos) {
        this.player = player;
        this.blockPos = blockPos;

        BlockState state = player.level().getBlockState(blockPos);
        this.hitSound = state.getSoundType().getHitSound();

        this.remainingHits = 2 + player.getRandom().nextInt(4);
        this.ticksUntilNextHit = 0;
    }

    public boolean tick() {
        if (!player.isAlive() || player.isRemoved()) {
            return true;
        }

        if (ticksUntilNextHit > 0) {
            ticksUntilNextHit--;
            return false;
        }

        playHitSound();

        remainingHits--;

        if (remainingHits <= 0) {
            return true;
        }

        ticksUntilNextHit = 4 + player.getRandom().nextInt(6);

        return false;
    }

    private void playHitSound() {
        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 0.5;
        double z = blockPos.getZ() + 0.5;

        float volume = 0.3F;
        float pitch = 0.85F + player.getRandom().nextFloat() * 0.2F;

        player.connection.send(
                new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(hitSound),
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
