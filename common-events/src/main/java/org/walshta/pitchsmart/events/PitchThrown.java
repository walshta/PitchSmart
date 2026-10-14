package org.walshta.pitchsmart.events;

import java.time.Instant;
import java.util.UUID;

public record PitchThrown(
    UUID eventId,
    String gameId,
    String pitcherId,
    int inning,
    boolean topOfInning,
    PitchResult result,
    Instant occurredAt
) {}
