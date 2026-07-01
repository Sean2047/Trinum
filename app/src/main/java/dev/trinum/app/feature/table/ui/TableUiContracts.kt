package dev.trinum.app.feature.table.ui

import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.domain.model.TableCell

data class TableUiState(
    val rows: Int = 5,
    val columns: Int = 5,
    val cells: Map<Pair<Int, Int>, TableCell> = emptyMap(),
    val selectedCell: Pair<Int, Int>? = null,
    val savedTables: List<SavedTable> = emptyList(),
    val currentTableId: Long? = null,
    val currentTableName: String = "",
    val evaluatedResults: Map<Pair<Int, Int>, String> = emptyMap(),
    val isCopyEnabled: Boolean = false,
) {
    val displayCells: Map<Pair<Int, Int>, String>
        get() = cells.mapValues { (coords, cell) ->
            if (cell.isFormula) evaluatedResults[coords] ?: cell.content
            else cell.content
        }
}

sealed class TableUiEffect {
    data class ShowSaveSuccess(val tableName: String) : TableUiEffect()
    data class ShowError(val message: String) : TableUiEffect()
    data class CopyToClipboard(val text: String) : TableUiEffect()
}

sealed class TableUiAction {
    data class SelectCell(val row: Int, val column: Int) : TableUiAction()
    data class UpdateCellContent(val row: Int, val column: Int, val content: String) : TableUiAction()
    data object EvaluateAll : TableUiAction()
    data class SetTableName(val name: String) : TableUiAction()
    data object SaveTable : TableUiAction()
    data class LoadTable(val tableId: Long) : TableUiAction()
    data class DeleteTable(val tableId: Long) : TableUiAction()
    data object NewTable : TableUiAction()
    data object CopyCell : TableUiAction()
}

data class TableUiIntent(
    val cells: Map<Pair<Int, Int>, String>,
    val rows: Int,
    val columns: Int
)
