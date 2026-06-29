# ADR-005: Room 2.7.0 Local Persistence
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Use Room 2.7.0 for local persistence, scoped to two use cases: calculator expression history
and saved table state. DataStore and raw SQLite are prohibited.

## Context
Trinum is 100% offline. Persistence is needed for (1) the last 50 calculator expressions
and (2) named table saves. The schema is intentionally minimal to exercise DAO/Repository
principles without over-engineering.

## Consequences
- Two DAOs with fully locked signatures (see AI Session Guide §1.3): `HistoryEntryDao`, `SavedTableDao`
- Three entities with `@Keep`: `HistoryEntryEntity`, `SavedTableEntity`, `TableCellEntity`
- One `@Relation` class: `SavedTableWithCells`
- `AppDatabase` declares `version = 1` at Foundation Task; any schema change requires a
  `Migration(from, to)` object and a DEC entry before the change is made
- `fallbackToDestructiveMigration()` is forbidden in non-debug build variants
- `exportSchema = true`; schema JSON files committed to version control
- Unit Converter feature has no persistence — conversion is stateless computation

## Rationale
Room is chosen over raw SQLite and DataStore because:
1. DAO interface + `Flow<T>` return types map directly to the Repository pattern required by P2
2. In-memory Room database enables deterministic DAO integration tests (P11 test pyramid)
3. Compile-time query validation catches SQL errors before runtime — critical for AI-generated queries
4. DataStore is appropriate for key-value preferences, not the structured relational data
   (table cells with foreign-key relationships) needed by the Table Calculator
