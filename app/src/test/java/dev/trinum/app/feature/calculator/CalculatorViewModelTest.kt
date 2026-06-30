package dev.trinum.app.feature.calculator

import app.cash.turbine.test
import dev.trinum.app.domain.model.HistoryEntry
import dev.trinum.app.domain.repository.HistoryRepository
import dev.trinum.app.feature.calculator.ui.CalculatorUiAction
import dev.trinum.app.feature.calculator.ui.CalculatorUiEffect
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() { Dispatchers.setMain(testDispatcher) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private fun createVm(repo: HistoryRepository = FakeHistoryRepository()) =
        CalculatorViewModel(EvaluateExpressionUseCase(), repo)

    @Test
    fun `initial state has empty expression and no history`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            val initial = awaitItem()
            assertEquals("", initial.expression)
            assertEquals("", initial.result)
            assertFalse(initial.isError)
            assertTrue(initial.history.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `append symbol updates expression`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem() // initial
            vm.onAction(CalculatorUiAction.AppendToExpression("5"))
            assertEquals("5", awaitItem().expression)
            vm.onAction(CalculatorUiAction.AppendToExpression("+"))
            assertEquals("5+", awaitItem().expression)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `delete last char removes trailing character`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("5"))
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("+"))
            awaitItem()
            vm.onAction(CalculatorUiAction.DeleteLastChar)
            assertEquals("5", awaitItem().expression)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clear expression resets local state`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("99"))
            awaitItem()
            vm.onAction(CalculatorUiAction.ClearExpression)
            val cleared = awaitItem()
            assertEquals("", cleared.expression)
            assertEquals("", cleared.result)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `evaluate valid expression sets result`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem() // initial
            vm.onAction(CalculatorUiAction.AppendToExpression("2+3"))
            awaitItem() // expression updated
            vm.onAction(CalculatorUiAction.Evaluate)
            // With UnconfinedTestDispatcher, localState and history may be combined into one emission
            var state = awaitItem()
            if (state.result.isEmpty()) state = awaitItem()
            assertEquals("5", state.result)
            assertFalse(state.isError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `evaluate inserts entry into history repository`() = runTest {
        val repo = FakeHistoryRepository()
        val vm = createVm(repo)
        vm.onAction(CalculatorUiAction.AppendToExpression("2+3"))
        vm.onAction(CalculatorUiAction.Evaluate)
        assertEquals(1, repo.entryCount())
        assertEquals("2+3", repo.getEntries().first().expression)
        assertEquals("5", repo.getEntries().first().result)
    }

    @Test
    fun `evaluate invalid expression sets isError true`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("2"))
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("+"))
            awaitItem()
            vm.onAction(CalculatorUiAction.AppendToExpression("+"))
            awaitItem()
            vm.onAction(CalculatorUiAction.Evaluate)
            val errorState = awaitItem()
            assertTrue(errorState.isError)
            assertEquals("Error", errorState.result)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `copy result emits CopyToClipboard effect`() = runTest {
        val vm = createVm()
        vm.onAction(CalculatorUiAction.AppendToExpression("5"))
        vm.onAction(CalculatorUiAction.Evaluate)
        vm.effects.test {
            vm.onAction(CalculatorUiAction.CopyResult)
            val effect = awaitItem()
            assertTrue(effect is CalculatorUiEffect.CopyToClipboard)
            assertEquals("5", (effect as CalculatorUiEffect.CopyToClipboard).text)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clear history calls clearAll and emits confirmation effect`() = runTest {
        val repo = FakeHistoryRepository()
        val vm = createVm(repo)
        repo.seedEntry(HistoryEntry(id = 1L, expression = "1+1", result = "2", timestamp = 0L))
        vm.effects.test {
            vm.onAction(CalculatorUiAction.ClearHistory)
            assertEquals(CalculatorUiEffect.ShowClearHistoryConfirmation, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        assertTrue(repo.isEmpty())
    }

    @Test
    fun `restore history entry sets expression and result`() = runTest {
        val repo = FakeHistoryRepository()
        repo.seedEntry(HistoryEntry(id = 1L, expression = "3*4", result = "12", timestamp = 0L))
        val vm = createVm(repo)
        vm.uiState.test {
            awaitItem() // initial state with seeded history
            vm.onAction(CalculatorUiAction.RestoreHistoryEntry(1L))
            val state = awaitItem()
            assertEquals("3*4", state.expression)
            assertEquals("12", state.result)
            assertFalse(state.isError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `delete entry removes it from repository`() = runTest {
        val repo = FakeHistoryRepository()
        repo.seedEntry(HistoryEntry(id = 1L, expression = "1+1", result = "2", timestamp = 0L))
        val vm = createVm(repo)
        vm.onAction(CalculatorUiAction.DeleteHistoryEntry(1L))
        assertTrue(repo.isEmpty())
    }
}

private class FakeHistoryRepository : HistoryRepository {
    private val _entries = MutableStateFlow<List<HistoryEntry>>(emptyList())

    fun seedEntry(entry: HistoryEntry) { _entries.update { it + entry } }
    fun isEmpty(): Boolean = _entries.value.isEmpty()
    fun entryCount(): Int = _entries.value.size
    fun getEntries(): List<HistoryEntry> = _entries.value

    override fun observeRecent(): Flow<List<HistoryEntry>> = _entries.asStateFlow()

    override suspend fun insert(entry: HistoryEntry) {
        _entries.update { it + entry }
    }

    override suspend fun deleteById(id: Long) {
        _entries.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun clearAll() {
        _entries.update { emptyList() }
    }
}
