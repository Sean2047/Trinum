# TASK-008: Table Copy Cell (TD-003 Resolution)
Sprint: 4
Status: Pending

## Objective
Resolve TD-003 by activating the dead TableUiEffect.CopyToClipboard: add a CopyCell action, implement the handler in TableViewModel, and surface a Copy button in TableActionRow (enabled only when a cell is selected).

## READS
- app/src/main/java/dev/trinum/app/feature/table/ui/TableUiContracts.kt   — add CopyCell action here
- app/src/main/java/dev/trinum/app/feature/table/TableViewModel.kt         — add copyCell() handler
- app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt         — add Copy button to TableActionRow
- app/src/test/java/dev/trinum/app/feature/table/TableViewModelTest.kt     — add CopyCell effect test

## CREATES
- none

## MODIFIES
- app/src/main/java/dev/trinum/app/feature/table/ui/TableUiContracts.kt
    Add: data object CopyCell : TableUiAction()
- app/src/main/java/dev/trinum/app/feature/table/TableViewModel.kt
    Add: is TableUiAction.CopyCell -> copyCell() in onAction
    Add: private fun copyCell() — reads selectedCell from _localState, resolves display value
    (evaluatedResults[coords] ?: cells[coords]?.content), sends TableUiEffect.CopyToClipboard;
    no-op if no cell selected or content is blank
- app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt
    TableActionRow: add Copy OutlinedButton (weight 1f), enabled = uiState.selectedCell != null,
    onClick = { onAction(TableUiAction.CopyCell) }
- app/src/test/java/dev/trinum/app/feature/table/TableViewModelTest.kt
    Add: copy cell sends CopyToClipboard effect with raw content (no evaluation)
    Add: copy cell after evaluate sends CopyToClipboard effect with evaluated result
    Add: copy cell with no selected cell is a no-op (no effect emitted)

## DELETES
- none

## Evidence
- TD-003: TableUiEffect.CopyToClipboard wired in TableScreen but never sent; this task closes it
- ADR P3.3: clipboard is a one-time side effect → must route via UiEffect Channel, not UiState
- ADR P1: action dispatched CopyCell → ViewModel → effect; no business logic in @Composable
- DEC-018 (closed): no new dependencies needed; ClipData/ClipEntry already used in TableScreen

## Definition of Done
- [ ] All CREATES files exist (n/a)
- [ ] Compile gate passes
- [ ] Tests written per P11 (effect test via vm.effects.test{})
- [ ] WorkStatus updated
- [ ] Knowledge Alert output (P17)
- [ ] Handoff Package submitted for Reviewer
- [ ] Reviewer: APPROVED
- [ ] CI PASS
