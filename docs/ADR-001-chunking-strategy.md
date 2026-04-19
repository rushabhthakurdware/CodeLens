# ADR-001: Code chunking strategy

## Status
Accepted

## Context
Before storing code in the vector database, we must split source files 
into smaller chunks. The chunking strategy directly affects RAG retrieval 
quality. Poor chunking means the AI retrieves incomplete or irrelevant 
code blocks when answering questions.

## Options considered

### Option A: Fixed character chunking
Split every file into chunks of N characters (e.g. 500 chars), 
with some overlap.

### Option B: Function-level chunking via AST parsing
Use tree-sitter to parse the Abstract Syntax Tree of each file and 
extract complete functions/methods as individual chunks.

## Decision
Option B — function-level chunking via tree-sitter.

## Reasoning
- A function is the natural unit of logic in code. Retrieving a complete 
  function gives the LLM full context to answer questions accurately.
- Fixed character chunking can split a function in the middle of a loop 
  or condition, making the retrieved chunk meaningless.
- tree-sitter is the industry standard for multi-language AST parsing, 
  used by GitHub, Neovim, and VS Code.

## Trade-offs
- More complex to implement than character chunking.
- Very long functions (500+ lines) may still need secondary splitting.
- Requires a separate parser per language (Java, Python, JS etc.).

## Consequences
Every source file must be parsed through tree-sitter before embedding.
The Python FastAPI service owns this responsibility.