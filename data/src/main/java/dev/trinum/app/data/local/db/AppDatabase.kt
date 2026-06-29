package dev.trinum.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.trinum.app.data.local.dao.HistoryEntryDao
import dev.trinum.app.data.local.dao.SavedTableDao
import dev.trinum.app.data.local.entity.HistoryEntryEntity
import dev.trinum.app.data.local.entity.SavedTableEntity
import dev.trinum.app.data.local.entity.TableCellEntity

@Database(
    entities = [
        HistoryEntryEntity::class,
        SavedTableEntity::class,
        TableCellEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyEntryDao(): HistoryEntryDao
    abstract fun savedTableDao(): SavedTableDao
}
