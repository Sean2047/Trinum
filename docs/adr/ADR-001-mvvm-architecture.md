# ADR-001: MVVM Architecture Pattern
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Adopt MVVM (Model-View-ViewModel) as the single architecture pattern across all features.

## Context
Trinum has three peer tool features (Calculator, Converter, Table). A consistent architecture
pattern is required so AI sessions across multiple features do not independently choose
different patterns. MVI was considered as an alternative.

## Consequences
- One ViewModel per screen; ViewModel survives configuration changes via `hiltViewModel()`
- Zero business logic inside `@Composable` functions — Composables observe state and emit UiActions only
- All mutable state lives in ViewModel as `StateFlow<FeatureUiState>`
- Use cases encapsulate business logic; ViewModels orchestrate use cases
- Unit-testing strategy is ViewModel-centric: test all UiAction handlers via Turbine

## Rationale
MVVM is preferred over MVI for this project because:
1. Trinum's computation is largely synchronous and stateless — MVI's unidirectional intent
   processing adds overhead without benefit for tool-type apps
2. MVVM ViewModel isolation makes pure-function testing of expression evaluation and unit
   conversion trivial
3. Hilt's `@HiltViewModel` integrates directly with MVVM; no adapter layer needed
