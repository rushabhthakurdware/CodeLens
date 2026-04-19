# ADR-003: Embedding model choice

## Status
Accepted

## Context
Embeddings convert code chunks into vectors. The quality of embeddings 
directly determines how accurately the system retrieves relevant code 
when a user asks a question. We need to choose between a general-purpose 
embedding model and a code-specific one.

## Options considered

### Option A: OpenAI text-embedding-3-small
General-purpose embedding model via API. 1536 dimensions. 
No local setup required.

### Option B: CodeBERT (Microsoft)
Open-source model trained specifically on code (Python, Java, JS, etc.).
768 dimensions. Runs locally.

### Option C: OpenAI text-embedding-3-large
Higher quality than Option A but more expensive per token.

## Decision
Option A for MVP — OpenAI text-embedding-3-small.
Plan to evaluate CodeBERT in v2.

## Reasoning
- text-embedding-3-small is fast, reliable, and requires no local 
  infrastructure to run during development.
- For MVP, speed of development matters more than maximum accuracy.
- OpenAI's models have strong performance on code despite being 
  general-purpose (code is well-represented in training data).
- Switching embedding models later requires re-embedding all stored 
  chunks — this is a known cost, documented here as a future risk.

## Trade-offs
- API cost per embedding call. Mitigated by idempotent ingestion 
  (SHA-256 file hashing ensures we never re-embed unchanged files).
- Vendor dependency on OpenAI availability.
- CodeBERT may produce higher quality code embeddings — deferred to v2.

## Consequences
Vector column in pgvector must be vector(1536).
All embedding calls go through the Python FastAPI service.
File hashing must be implemented before any embedding logic to 
control API costs.