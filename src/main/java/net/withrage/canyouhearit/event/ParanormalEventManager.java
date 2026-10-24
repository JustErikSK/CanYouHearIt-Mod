package net.withrage.canyouhearit.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.withrage.canyouhearit.event.events.*;

import java.util.*;

public class ParanormalEventManager {

    private static final int MIN_DELAY = 20 * 5; // Changed for testing, default values are 20 * 60
    private static final int MAX_DELAY = 20 * 10; // Changed for testing, default values are 20 * 180

    private final Map<UUID, Integer> timers = new HashMap<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            activeSequences.removeIf(FootstepSequence::tick);
            delayedDoorActions.removeIf(DelayedDoorAction::tick);
            activeKnockingSequences.removeIf(KnockingSequence::tick);
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

        List<ParanormalEvent> availableEvents = events.stream()
                .filter(event -> event.canTrigger(player))
                .toList();

        if (availableEvents.isEmpty()) {
            return;
        }

        ParanormalEvent selected = availableEvents.get(
                player.getRandom().nextInt(availableEvents.size())
        );

        selected.trigger(player);
    }

    private final List<ParanormalEvent> events = List.of(
            new FootstepEvent(this),
            new DoorEvent(this),
            new KnockingEvent(this)
    );

    private final List<FootstepSequence> activeSequences = new ArrayList<>();
    private final List<DelayedDoorAction> delayedDoorActions = new ArrayList<>();
    private final List<KnockingSequence> activeKnockingSequences = new ArrayList<>();

    public void addFootstepSequence(FootstepSequence sequence) {activeSequences.add(sequence);}
    public void addDelayedDoorAction(DelayedDoorAction action) {delayedDoorActions.add(action);}
    public void addKnockingSequence(KnockingSequence sequence) {activeKnockingSequences.add(sequence);}
}
