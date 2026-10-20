package net.withrage.canyouhearit.event.events;

import net.minecraft.server.level.ServerPlayer;
import net.withrage.canyouhearit.event.ParanormalEvent;
import net.withrage.canyouhearit.event.ParanormalEventManager;

public class FootstepEvent implements ParanormalEvent {

    private final ParanormalEventManager manager;

    public FootstepEvent(ParanormalEventManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canTrigger(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator();
    }

    @Override
    public void trigger(ServerPlayer player) {
        manager.addFootstepSequence(new FootstepSequence(player));
    }
}
