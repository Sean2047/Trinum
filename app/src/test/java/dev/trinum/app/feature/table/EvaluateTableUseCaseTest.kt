package dev.trinum.app.feature.table

import dev.trinum.app.feature.table.ui.TableUiIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluateTableUseCaseTest {

    private val useCase = EvaluateTableUseCase()

    private fun intent(cells: Map<Pair<Int, Int>, String>) =
        TableUiIntent(cells = cells, rows = 5, columns = 5)

    @Test
    fun `literal cell returns its numeric string`() {
        val result = useCase(intent(mapOf((0 to 0) to "42"))).getOrThrow()
        assertEquals("42", result[0 to 0])
    }

    @Test
    fun `zero literal returns zero string`() {
        val result = useCase(intent(mapOf((0 to 0) to "0"))).getOrThrow()
        assertEquals("0", result[0 to 0])
    }

    @Test
    fun `formula A1 plus B1 evaluates sum`() {
        val cells = mapOf(
            (0 to 0) to "10",
            (0 to 1) to "20",
            (0 to 2) to "=A1+B1",
        )
        val result = useCase(intent(cells)).getOrThrow()
        assertEquals("30", result[0 to 2])
    }

    @Test
    fun `circular reference returns zero not crash`() {
        val cells = mapOf(
            (0 to 0) to "=B1",
            (0 to 1) to "=A1",
        )
        val result = useCase(intent(cells)).getOrThrow()
        assertEquals("0", result[0 to 0])
        assertEquals("0", result[0 to 1])
    }

    @Test
    fun `missing cell reference evaluates to zero`() {
        val cells = mapOf((0 to 0) to "=C5")
        val result = useCase(intent(cells)).getOrThrow()
        assertEquals("0", result[0 to 0])
    }

    @Test
    fun `multi-step resolution A refs B which is literal`() {
        val cells = mapOf(
            (0 to 0) to "=B1",
            (0 to 1) to "7",
        )
        val result = useCase(intent(cells)).getOrThrow()
        assertEquals("7", result[0 to 0])
    }

    @Test
    fun `invalid formula expression returns failure`() {
        val cells = mapOf((0 to 0) to "=***")
        val result = useCase(intent(cells))
        assertTrue(result.isFailure)
    }
}
