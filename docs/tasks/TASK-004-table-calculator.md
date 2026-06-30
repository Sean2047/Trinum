# TASK-004: Table Calculator Feature
Sprint: 1
Status: Pending

## Objective
Implement the Table Calculator screen: a 5×5 grid supporting literal values and A1-style formula cells evaluated via exp4j, with save/load/delete persistence through `TableRepository`.

## READS
- `app/src/main/java/dev/trinum/app/feature/table/ui/TableUiContracts.kt` — locked contracts: TableUiState (rows, columns, cells, selectedCell, savedTables, currentTableId/Name), TableUiEffect (ShowSaveSuccess, ShowError, CopyToClipboard), TableUiAction (8 variants), TableUiIntent
- `domain/src/main/java/dev/trinum/app/domain/model/TableState.kt` — SavedTable, TableCell, TableState domain models (all fields)
- `domain/src/main/java/dev/trinum/app/domain/repository/TableRepository.kt` — 4 locked signatures: observeAll(), getTableState(), saveTable(), deleteTable()
- `data/src/main/java/dev/trinum/app/data/repository/TableRepositoryImpl.kt` — toDomain / toEntity mapper patterns; how saveTable builds entity from domain
- `app/src/main/java/dev/trinum/app/feature/calculator/EvaluateExpressionUseCase.kt` — UseCase pattern with exp4j ExpressionBuilder; Result<String> return; @Inject constructor
- `app/src/main/java/dev/trinum/app/feature/calculator/CalculatorViewModel.kt` — combine(localStateFlow, repository.observeAll()) pattern; stateIn; viewModelScope.launch for suspend actions; Channel for effects
- `app/src/main/java/dev/trinum/app/feature/calculator/ui/CalculatorScreen.kt` — Screen/Content split pattern; LaunchedEffect(viewModel.effects) for clipboard/snackbar; collectAsState()
- `app/src/main/java/dev/trinum/app/navigation/AppNavGraph.kt` — MODIFIES target; current Table placeholder is `Box(modifier = Modifier.fillMaxSize())`

## CREATES
- `app/src/main/java/dev/trinum/app/feature/table/EvaluateTableUseCase.kt` — `@Inject constructor()`; `operator fun invoke(intent: TableUiIntent): Result<Map<Pair<Int,Int>, String>>`; private A1-ref parser (`parseRef: String → Pair<Int,Int>?`), cell resolver (DFS with `visiting` set for cycle detection, `cache` for memoization), formula substitutor (replace cell refs with resolved doubles, then exp4j evaluate); CELL_REF_REGEX companion constant; MagicNumber-safe: column conversion uses named constants
- `app/src/main/java/dev/trinum/app/feature/table/TableViewModel.kt` — `@HiltViewModel @Inject constructor(evaluateTable: EvaluateTableUseCase, tableRepository: TableRepository)`; private `LocalState(rows, columns, cells: Map<Pair<Int,Int>,TableCell>, selectedCell, currentTableId, currentTableName)`; `uiState = combine(_localState, tableRepository.observeAll()) { local, saved → TableUiState(...) }.stateIn(viewModelScope, WhileSubscribed(5000), TableUiState())`; `onAction` dispatches all 8 UiActions; synchronous actions (SelectCell, UpdateCellContent, EvaluateAll, SetTableName, NewTable) use compute-then-update pattern (DEC-016); suspend actions (SaveTable, LoadTable, DeleteTable) use `viewModelScope.launch { ... }`; EvaluateAll: calls `evaluateTable(TableUiIntent(...))` and on success updates cells map with evaluated values (formula content replaced by result string; isFormula flag preserved — Sprint 1 MVP, formula text not retained post-evaluation); SaveTable builds `TableState` via `buildTableState(LocalState)` helper; LoadTable parses `TableState` to `LocalState` via `buildLocalState(TableState)` helper; DeleteTable sends no effect (list auto-updates via observeAll)
- `app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt` — `TableScreen(viewModel: TableViewModel = hiltViewModel())`; `internal fun TableContent(uiState, onAction, snackbarHostState, modifier)`; `LaunchedEffect(viewModel.effects)` handles ShowSaveSuccess (snackbar), ShowError (snackbar), CopyToClipboard (LocalClipboardManager); `CellGrid(cells, rows, columns, selectedCell, onCellTap)` — `LazyColumn` of rows, each row a `Row` of cells; cell tap dispatches SelectCell; `CellEditBar(selectedCell, cells, onUpdateContent)` — OutlinedTextField for cell content editing; `SavedTablesList(savedTables, onLoad, onDelete)` — LazyColumn of saved table rows; UnitCategory.displayName pattern not needed here (no chips row); `SnackbarHost` for effects
- `app/src/test/java/dev/trinum/app/feature/table/EvaluateTableUseCaseTest.kt` — plain JUnit (no Turbine); covers: literal cell returns numeric string, formula `=A1+B1` evaluates correctly, formula with self-ref (cycle) returns "0" not crash, empty cell reference returns 0, 0 input, invalid formula returns Failure, multi-step resolution (A1 refs B1 which is literal)
- `app/src/test/java/dev/trinum/app/feature/table/TableViewModelTest.kt` — Turbine; `UnconfinedTestDispatcher`; `FakeTableRepository` (implements `TableRepository`, exposes `MutableStateFlow<List<SavedTable>>`); tests: initial state (5×5, empty cells), UpdateCellContent stores content + sets isFormula flag, EvaluateAll updates cell content to evaluated result, SelectCell updates selectedCell, SetTableName updates name, NewTable resets to blank, SaveTable emits ShowSaveSuccess effect, LoadTable populates cells from saved state, DeleteTable calls repository deleteTable
- `app/src/androidTest/java/dev/trinum/app/feature/table/ui/TableScreenTest.kt` — Compose semantics via `TableContent`; covers: cell tap selects it (selectedCell updated in state), evaluated result displays in cell after EvaluateAll, save success snackbar appears, empty table grid renders 5 rows×5 columns

## MODIFIES
- `app/src/main/java/dev/trinum/app/navigation/AppNavGraph.kt` — replace `Box(modifier = Modifier.fillMaxSize())` placeholder in `Routes.Table.route` composable with `TableScreen()`; add import for `TableScreen`; retain Box import only if still used elsewhere (it is not — remove it)

## DELETES
- none

## Evidence
- ADR §1: MVVM — one ViewModel per screen; zero business logic in @Composable
- ADR §1: custom cell-ref resolver for table formula evaluation (exp4j handles arithmetic; resolver handles A1-style refs)
- ADR §1: Repository as data boundary — TableViewModel injects `TableRepository` interface, never `TableRepositoryImpl` or `SavedTableDao` directly (P2, P3)
- ADR §1: Coroutine contract — `combine().stateIn(viewModelScope, WhileSubscribed(5000), initial)` (matches CalculatorViewModel pattern for repos + local state)
- DEC-014: Class E — Screen Feature Tasks require 6 CREATES (UseCase + ViewModel + Screen + 3 tests); P7 exceedance documented
- DEC-016: compute-then-update pattern — synchronous UiActions produce exactly one `_state.update` call; prevents intermediate-state Turbine test failures
- DEC-015 (context): temperature used Kelvin intermediate; similarly, circular cell references resolve to 0 (no crash) per explicit design decision
- **Sprint 1 limitation**: EvaluateAll replaces formula content with evaluated result string in `cells`; formula text is not retained. `TableUiState` contract is locked and has no separate formula-buffer field. Acceptable for Sprint 1; future task would extend `TableUiState` with `formulaBuffer`.

## Definition of Done
- [ ] All CREATES files exist
- [ ] Compile gate passes
- [ ] Tests written per P11 (ViewModel Turbine tests, UseCase unit tests, Screen Compose semantics test)
- [ ] WorkStatus.md updated (Task Progress, File Registry, DECs if any, Session Output)
- [ ] Knowledge Alert output (P17) if triggered
- [ ] Handoff Package submitted for Reviewer
- [ ] Reviewer: APPROVED
- [ ] CI PASS: `./gradlew test detekt ktlintCheck lint`
