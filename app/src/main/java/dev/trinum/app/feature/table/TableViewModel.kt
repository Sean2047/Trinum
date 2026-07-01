package dev.trinum.app.feature.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.domain.model.TableCell
import dev.trinum.app.domain.model.TableState
import dev.trinum.app.domain.repository.TableRepository
import dev.trinum.app.feature.table.ui.TableUiAction
import dev.trinum.app.feature.table.ui.TableUiEffect
import dev.trinum.app.feature.table.ui.TableUiIntent
import dev.trinum.app.feature.table.ui.TableUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val evaluateTable: EvaluateTableUseCase,
    private val tableRepository: TableRepository,
) : ViewModel() {

    private data class LocalState(
        val rows: Int = 5,
        val columns: Int = 5,
        val cells: Map<Pair<Int, Int>, TableCell> = emptyMap(),
        val selectedCell: Pair<Int, Int>? = null,
        val currentTableId: Long? = null,
        val currentTableName: String = "",
        val createdAt: Long = 0L,
        val evaluatedResults: Map<Pair<Int, Int>, String> = emptyMap(),
    )

    private val _localState = MutableStateFlow(LocalState())
    private val _effects = Channel<TableUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var loadJob: Job? = null

    val uiState: StateFlow<TableUiState> = combine(
        _localState,
        tableRepository.observeAll(),
    ) { local, saved ->
        TableUiState(
            rows = local.rows,
            columns = local.columns,
            cells = local.cells,
            selectedCell = local.selectedCell,
            savedTables = saved,
            currentTableId = local.currentTableId,
            currentTableName = local.currentTableName,
            evaluatedResults = local.evaluatedResults,
            isCopyEnabled = local.selectedCell?.let { coords ->
                local.cells[coords]?.let { cell ->
                    resolvedCopyText(cell, coords, local.evaluatedResults) != null
                }
            } ?: false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), TableUiState())

    fun onAction(action: TableUiAction) {
        when (action) {
            is TableUiAction.SelectCell -> selectCell(action.row, action.column)
            is TableUiAction.UpdateCellContent -> updateCellContent(action.row, action.column, action.content)
            TableUiAction.EvaluateAll -> evaluateAll()
            is TableUiAction.SetTableName -> setTableName(action.name)
            TableUiAction.SaveTable -> saveTable()
            is TableUiAction.LoadTable -> loadTable(action.tableId)
            is TableUiAction.DeleteTable -> deleteTable(action.tableId)
            TableUiAction.NewTable -> newTable()
            TableUiAction.CopyCell -> copyCell()
        }
    }

    private fun selectCell(row: Int, col: Int) {
        _localState.update { it.copy(selectedCell = row to col) }
    }

    private fun updateCellContent(row: Int, col: Int, content: String) {
        val state = _localState.value
        val coords = row to col
        val isFormula = content.startsWith("=")
        val existing = state.cells[coords]
        val updated = existing?.copy(content = content, isFormula = isFormula)
            ?: TableCell(
                id = 0,
                tableId = state.currentTableId ?: 0L,
                row = row,
                column = col,
                content = content,
                isFormula = isFormula,
            )
        _localState.update { it.copy(cells = it.cells + (coords to updated), evaluatedResults = emptyMap()) }
    }

    private fun evaluateAll() {
        val state = _localState.value
        val intent = TableUiIntent(
            cells = state.cells.mapValues { it.value.content },
            rows = state.rows,
            columns = state.columns,
        )
        evaluateTable(intent).onSuccess { results ->
            _localState.update { it.copy(evaluatedResults = results) }
        }.onFailure {
            viewModelScope.launch { _effects.send(TableUiEffect.ShowError("Evaluation failed")) }
        }
    }

    private fun setTableName(name: String) {
        _localState.update { it.copy(currentTableName = name) }
    }

    private fun saveTable() {
        val state = _localState.value
        viewModelScope.launch {
            runCatching { tableRepository.saveTable(buildTableState(state)) }
                .fold(
                    onSuccess = { id ->
                        _localState.update { it.copy(currentTableId = id) }
                        _effects.send(TableUiEffect.ShowSaveSuccess(state.currentTableName))
                    },
                    onFailure = { _effects.send(TableUiEffect.ShowError("Save failed")) },
                )
        }
    }

    private fun loadTable(tableId: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val tableState = tableRepository.getTableState(tableId)
            if (tableState != null) {
                _localState.update { buildLocalState(tableState) }
            } else {
                _effects.send(TableUiEffect.ShowError("Table not found"))
            }
        }
    }

    private fun deleteTable(tableId: Long) {
        viewModelScope.launch { tableRepository.deleteTable(tableId) }
    }

    private fun newTable() {
        _localState.update { LocalState() }
    }

    private fun copyCell() {
        val state = _localState.value
        val coords = state.selectedCell ?: return
        val cell = state.cells[coords] ?: return
        val text = resolvedCopyText(cell, coords, state.evaluatedResults)
        viewModelScope.launch {
            if (text != null) {
                _effects.send(TableUiEffect.CopyToClipboard(text))
            } else if (cell.isFormula) {
                _effects.send(TableUiEffect.ShowError("Evaluate formulas before copying"))
            }
        }
    }

    companion object {
        private fun resolvedCopyText(
            cell: TableCell,
            coords: Pair<Int, Int>,
            evaluatedResults: Map<Pair<Int, Int>, String>,
        ): String? = if (cell.isFormula) {
            evaluatedResults[coords]?.takeIf { it.isNotBlank() && it != EvaluateTableUseCase.ERROR_VALUE }
        } else {
            cell.content.takeIf { it.isNotBlank() }
        }

        private fun buildTableState(state: LocalState): TableState {
            val now = System.currentTimeMillis()
            val table = SavedTable(
                id = state.currentTableId ?: 0L,
                name = state.currentTableName.ifBlank { "Untitled" },
                rows = state.rows,
                columns = state.columns,
                createdAt = if (state.createdAt > 0L) state.createdAt else now,
                updatedAt = now,
            )
            return TableState(table = table, cells = state.cells.values.toList())
        }

        private fun buildLocalState(tableState: TableState): LocalState {
            val cellsMap = tableState.cells.associateBy { it.row to it.column }
            return LocalState(
                rows = tableState.table.rows,
                columns = tableState.table.columns,
                cells = cellsMap,
                currentTableId = tableState.table.id,
                currentTableName = tableState.table.name,
                createdAt = tableState.table.createdAt,
            )
        }
    }
}
