package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChestSequence {

    private final ServerLevel level;
    private final BlockPos pos;
    private int remainingTicks;

    public ChestSequence(ServerLevel level, BlockPos pos, int delay) {
        this.level = level;
        this.pos = pos.immutable();
        this.remainingTicks = delay;
    }

    public boolean tick() {
        if (--remainingTicks > 0) {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof ChestBlockEntity) {
            BlockState state = level.getBlockState(pos);

            level.blockEvent(pos, state.getBlock(), 1, 0);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.CHEST_CLOSE,
                    SoundSource.BLOCKS,
                    0.5F,
                    0.9F + level.random.nextFloat() * 0.1F
            );
        }
        return true;
    }
}
