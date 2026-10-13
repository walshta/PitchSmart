# 1. Compose file layout: shared infra vs. service-owned infra

Date: 2026-10-13
Status: Accepted

## Context

ingest-service and game-state-service are separate microservices: each is deployed
and scaled on its own, owns its own data store, and talks to the other only through
Kafka. Some infrastructure is owned by a single service (game-state's Redis, later
ingest's Postgres). Other infrastructure is shared by every service (Kafka, Kafka UI,
and later the schema registry and observability stack).

Giving each service a fully separate compose file would start a separate Kafka per
service, and events would never cross between them. Something has to define the one
Kafka they all use.

## Decision

- Shared infrastructure lives in `infra/compose.yaml`.
- Each service has its own `<service>/compose.yaml` containing only what that service owns.
- The root `compose.yaml` holds no services of its own; it only `include:`s the fragments,
  so everything runs on one Docker network.
- Services use Spring Boot Docker Compose support pointed at `../compose.yaml` with
  `lifecycle-management: start-only`, so stopping one service never stops shared Kafka.

## Consequences

- Ownership is visible in the file layout: a service's dependencies sit next to its code.
- The root file stays a short list of includes as the system grows.
- One extra folder compared with defining Kafka directly in the root file.
- New shared components (schema registry in Phase 2, Grafana/Prometheus in Phase 7) go in `infra/`.
- A service fragment is only added to the root `include:` once it defines at least one service.
