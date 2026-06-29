package dev.trinum.app.data.repository

import dev.trinum.app.data.local.dao.SavedTableDao
import dev.trinum.app.data.local.entity.SavedTableEntity
import dev.trinum.app.data.local.entity.TableCellEntity
import dev.trinum.app.data.local.relation.SavedTableWithCells
import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.domain.model.TableCell
import dev.trinum.app.domain.model.TableState
import dev.trinum.app.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TableRepositoryImpl @Inject constructor(
    private val dao: SavedTableDao
) : TableRepository {

    override fun observeAll(): Flow<List<SavedTable>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTableState(tableId: Long): TableState? =
        dao.getWithCells(tableId)?.toDomain()

    override suspend fun saveTable(tableState: TableState): Long {
        val tableId = dao.insertTable(tableState.table.toEntity())
        dao.deleteCellsForTable(tableId)
        dao.insertCells(tableState.cells.map { it.copy(tableId = tableId).toEntity() })
        return tableId
    }

    override suspend fun deleteTable(tableId: Long) =
        dao.deleteTable(tableId)
}

private fun SavedTableWithCells.toDomain() = TableState(
    table = table.toDomain(),
    cells = cells.map { it.toDomain() }
)

private fun SavedTableEntity.toDomain() = SavedTable(
    id = id,
    name = name,
    rows = rows,
    columns = columns,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TableCellEntity.toDomain() = TableCell(
    id = id,
    tableId = tableId,
    row = row,
    column = column,
    content = content,
    isFormula = isFormula
)

private fun SavedTable.toEntity() = SavedTableEntity(
    id = id,
    name = name,
    rows = rows,
    columns = columns,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TableCell.toEntity() = TableCellEntity(
    id = id,
    tableId = tableId,
    row = row,
    column = column,
    content = content,
    isFormula = isFormula
)
