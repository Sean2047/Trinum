# ADR-004: Navigation Compose + Bottom Navigation Bar
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Use Navigation Compose with a bottom navigation bar for top-level tool switching among the
three peer tabs: Calculator, Converter, Table.

## Context
Trinum presents three equal-weight tool features with no hierarchy between them. The User
Flow defines "Select Tool Tab" as reachable from any node via bottom navigation. Fragment-based
navigation and drawer navigation were considered.

## Consequences
- `NavHost` with three top-level destinations defined in `AppNavGraph.kt`
- Route strings centralised in `Routes.kt` as a sealed class — no inline string literals for routes
- `BottomNavigationBar` composable lives in `:app` (not `:core:ui`, as it references navigation routes)
- Deep-linking between tools is out of scope for MVP; no back-stack manipulation between tabs
- `navigation-compose` version pinned in `libs.versions.toml`; all route strings locked in Foundation Task
- Feature Tasks must not add new top-level destinations without a DEC

## Rationale
Bottom navigation is chosen because:
1. Three peer tools map directly to the standard bottom-nav UX pattern (no hierarchy to express)
2. Navigation Compose integrates with `hiltViewModel()` scoping — each tab gets its own
   ViewModel instance with correct lifecycle
3. Fragment-based navigation adds unnecessary complexity when the entire UI is Compose
