package net.withrage.canyouhearit.event;

import net.minecraft.server.level.ServerPlayer;

public interface ParanormalEvent {

    boolean canTrigger(ServerPlayer player);

    void trigger(ServerPlayer player);
}
