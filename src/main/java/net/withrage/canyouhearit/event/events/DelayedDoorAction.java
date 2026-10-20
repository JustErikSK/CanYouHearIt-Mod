package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DelayedDoorAction {

    private final Level level;
    private final BlockPos pos;
    private int remainingTicks;

    public DelayedDoorAction(Level level, BlockPos pos, int delay) {
        this.level = level;
        this.pos = pos;
        this.remainingTicks = delay;
    }

    public boolean tick() {
        if (--remainingTicks > 0) {
            return false;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof DoorBlock door
                && state.getValue(BlockStateProperties.OPEN)) {

            door.setOpen(null, level, state, pos, false);
        }
        return true;
    }
}
