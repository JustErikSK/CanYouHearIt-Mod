package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TrapdoorSequence {

    private final ServerLevel level;
    private final BlockPos pos;
    private int remainingTicks;

    public TrapdoorSequence(ServerLevel level, BlockPos pos, int delay) {
        this.level = level;
        this.pos = pos.immutable();
        this.remainingTicks = delay;
    }

    public boolean tick() {
        if (--remainingTicks > 0) {
            return false;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof TrapDoorBlock
                && !state.is(Blocks.IRON_TRAPDOOR)
                && state.getValue(TrapDoorBlock.OPEN)) {

            level.setBlock(
                    pos,
                    state.setValue(TrapDoorBlock.OPEN, false),
                    3
            );

            level.playSound(
                    null,
                    pos,
                    SoundEvents.WOODEN_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS,
                    0.7F,
                    0.9F + level.random.nextFloat() * 0.1F
            );
        }
        return true;
    }
}
