package dev.trinum.app.domain.repository

import dev.trinum.app.domain.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeRecent(): Flow<List<HistoryEntry>>
    suspend fun insert(entry: HistoryEntry)
    suspend fun deleteById(id: Long)
    suspend fun clearAll()
}
