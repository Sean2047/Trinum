package dev.trinum.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.trinum.app.data.local.entity.SavedTableEntity
import dev.trinum.app.data.local.entity.TableCellEntity
import dev.trinum.app.data.local.relation.SavedTableWithCells
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTableDao {

    @Query("SELECT * FROM saved_tables ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<SavedTableEntity>>

    @Transaction
    @Query("SELECT * FROM saved_tables WHERE id = :tableId")
    suspend fun getWithCells(tableId: Long): SavedTableWithCells?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: SavedTableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCells(cells: List<TableCellEntity>)

    @Query("DELETE FROM table_cells WHERE table_id = :tableId")
    suspend fun deleteCellsForTable(tableId: Long)

    @Query("DELETE FROM saved_tables WHERE id = :tableId")
    suspend fun deleteTable(tableId: Long)
}
