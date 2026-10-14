# 2. Pitch ingest returns 202 Accepted with an event ID receipt

Date: 2026-10-14
Status: Accepted

## Context

`POST /games/{gameId}/pitches` does not process a pitch. It turns the request into a
`PitchThrown` event, publishes it to `game-events`, and waits for Kafka to acknowledge it.
Downstream services (game-state, and later compliance and stats) process it
asynchronously, after the HTTP response has been sent.

The server generates the event's `eventId` and `occurredAt`; the client never sees the
event itself. Without a response body, the client has no way to refer to the pitch it
just submitted.

Options considered:

1. **`void` with 202 and an empty body.** Simplest. The client gets no handle on the event.
2. **Return the bare `UUID`.** Serializes as a JSON string (`"3f2a..."`), which cannot gain
   fields later without breaking clients.
3. **Return a small record, `PitchAccepted(eventId)`.** Serializes as `{"eventId": "..."}`.
4. **202 with a `Location` header** pointing at a status resource (`/pitches/{eventId}`).
   The textbook pattern for async APIs, but there is no status endpoint yet.

## Decision

- Respond **202 Accepted**, not 200: the pitch has been recorded, not processed.
- Respond only after Kafka acknowledges the send (`send(...).join()`), so a 202 means the
  event is durably stored. If Kafka is unavailable the client gets a 5xx, not a false 202.
- Return `PitchAccepted(UUID eventId)` as a JSON object (option 3).

## Consequences

- The client holds a receipt it can use to trace one pitch through Kafka UI, service logs,
  and (Phase 7) distributed traces.
- End-to-end tests can POST, read the `eventId`, and assert that downstream services
  processed that specific event.
- The ID gives Phase 4 idempotency work a shared key for recognizing client retries.
- The response is an object, so fields (`receivedAt`, a status link) can be added
  without breaking existing clients.
- If a status endpoint is added later, add a `Location` header (option 4) alongside the body.
- Waiting on the Kafka ack ties request latency to the broker. Phase 4's transactional
  outbox replaces this: the 202 will mean "saved to the outbox" instead.
