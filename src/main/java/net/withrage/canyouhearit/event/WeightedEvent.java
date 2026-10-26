package net.withrage.canyouhearit.event;

public record WeightedEvent(
        ParanormalEvent event,
        int weight
) {
    public WeightedEvent {
        if (weight < 0) {
            throw new IllegalArgumentException(
                    "Event weight cannot be negative!"
            );
        }
    }
}
