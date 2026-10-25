package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.withrage.canyouhearit.event.ParanormalEvent;

import java.util.ArrayList;
import java.util.List;

public class TorchEvent implements ParanormalEvent {

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyTorches(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> torches = findNearbyTorches(player);

        if (torches.isEmpty()) {
            return;
        }

        BlockPos pos = torches.get(
                player.getRandom().nextInt(torches.size())
        );

        BlockState state = player.level().getBlockState(pos);
        Block replacement;

        if (state.is(Blocks.TORCH)) {
            replacement = Blocks.SOUL_TORCH;
        } else if (state.is(Blocks.SOUL_TORCH)) {
            replacement = Blocks.TORCH;
        } else if (state.is(Blocks.WALL_TORCH)) {
            replacement = Blocks.SOUL_WALL_TORCH;
        } else if (state.is(Blocks.SOUL_WALL_TORCH)) {
            replacement = Blocks.WALL_TORCH;
        } else {
            return;
        }

        BlockState newState = replacement.defaultBlockState();

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                && newState.hasProperty(
                BlockStateProperties.HORIZONTAL_FACING)) {

            newState = newState.setValue(
                    BlockStateProperties.HORIZONTAL_FACING,
                    state.getValue(
                            BlockStateProperties.HORIZONTAL_FACING
                    )
            );
        }

        player.serverLevel().setBlock(
                pos,
                newState,
                Block.UPDATE_ALL
        );
    }

    private List<BlockPos> findNearbyTorches(ServerPlayer player) {
        List<BlockPos> torches = new ArrayList<>();

        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-8, -4, -8),
                center.offset(8, 4, 8))) {

            BlockState state = player.level().getBlockState(pos);

            if (state.is(Blocks.TORCH)
                    || state.is(Blocks.SOUL_TORCH)
                    || state.is(Blocks.WALL_TORCH)
                    || state.is(Blocks.SOUL_WALL_TORCH)) {

                torches.add(pos.immutable());
            }
        }
        return torches;
    }
}
