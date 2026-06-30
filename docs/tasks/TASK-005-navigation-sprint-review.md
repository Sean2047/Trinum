# TASK-005: Navigation Integration + Sprint Review
Sprint: 1
Status: Pending

## Objective
Wire a Material3 bottom navigation bar (3 tabs: Calculator / Converter / Table) into a `MainScreen` composable, update `MainActivity` to use it, and complete the Sprint 1 Fitness Checklist (§7).

## READS
- `app/src/main/java/dev/trinum/app/MainActivity.kt` — MODIFIES target; current structure (creates navController + calls AppNavGraph directly)
- `app/src/main/java/dev/trinum/app/navigation/AppNavGraph.kt` — NavHost to be wrapped by MainScreen
- `app/src/main/java/dev/trinum/app/navigation/Routes.kt` — route definitions used to build tab items
- `app/src/main/java/dev/trinum/app/feature/calculator/ui/CalculatorScreen.kt` — existing Scaffold/padding pattern (inner Scaffold for SnackbarHost)
- `app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt` — same inner-Scaffold pattern
- `gradle/libs.versions.toml` — verify icon dependency availability (no `material-icons-extended` declared; may require DEC if extended icons are needed)
- `docs/invariants.md` — Sprint Review: verify INV-001 through INV-005 still hold against new code
- `docs/AI_SESSION_GUIDE.md` §7 — Sprint Review Fitness Checklist items to run

## CREATES
- `app/src/main/java/dev/trinum/app/navigation/MainScreen.kt` — `@Composable fun MainScreen()`: creates `rememberNavController()` internally; `Scaffold(bottomBar = { BottomNavBar(navController) }) { padding -> Box(Modifier.padding(padding)) { AppNavGraph(navController) } }`; private `BottomNavBar(navController, modifier)` composable using `NavigationBar` + `NavigationBarItem`; `currentBackStackEntryAsState()` drives selected tab; tap handler uses `navController.navigate(route) { popUpTo(startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }`; private `data class BottomNavItem(route, label, icon)` + `NAV_ITEMS` list (Calculator/Converter/Table with icons from `Icons.Default`); **icon note**: verify `Calculate`, `SwapHoriz`, `GridOn` are available in basic material3; if not, log DEC-017 and add `material-icons-extended` to `:app` before proceeding
- `app/src/androidTest/java/dev/trinum/app/navigation/MainScreenTest.kt` — Compose semantics via `createAndroidComposeRule<MainActivity>` (requires Hilt test runner); verify: all 3 tab labels are displayed (`onNodeWithText("Calculator")`, etc.); tapping a tab label navigates (selected state changes)

## MODIFIES
- `app/src/main/java/dev/trinum/app/MainActivity.kt` — replace `rememberNavController()` + `AppNavGraph(navController)` with a single `MainScreen()` call; remove `rememberNavController` import and `AppNavGraph` import (both move inside MainScreen)

## DELETES
- none

## Evidence
- ADR §1: Navigation — Navigation Compose + bottom nav; 3 tabs: Calculator / Converter / Table
- ADR §1: MVVM — `MainScreen` is pure navigation glue, no ViewModel needed
- ADR §1: Composable nesting depth ≤5 (§7 fitness baseline); verify `MainScreen → Scaffold → Box → AppNavGraph → [Screen] → Scaffold` is ≤5 deep
- **Inner-Scaffold coexistence**: `CalculatorScreen` and `TableScreen` own inner `Scaffold`s for `SnackbarHost`. The outer `MainScreen` Scaffold has `bottomBar` only. Applying outer `paddingValues` via `Box(Modifier.padding(padding))` wrapping `AppNavGraph` ensures content avoids the bottom nav without double-padding; each screen's own inner Scaffold handles its own content insets. `ConverterScreen` (no inner Scaffold) benefits from the outer padding keeping content above the nav bar.
- **Sprint 1 TD note (INV-004 tension)**: `EvaluateAll` in `TableViewModel` replaces formula content with numeric result while preserving `isFormula = true`, creating an in-memory inconsistency with INV-004. This is the documented Sprint 1 limitation (TASK-004 spec). If the user saves after evaluation, the persisted `TableCellEntity` would have `isFormula = true` but non-formula content. Log as TD item in WorkStatus during Sprint Review.
- **Sprint Review scope**: After navigation wiring is CI-green, run §7 checklist: `./gradlew :domain:test`, module Fan-In/Fan-Out check, TD review, invariants spot-check, ADR state audit. Output results in WorkStatus Session Output.
- DEC-014: Class E — this task has only 2 CREATES (navigation wiring + 1 test); no P7 exceedance

## Definition of Done
- [ ] All CREATES files exist
- [ ] Compile gate passes
- [ ] `MainScreen` displays bottom nav with 3 labelled tabs
- [ ] Tab navigation works (back-stack preserved per tab, launchSingleTop)
- [ ] Tests written per P11
- [ ] WorkStatus updated (File Registry, Session Output)
- [ ] Knowledge Alert output (P17) if triggered
- [ ] Sprint 1 Fitness Checklist (§7) completed and results in WorkStatus Session Output
- [ ] INV-004 tension logged as TD item (or DEC if fix is planned for Sprint 2)
- [ ] Handoff Package submitted for Reviewer
- [ ] Reviewer: APPROVED
- [ ] CI PASS: `./gradlew test detekt ktlintCheck lint`
