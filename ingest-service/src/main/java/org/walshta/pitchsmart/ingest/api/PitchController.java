package org.walshta.pitchsmart.ingest.api;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.walshta.pitchsmart.events.PitchThrown;
import org.walshta.pitchsmart.events.Topics;
import org.walshta.pitchsmart.ingest.requests.PitchRequest;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping(path = "/games/{gameId}/pitches")
@RequiredArgsConstructor
public class PitchController {
  public record PitchAccepted(UUID eventId) {}

  private final KafkaTemplate<String, PitchThrown> kafkaTemplate;

  @PostMapping()
  @ResponseStatus(HttpStatus.ACCEPTED)
  public PitchAccepted handlePitch(@PathVariable String gameId, @Valid @RequestBody PitchRequest pitchRequest) {
    var event = new PitchThrown(
        UUID.randomUUID(),
        gameId,
        pitchRequest.pitcherId(),
        pitchRequest.inning(),
        pitchRequest.topOfInning(),
        pitchRequest.result(),
        Instant.now()
    );

    kafkaTemplate.send(Topics.GAME_EVENTS, gameId, event).join();
    return new PitchAccepted(event.eventId());
  }
}
