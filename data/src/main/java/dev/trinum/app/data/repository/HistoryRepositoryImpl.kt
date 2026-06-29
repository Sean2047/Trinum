package dev.trinum.app.data.repository

import dev.trinum.app.data.local.dao.HistoryEntryDao
import dev.trinum.app.data.local.entity.HistoryEntryEntity
import dev.trinum.app.domain.model.HistoryEntry
import dev.trinum.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryEntryDao
) : HistoryRepository {

    override fun observeRecent(): Flow<List<HistoryEntry>> =
        dao.observeRecent().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(entry: HistoryEntry) =
        dao.insert(entry.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)

    override suspend fun clearAll() =
        dao.clearAll()
}

private fun HistoryEntryEntity.toDomain() = HistoryEntry(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)

private fun HistoryEntry.toEntity() = HistoryEntryEntity(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)
