package net.withrage.canyouhearit.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;
import net.withrage.canyouhearit.util.DoorUtils;

import java.util.List;

public class KnockingEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public KnockingEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && !DoorUtils.findNearbyDoors(player, true).isEmpty();
    }

    @Override
    public void trigger(ServerPlayer player) {
        List<BlockPos> doors = DoorUtils.findNearbyDoors(player, true);

        if (doors.isEmpty()) {
            return;
        }

        BlockPos pos = doors.get(
                player.getRandom().nextInt(doors.size())
        );

        manager.addKnockingSequence(
                new KnockingSequence(player, pos)
        );
    }
}
