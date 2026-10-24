package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class ChestEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public ChestEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyChests(player).isEmpty();
    }


    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> chests = findNearbyChests(player);

        if (chests.isEmpty()) {
            return;
        }

        BlockPos pos = chests.get(
                player.getRandom().nextInt(chests.size())
        );

        var level = player.serverLevel();

        if (!(level.getBlockEntity(pos)
                instanceof ChestBlockEntity chest)) {
            return;
        }

        BlockState state = level.getBlockState(pos);

        level.blockEvent(pos, state.getBlock(), 1, 1);

        int delay = 40 + player.getRandom().nextInt(61);

        manager.addChestSequence(
                new ChestSequence(level, pos, delay)
        );
    }

    private List<BlockPos> findNearbyChests(ServerPlayer player) {
        List<BlockPos> chests = new ArrayList<>();

        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-8, -4, -8),
                center.offset(8, 4, 8))) {

            BlockState state = player.level().getBlockState(pos);

            if (state.getBlock() instanceof AbstractChestBlock<?>
                    && player.level().getBlockEntity(pos)
                    instanceof ChestBlockEntity) {

                chests.add(pos.immutable());
            }
        }
        return chests;
    }
}
