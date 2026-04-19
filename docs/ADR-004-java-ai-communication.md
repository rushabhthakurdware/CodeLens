# ADR-004: Java to AI service communication

## Status
Accepted

## Context
The Spring Boot backend needs to trigger AI operations (ingestion, 
search, analysis) that live in the Python FastAPI service. We need 
to decide how these two services communicate.

## Options considered

### Option A: Direct HTTP REST calls (Spring Boot → FastAPI)
Spring Boot calls FastAPI endpoints via RestTemplate or WebClient.

### Option B: Message broker (Kafka / RabbitMQ)
Spring Boot publishes events to a queue. Python consumes them 
asynchronously.

### Option C: Spring AI / LangChain4j (Java-native AI)
Handle AI logic directly inside Spring Boot without a Python service.

## Decision
Option A — direct HTTP REST calls via Spring WebClient.

## Reasoning
- Simple to implement, debug, and monitor for a single-developer project.
- Spring WebClient supports non-blocking async calls, so the API 
  stays responsive while waiting for Python to respond.
- A message broker (Option B) adds significant operational complexity 
  (another Docker container, new failure modes) without meaningful 
  benefit at our scale.
- Option C (Java-native AI) is promising but Spring AI's ecosystem 
  for code-specific tasks is less mature than Python's. Python owns 
  tree-sitter, CodeBERT, and most ML tooling natively.

## Trade-offs
- Tight coupling between Spring Boot and FastAPI. If FastAPI is down, 
  ingestion fails. Mitigated by health checks in Docker Compose.
- No retry/dead-letter queue logic out of the box. Must implement 
  retry logic in WebClient calls.

## Consequences
FastAPI must expose documented REST endpoints (/ingest, /search, /analyze).
Spring Boot uses WebClient (not RestTemplate) for non-blocking calls.
Docker Compose health checks must ensure FastAPI is ready before 
Spring Boot starts.