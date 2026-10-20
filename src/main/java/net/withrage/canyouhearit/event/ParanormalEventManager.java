
package net.withrage.canyouhearit.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParanormalEventManager {

    private static final int MIN_DELAY = 20 * 60;
    private static final int MAX_DELAY = 20 * 180;

    private final Map<UUID, Integer> timers = new HashMap<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (ServerPlayer player : event.getServer()
                .getPlayerList().getPlayers()) {

            UUID uuid = player.getUUID();

            int timer = timers.computeIfAbsent(
                    uuid,
                    id -> randomDelay(player)
            );

            if (timer > 0) {
                timers.put(uuid, timer - 1);
                continue;
            }

            triggerRandomEvent(player);
            timers.put(uuid, randomDelay(player));
        }

        timers.keySet().removeIf(uuid ->
                event.getServer().getPlayerList()
                        .getPlayer(uuid) == null
        );
    }

    private int randomDelay(ServerPlayer player) {
        return MIN_DELAY + player.getRandom()
                .nextInt(MAX_DELAY - MIN_DELAY + 1);
    }

    private void triggerRandomEvent(ServerPlayer player) {
    }
}
