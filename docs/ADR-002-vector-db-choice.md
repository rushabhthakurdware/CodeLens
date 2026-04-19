# ADR-002: Vector database choice

## Status
Accepted

## Context
We need to store vector embeddings of code chunks and retrieve them 
via similarity search. Several dedicated vector databases exist, but 
we also have the option of extending our existing PostgreSQL instance.

## Options considered

### Option A: Pinecone
Fully managed cloud vector database. Simple API, scales easily.

### Option B: ChromaDB
Lightweight open-source vector database. Easy local setup.

### Option C: PostgreSQL + pgvector extension
Adds vector storage and similarity search directly inside PostgreSQL.

## Decision
Option C — PostgreSQL with the pgvector extension.

## Reasoning
- We already need PostgreSQL for relational data (users, projects, jobs).
  Using pgvector means one database, one connection, one Docker container.
- pgvector supports cosine similarity, L2 distance, and inner product — 
  sufficient for our use case.
- A single SQL query can JOIN relational metadata with vector similarity 
  search. This is not possible with a separate vector DB.
- Eliminates the cost and complexity of managing a separate service.
- ACID compliance — vector writes and metadata writes happen in the same 
  transaction, preventing inconsistent state.

## Trade-offs
- pgvector is slower than dedicated vector DBs (Pinecone, Weaviate) at 
  very high scale (millions of vectors).
- No built-in approximate nearest neighbour (ANN) index as mature as 
  Pinecone's. Mitigated by using HNSW index in pgvector >= 0.5.0.

## Consequences
PostgreSQL must have the pgvector extension installed.
Use pgvector >= 0.5.0 to access HNSW indexing for better query speed.
Vector dimensions must be decided before schema creation (1536 for 
OpenAI embeddings, 768 for CodeBERT).