package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class DoorEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public DoorEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyDoors(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> doors = findNearbyDoors(player);

        if (doors.isEmpty()) {
            return;
        }

        BlockPos pos = doors.get(
                player.getRandom().nextInt(doors.size())
        );

        Level level = player.level();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof DoorBlock door)) {
            return;
        }

        boolean isOpen = state.getValue(BlockStateProperties.OPEN);

        door.setOpen(null, level, state, pos, !isOpen);

        if (!isOpen && player.getRandom().nextBoolean()) {
            int delay = 40 + player.getRandom().nextInt(61);

            manager.addDelayedDoorAction(
                    new DelayedDoorAction(level, pos, delay)
            );
        }
    }

    private List<BlockPos> findNearbyDoors(ServerPlayer player) {
        List<BlockPos> doors = new ArrayList<>();

        BlockPos center = player.blockPosition();
        Level level = player.level();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-8, -4, -8),
                center.offset(8, 4, 8))) {

            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof DoorBlock
                    && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)
                    == DoubleBlockHalf.LOWER) {

                doors.add(pos.immutable());
            }
        }
        return doors;
    }
}
