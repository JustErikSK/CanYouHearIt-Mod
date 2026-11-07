package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class LadderSoundEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public LadderSoundEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyLadders(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> ladders = findNearbyLadders(player);

        if (ladders.isEmpty()) {
            return;
        }

        BlockPos selected = ladders.get(
                player.getRandom().nextInt(ladders.size())
        );

        manager.addLadderSoundSequence(
                new LadderSoundSequence(player, selected)
        );
    }

    private List<BlockPos> findNearbyLadders(ServerPlayer player) {
        List<BlockPos> ladders = new ArrayList<>();

        BlockPos center = player.blockPosition();

        int radius = 8;
        int verticalRadius = 5;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -verticalRadius, -radius),
                center.offset(radius, verticalRadius, radius)
        )) {
            if (player.level().getBlockState(pos).is(Blocks.LADDER)) {
                ladders.add(pos.immutable());
            }
        }
        return ladders;
    }
}
