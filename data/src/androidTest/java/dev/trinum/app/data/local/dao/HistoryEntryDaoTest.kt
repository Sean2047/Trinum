package dev.trinum.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.trinum.app.data.local.db.AppDatabase
import dev.trinum.app.data.local.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryEntryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: HistoryEntryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.historyEntryDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_observeRecent_returnsEntry() = runTest {
        val entry = HistoryEntryEntity(expression = "2+2", result = "4.0", timestamp = 1000L)
        dao.insert(entry)
        val results = dao.observeRecent().first()
        assertEquals(1, results.size)
        assertEquals("2+2", results[0].expression)
        assertEquals("4.0", results[0].result)
    }

    @Test
    fun observeRecent_limitsTo50Entries() = runTest {
        repeat(55) { i ->
            dao.insert(HistoryEntryEntity(expression = "$i+1", result = "${i + 1}", timestamp = i.toLong()))
        }
        val results = dao.observeRecent().first()
        assertEquals(50, results.size)
    }

    @Test
    fun deleteById_removesOnlyTargetEntry() = runTest {
        dao.insert(HistoryEntryEntity(expression = "1+1", result = "2.0", timestamp = 1000L))
        dao.insert(HistoryEntryEntity(expression = "3+3", result = "6.0", timestamp = 2000L))
        val inserted = dao.observeRecent().first()
        dao.deleteById(inserted[1].id)
        val results = dao.observeRecent().first()
        assertEquals(1, results.size)
        assertEquals("3+3", results[0].expression)
    }

    @Test
    fun clearAll_removesAllEntries() = runTest {
        dao.insert(HistoryEntryEntity(expression = "1+1", result = "2.0", timestamp = 1000L))
        dao.insert(HistoryEntryEntity(expression = "2+2", result = "4.0", timestamp = 2000L))
        dao.clearAll()
        val results = dao.observeRecent().first()
        assertTrue(results.isEmpty())
    }
}
