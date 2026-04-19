# ADR-005: Search strategy

## Status
Accepted

## Context
When a user asks a question (e.g. "how does authentication work?"), 
the system must retrieve the most relevant code chunks from the database 
to pass to the LLM. The retrieval strategy determines answer quality.

## Options considered

### Option A: Pure vector search
Convert the query to an embedding and find the most similar vectors 
using cosine similarity.

### Option B: Pure keyword search
Use PostgreSQL full-text search (tsvector / tsquery) to find chunks 
containing matching terms.

### Option C: Hybrid search (vector + keyword combined)
Run both searches and merge/re-rank the results.

## Decision
Option C — hybrid search.

## Reasoning
- Vector search alone misses exact matches. If a user searches for 
  "UserAuthService", vector search may return semantically similar 
  but irrelevant results instead of the exact class.
- Keyword search alone misses semantic intent. "How does login work?" 
  won't match code that never uses the word "login" but handles 
  authentication.
- Hybrid search covers both cases. This is the approach used by 
  production RAG systems at companies like Elastic and Weaviate.
- PostgreSQL supports full-text search natively — no extra tooling needed.

## Trade-offs
- More complex query logic — must merge and de-duplicate two result sets.
- Slightly higher query latency than pure vector search.
- Requires a re-ranking step to produce a final ordered result list.

## Consequences
pgvector handles vector similarity search.
PostgreSQL tsvector/tsquery handles keyword search.
A re-ranking function in the Python service merges both result sets 
before returning chunks to Spring Boot.
Add a GIN index on the content column for full-text search performance.