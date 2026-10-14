package org.walshta.pitchsmart.ingest.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.walshta.pitchsmart.events.PitchResult;

public record PitchRequest(
    @NotBlank String pitcherId,
    @Min(1) int inning,
    @NotNull Boolean topOfInning,
    @NotNull PitchResult result
) {}
