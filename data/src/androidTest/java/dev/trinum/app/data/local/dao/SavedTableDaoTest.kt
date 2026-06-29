package dev.trinum.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.trinum.app.data.local.db.AppDatabase
import dev.trinum.app.data.local.entity.SavedTableEntity
import dev.trinum.app.data.local.entity.TableCellEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedTableDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SavedTableDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.savedTableDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertTable_and_observeAll_returnsTable() = runTest {
        val table = SavedTableEntity(name = "Budget", rows = 3, columns = 3, createdAt = 0L, updatedAt = 0L)
        dao.insertTable(table)
        val results = dao.observeAll().first()
        assertEquals(1, results.size)
        assertEquals("Budget", results[0].name)
    }

    @Test
    fun getWithCells_returnsTableWithCells() = runTest {
        val table = SavedTableEntity(name = "Test", rows = 2, columns = 2, createdAt = 0L, updatedAt = 0L)
        val tableId = dao.insertTable(table)
        dao.insertCells(
            listOf(
                TableCellEntity(tableId = tableId, row = 0, column = 0, content = "10", isFormula = false),
                TableCellEntity(tableId = tableId, row = 0, column = 1, content = "=A1+5", isFormula = true)
            )
        )
        val result = dao.getWithCells(tableId)
        assertNotNull(result)
        assertEquals(2, result!!.cells.size)
        assertTrue(result.cells.any { it.isFormula })
    }

    @Test
    fun deleteCellsForTable_clearsOnlyCellsOfTable() = runTest {
        val tableId = dao.insertTable(
            SavedTableEntity(name = "T", rows = 1, columns = 1, createdAt = 0L, updatedAt = 0L)
        )
        dao.insertCells(listOf(
            TableCellEntity(tableId = tableId, row = 0, column = 0, content = "5", isFormula = false)
        ))
        dao.deleteCellsForTable(tableId)
        val result = dao.getWithCells(tableId)
        assertNotNull(result)
        assertTrue(result!!.cells.isEmpty())
    }

    @Test
    fun deleteTable_removesTableAndCascadesCells() = runTest {
        val tableId = dao.insertTable(
            SavedTableEntity(name = "ToDelete", rows = 1, columns = 1, createdAt = 0L, updatedAt = 0L)
        )
        dao.insertCells(listOf(
            TableCellEntity(tableId = tableId, row = 0, column = 0, content = "1", isFormula = false)
        ))
        dao.deleteTable(tableId)
        val tables = dao.observeAll().first()
        assertTrue(tables.isEmpty())
        assertNull(dao.getWithCells(tableId))
    }
}
