package dev.trinum.app.feature.converter

import app.cash.turbine.test
import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.feature.converter.ui.ConverterUiAction
import dev.trinum.app.feature.converter.ui.ConverterUiEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ConverterViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun createVm() = ConverterViewModel(ConvertUnitUseCase())

    @Test
    fun `initial state has LENGTH category and empty result`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            val initial = awaitItem()
            assertEquals(UnitCategory.LENGTH, initial.selectedCategory)
            assertEquals("", initial.result)
            assertFalse(initial.isError)
            assertEquals(8, initial.availableUnits.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `set input value triggers conversion and emits result`() = runTest {
        val vm = createVm() // initial: meter -> kilometer
        vm.uiState.test {
            awaitItem() // initial
            vm.onAction(ConverterUiAction.SetInputValue("1000"))
            val state = awaitItem()
            assertEquals("1000", state.inputValue)
            assertEquals("1", state.result) // 1000 m = 1 km
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `non-numeric input clears result without error`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem()
            vm.onAction(ConverterUiAction.SetInputValue("abc"))
            val state = awaitItem()
            assertEquals("abc", state.inputValue)
            assertEquals("", state.result)
            assertFalse(state.isError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `select category updates available units`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            awaitItem() // initial (LENGTH)
            vm.onAction(ConverterUiAction.SelectCategory(UnitCategory.MASS))
            val state = awaitItem()
            assertEquals(UnitCategory.MASS, state.selectedCategory)
            assertEquals(6, state.availableUnits.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `swap units exchanges from and to`() = runTest {
        val vm = createVm()
        vm.uiState.test {
            val initial = awaitItem()
            val originalFrom = initial.fromUnit
            val originalTo = initial.toUnit
            vm.onAction(ConverterUiAction.SwapUnits)
            val swapped = awaitItem()
            assertEquals(originalTo, swapped.fromUnit)
            assertEquals(originalFrom, swapped.toUnit)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `swap with value recalculates result`() = runTest {
        val vm = createVm() // initial: meter -> kilometer
        vm.uiState.test {
            awaitItem() // initial state
            vm.onAction(ConverterUiAction.SetInputValue("1000"))
            val afterInput = awaitItem()
            assertEquals("1", afterInput.result) // 1000 m = 1 km
            vm.onAction(ConverterUiAction.SwapUnits) // now kilometer -> meter
            // SwapUnits resets result to "" then recalculates — may emit one or two items
            var afterSwap = awaitItem()
            if (afterSwap.result.isEmpty()) afterSwap = awaitItem()
            assertEquals("1000000", afterSwap.result) // 1000 km = 1 000 000 m
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `copy result emits CopyToClipboard effect`() = runTest {
        val vm = createVm()
        vm.onAction(ConverterUiAction.SetInputValue("1000"))
        vm.effects.test {
            vm.onAction(ConverterUiAction.CopyResult)
            val effect = awaitItem()
            assertTrue(effect is ConverterUiEffect.CopyToClipboard)
            assertEquals("1", (effect as ConverterUiEffect.CopyToClipboard).text)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `copy result does nothing when result is empty`() = runTest {
        val vm = createVm()
        // No input set; result is empty
        vm.effects.test {
            vm.onAction(ConverterUiAction.CopyResult)
            expectNoEvents()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `select from unit recalculates`() = runTest {
        val vm = createVm() // meter -> kilometer
        vm.onAction(ConverterUiAction.SetInputValue("1"))
        val useCase = ConvertUnitUseCase()
        val mile = useCase.unitsFor(UnitCategory.LENGTH).first { it.id == "mile" }
        vm.uiState.test {
            awaitItem() // current state after SetInputValue
            vm.onAction(ConverterUiAction.SelectFromUnit(mile))
            val state = awaitItem()
            assertEquals(mile, state.fromUnit)
            // 1 mile -> kilometer: result should be ~1.609
            val result = state.result.toDoubleOrNull() ?: 0.0
            assertTrue("Expected ~1.609, got $result", result in 1.608..1.610)
            cancelAndConsumeRemainingEvents()
        }
    }
}
