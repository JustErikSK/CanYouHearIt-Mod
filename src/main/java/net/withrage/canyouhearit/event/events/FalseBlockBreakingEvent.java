package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class FalseBlockBreakingEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public FalseBlockBreakingEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyBlocks(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> blocks = findNearbyBlocks(player);

        if (blocks.isEmpty()) {
            return;
        }

        BlockPos selected = blocks.get(
                player.getRandom().nextInt(blocks.size())
        );

        manager.addFalseBlockBreakingSequence(
                new FalseBlockBreakingSequence(player, selected)
        );
    }

    private List<BlockPos> findNearbyBlocks(ServerPlayer player) {
        List<BlockPos> blocks = new ArrayList<>();

        BlockPos center = player.blockPosition();

        int radius = 8;
        int verticalRadius = 4;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -verticalRadius, -radius),
                center.offset(radius, verticalRadius, radius)
        )) {
            double distance = Math.sqrt(pos.distSqr(center));

            if (distance < 3 || distance > 8) {
                continue;
            }

            BlockState state = player.level().getBlockState(pos);

            if (state.isAir()
                    || !state.isSolidRender(player.level(), pos)
                    || state.getDestroySpeed(player.level(), pos) < 0) {
                continue;
            }

            blocks.add(pos.immutable());
        }
        return blocks;
    }
}
