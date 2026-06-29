# Trinum — Domain Invariants Registry
# docs/invariants.md
# Governed by: Android AI Dev Design Principles v6.1 P16
# Format: INV-NNN | Scope | Statement | Source | Added (Sprint)
#
# Scopes: Architecture | Business | Data
# Maintenance: AI adds entries during sessions; developer reviews at Sprint Review
# A violated invariant = STOP; log DEC; fix before continuing

| INV-ID  | Scope        | Invariant Statement                                                                                                          | Source           | Added    |
|---------|--------------|------------------------------------------------------------------------------------------------------------------------------|------------------|----------|
| INV-001 | Architecture | The `:domain` module must contain zero `android.*` or `androidx.*` imports at all times                                     | ADR-001, P3      | Sprint 1 |
| INV-002 | Business     | No computation result is ever obtained via a network call; all evaluation is local and synchronous or coroutine-local        | ADR-006, P-1     | Sprint 1 |
| INV-003 | Data         | `HistoryEntry.timestamp` is written exactly once at repository insertion time and is never modified after creation           | ADR-005          | Sprint 1 |
| INV-004 | Data         | A `TableCellEntity` with `isFormula = true` always stores the raw formula string (prefixed with `=`) in `content`, never a cached numeric result | ADR-005 | Sprint 1 |
| INV-005 | Business     | exp4j 0.4.8 treats leading/trailing unary `+`/`-` as syntactically valid (e.g. `"2++3"` evaluates to `5`); an expression is invalid only on structural parse error, `NaN`, or `Infinity` result | TASK-002 Knowledge Alert | Sprint 1 |
