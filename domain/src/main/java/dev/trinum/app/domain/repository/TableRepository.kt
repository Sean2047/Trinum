package dev.trinum.app.domain.repository

import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.domain.model.TableState
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun observeAll(): Flow<List<SavedTable>>
    suspend fun getTableState(tableId: Long): TableState?
    suspend fun saveTable(tableState: TableState): Long
    suspend fun deleteTable(tableId: Long)
}
