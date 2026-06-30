# Review Report — TASK-007
Date: 2026-06-30
Reviewer: Independent Reviewer Agent

## Round 1

### Gate Assessment
| Gate | Status | Notes |
|---|---|---|
| R1 File Existence | N/A | No CREATES declared |
| R2 Compile | PASS | All four declared modifications verified in source. `RestoreHistoryEntry(val id: Long)` present (UiContracts.kt line 24); when-branch `is CalculatorUiAction.RestoreHistoryEntry -> restoreEntry(action.id)` present (CalculatorViewModel.kt line 59); `private fun restoreEntry(id: Long)` present (lines 106–109); `HistorySection` gains `onRestore: (Long) -> Unit` parameter (line 194); `CalculatorContent` passes lambda at line 97; `HistoryItem` gains `onRestore: () -> Unit` (line 225); Row modifier gains `.clickable(onClick = onRestore)` (line 231); restore test present (CalculatorViewModelTest.kt lines 169–183). No syntax errors. CI PASS declared. |
| R3 Architecture | PASS | `restoreEntry()` calls `_localState.update { ... }` directly (no coroutine launch), consistent with `append()`, `deleteLast()`, and `clearExpression()` which all follow the same synchronous `_localState.update` pattern. Reading `uiState.value.history` is safe: `StateFlow.value` always returns the latest cached value synchronously; no suspend call is needed. `HistoryItem` and `HistorySection` are pure UI composables — zero business logic, zero suspend calls, no repository access. `onRestore` lambda in `CalculatorContent` correctly emits a `CalculatorUiAction`; no business logic in `@Composable`. P1, P3, P3.1, P3.3 all respected. |
| R4 Test Coverage | PASS | Test is meaningful (see analysis below). The restore test correctly asserts `expression`, `result`, and `!isError`. Error path (evaluate invalid expression) is covered by the existing `evaluate invalid expression sets isError true` test. Non-blocking: no test for `RestoreHistoryEntry` with a non-existent id (the `?: return` guard makes this a silent no-op), but this path is unreachable from the UI in normal operation and is not a required P11 error-path gate for this feature. |
| R5 Evidence | PASS | `RestoreHistoryEntry` follows the exact pattern of the already-present `DeleteHistoryEntry`. `restoreEntry()` mirrors `deleteEntry()` structure. No signature guessing; all API usage reads directly from existing code patterns in the same file. Locked DAO signatures not touched. |
| R6 Static Analysis | PASS | `restoreEntry()` = 4 lines. `HistorySection` = 29 lines (lines 191–219, at limit but within). `HistoryItem` = 26 lines (lines 222–247). `onAction` when-block = 12 branches. ViewModel function count = 9 (≤ 15 threshold). Cyclomatic complexity of all new code ≤ 3. No lint suppressions. No naming violations (`RestoreHistoryEntry` matches `DeleteHistoryEntry` convention). |
| R7 WorkStatus | PASS | TASK-007 listed as "In Review" in Task Progress (line 18); Review Queue entry present (line 116); Session Output entry present (line 145); WorkStatus is 146 lines (within 150-line limit); no new DECs needed (no new dependencies, DAOs, or repo signatures added). Non-blocking: per established practice (see TASK-006), File Registry is missing "Modified / TASK-007" entries for the four MODIFIES files. Not a protocol blocker (P8 strictly targets created files), but recommend adding them at mark-done time consistent with TASK-006 pattern. |

### R3 Detail — coroutine safety of restoreEntry()

`restoreEntry()` is synchronous: it reads `uiState.value.history` (safe — `StateFlow.value` is lock-free, always returns latest) then calls `_localState.update` (thread-safe, atomic). This is called from `onAction()` which is not inside a coroutine scope. The pattern is identical to `append()`, `deleteLast()`, and `clearExpression()`. No coroutine launch is needed or appropriate here. ✓

### R4 Detail — restore test subscription ordering analysis

The question: does the initial `CalculatorUiState()` stateIn value arrive at Turbine before the upstream combine fires?

With `UnconfinedTestDispatcher` as Main, when Turbine subscribes to `uiState`:
1. The subscription triggers `WhileSubscribed` which `launch`-es an upstream collection coroutine.
2. `UnconfinedTestDispatcher` runs launched coroutines eagerly and synchronously until their first suspension point.
3. The upstream `combine(_localState, historyRepository.observeRecent())` collects both `StateFlow` upstreams immediately (both emit their current values without suspending): `_localState` emits `LocalState()`, `_entries.asStateFlow()` emits the seeded list.
4. `combine` fires synchronously, stateIn's internal value is updated to `CalculatorUiState(history=[entry])` before Turbine's collector reads the StateFlow's current value.
5. Turbine therefore sees `CalculatorUiState(history=[entry])` as the first (and only) emission before the `onAction` call.

This means `uiState.value.history` is populated when `onAction(RestoreHistoryEntry(1L))` is called, `restoreEntry(1L)` finds the entry, and `_localState.update` produces the second emission `CalculatorUiState(expression="3*4", result="12", history=[entry])`. The test's second `awaitItem()` gets this state, and all three assertions (`expression=="3*4"`, `result=="12"`, `!isError`) pass. ✓

Non-blocking: this ordering is tightly coupled to `UnconfinedTestDispatcher`'s eager execution semantics. If the dispatcher were changed to `StandardTestDispatcher`, the initial empty state would be emitted first and the test would need an additional `awaitItem()` (or `advanceUntilIdle()`) to drain the intermediate state. The test is correct as written for the current dispatcher setup.

### R6 Detail — HistoryItem clickable nesting

`HistoryItem` has a Row with `.clickable(onClick = onRestore)` wrapping the content. The `TextButton` for delete is nested inside. In Compose, an inner clickable takes precedence over the outer one, so tapping the delete button fires `onDelete` (not `onRestore`), and tapping anywhere else in the row fires `onRestore`. This is the correct interaction model. ✓

### Issues Found
none

### Verdict
**APPROVED**
