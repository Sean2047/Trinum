package dev.trinum.app.feature.table

import app.cash.turbine.test
import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.domain.model.TableCell
import dev.trinum.app.domain.model.TableState
import dev.trinum.app.domain.repository.TableRepository
import dev.trinum.app.feature.table.ui.TableUiAction
import dev.trinum.app.feature.table.ui.TableUiEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TableViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun createVm(repo: TableRepository = FakeTableRepository()) =
        TableViewModel(EvaluateTableUseCase(), repo)

    @Test
    fun `initial state has 5 rows 5 columns and empty cells`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            val initial = awaitItem()
            assertEquals(5, initial.rows)
            assertEquals(5, initial.columns)
            assertTrue(initial.cells.isEmpty())
            assertTrue(initial.savedTables.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `select cell updates selectedCell`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.SelectCell(row = 1, column = 2))
            val state = awaitItem()
            assertEquals(1 to 2, state.selectedCell)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `update cell content stores content and sets isFormula flag`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 0, content = "=A2+B1"))
            val state = awaitItem()
            val cell = state.cells[0 to 0]
            assertNotNull(cell)
            assertEquals("=A2+B1", cell!!.content)
            assertTrue(cell.isFormula)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `update cell content with literal sets isFormula to false`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 0, content = "99"))
            val state = awaitItem()
            val cell = state.cells[0 to 0]
            assertNotNull(cell)
            assertTrue(!cell!!.isFormula)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `evaluate all replaces formula content with result`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 0, content = "10"))
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 1, content = "20"))
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 2, content = "=A1+B1"))
            awaitItem()
            vm.onAction(TableUiAction.EvaluateAll)
            val state = awaitItem()
            assertEquals("30", state.cells[0 to 2]?.content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `set table name updates currentTableName`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.SetTableName(name = "My Budget"))
            val state = awaitItem()
            assertEquals("My Budget", state.currentTableName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `new table resets to blank state`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.UpdateCellContent(row = 0, column = 0, content = "hello"))
            awaitItem()
            vm.onAction(TableUiAction.NewTable)
            val state = awaitItem()
            assertTrue(state.cells.isEmpty())
            assertEquals("", state.currentTableName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `save table emits ShowSaveSuccess effect`() = runTest {
        val vm = createVm()
        vm.onAction(TableUiAction.SetTableName(name = "Sprint Table"))
        vm.effects.test {
            vm.onAction(TableUiAction.SaveTable)
            val effect = awaitItem()
            assertTrue(effect is TableUiEffect.ShowSaveSuccess)
            assertEquals("Sprint Table", (effect as TableUiEffect.ShowSaveSuccess).tableName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `load table populates cells from saved state`() = runTest {
        val repo = FakeTableRepository()
        val now = System.currentTimeMillis()
        val table = SavedTable(id = 1L, name = "Saved", rows = 5, columns = 5, createdAt = now, updatedAt = now)
        val cell = TableCell(id = 1L, tableId = 1L, row = 0, column = 0, content = "77", isFormula = false)
        repo.seedTableState(TableState(table = table, cells = listOf(cell)))
        val vm = createVm(repo)
        vm.uiState.test {
            awaitItem()
            vm.onAction(TableUiAction.LoadTable(tableId = 1L))
            val state = awaitItem()
            assertEquals("Saved", state.currentTableName)
            assertEquals("77", state.cells[0 to 0]?.content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `delete table calls repository deleteTable`() = runTest {
        val repo = FakeTableRepository()
        val vm = createVm(repo)
        vm.onAction(TableUiAction.DeleteTable(tableId = 1L))
        assertEquals(1L, repo.deletedTableId)
    }
}

private class FakeTableRepository : TableRepository {
    private val _savedTables = MutableStateFlow<List<SavedTable>>(emptyList())
    private val tableStates = mutableMapOf<Long, TableState>()
    var deletedTableId: Long? = null
    private var nextId = 1L

    fun seedTableState(tableState: TableState) {
        tableStates[tableState.table.id] = tableState
        _savedTables.update { it + tableState.table }
    }

    override fun observeAll(): Flow<List<SavedTable>> = _savedTables.asStateFlow()

    override suspend fun getTableState(tableId: Long): TableState? = tableStates[tableId]

    override suspend fun saveTable(tableState: TableState): Long {
        val id = if (tableState.table.id != 0L) tableState.table.id else nextId++
        tableStates[id] = tableState.copy(table = tableState.table.copy(id = id))
        _savedTables.update { it + tableState.table.copy(id = id) }
        return id
    }

    override suspend fun deleteTable(tableId: Long) {
        deletedTableId = tableId
        _savedTables.update { list -> list.filterNot { it.id == tableId } }
    }
}
