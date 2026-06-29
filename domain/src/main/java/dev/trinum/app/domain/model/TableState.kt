package dev.trinum.app.domain.model

data class SavedTable(
    val id: Long = 0,
    val name: String,
    val rows: Int,
    val columns: Int,
    val createdAt: Long,
    val updatedAt: Long
)

data class TableCell(
    val id: Long = 0,
    val tableId: Long,
    val row: Int,
    val column: Int,
    val content: String,
    val isFormula: Boolean
)

data class TableState(
    val table: SavedTable,
    val cells: List<TableCell>
)
