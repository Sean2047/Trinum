# ADR-003: Hilt Dependency Injection
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Use Hilt 2.52 as the sole dependency injection framework. Koin is prohibited.

## Context
The project uses a multi-module structure (`:app`, `:domain`, `:data`, `:core:ui`). A DI
framework must wire `:data` implementations to `:domain` interfaces without creating direct
compile-time dependencies from `:app` to `:data`.

## Consequences
- `@HiltAndroidApp` on the Application class (`TrinumApplication`)
- `@HiltViewModel` on every ViewModel — no manual ViewModel construction anywhere
- `@InstallIn(SingletonComponent)` on all `@Module` classes providing Room DB, DAOs, and Repository bindings
- `:data` module provides its own `DatabaseModule` — `:app` does not bind data-layer dependencies directly
- `hilt-navigation-compose` used for `hiltViewModel()` in Composables
- Version pinned to `hilt = "2.52"` in `libs.versions.toml`; upgrades require a dedicated Upgrade Task + DEC

## Rationale
Hilt is chosen over Koin because:
1. Compile-time validation catches missing bindings before runtime — critical for multi-session AI development
   where each session independently generates components
2. Hilt's `@InstallIn` scoping maps directly to the module DAG: `:data` owns its Hilt module,
   `:app` owns top-level bindings — no cross-module DI leakage
3. Google-maintained; stable API surface reduces the risk of breaking changes across the project lifetime
