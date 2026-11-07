package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.BaseRailBlock;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

import java.util.ArrayList;
import java.util.List;

public class MinecartSoundEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public MinecartSoundEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !findNearbyRails(player).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> rails = findNearbyRails(player);

        if (rails.isEmpty()) {
            return;
        }

        BlockPos selected = rails.get(
                player.getRandom().nextInt(rails.size())
        );

        manager.addMinecartSoundSequence(
                new MinecartSoundSequence(player, selected)
        );
    }

    private List<BlockPos> findNearbyRails(ServerPlayer player) {
        List<BlockPos> rails = new ArrayList<>();

        BlockPos center = player.blockPosition();

        int radius = 12;
        int verticalRadius = 6;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -verticalRadius, -radius),
                center.offset(radius, verticalRadius, radius)
        )) {
            if (player.level().getBlockState(pos).getBlock()
                    instanceof BaseRailBlock) {
                rails.add(pos.immutable());
            }
        }
        return rails;
    }
}
