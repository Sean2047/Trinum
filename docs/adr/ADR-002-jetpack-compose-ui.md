# ADR-002: Jetpack Compose UI Framework
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Use Jetpack Compose exclusively for all UI. XML layouts are prohibited everywhere in the codebase.

## Context
The project requires a single UI technology choice to prevent mixed-paradigm code across AI sessions.
XML + View system was considered as an alternative.

## Consequences
- No `layout/` XML files anywhere in any module
- All screens implemented as `@Composable` functions following the `{Feature}Screen` naming convention
- Shared UI components live in `:core:ui` as Composable functions
- State observation in Composables via `collectAsStateWithLifecycle()` only (not `collectAsState()`)
- UI testing via Compose semantics (no Espresso)
- `compose-bom` version controls all Compose library versions; individual overrides are prohibited

## Rationale
Jetpack Compose is chosen over XML because:
1. Declarative UI aligns with the UiState → render mental model enforced by MVVM + UDF
2. Compose semantics-based UI testing integrates with the 10% UI test tier in the test pyramid
3. No mixed-paradigm risk: a single Composable model across all three features eliminates
   the class of bugs caused by View/Fragment lifecycle mismatches
