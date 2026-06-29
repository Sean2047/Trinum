package dev.trinum.app.feature.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluateExpressionUseCaseTest {

    private val useCase = EvaluateExpressionUseCase()

    @Test
    fun `simple addition returns correct integer result`() {
        val result = useCase("2+3")
        assertTrue(result.isSuccess)
        assertEquals("5", result.getOrNull())
    }

    @Test
    fun `subtraction returns correct result`() {
        val result = useCase("10-4")
        assertTrue(result.isSuccess)
        assertEquals("6", result.getOrNull())
    }

    @Test
    fun `multiplication returns correct result`() {
        val result = useCase("3*4")
        assertTrue(result.isSuccess)
        assertEquals("12", result.getOrNull())
    }

    @Test
    fun `division with integer result`() {
        val result = useCase("10/2")
        assertTrue(result.isSuccess)
        assertEquals("5", result.getOrNull())
    }

    @Test
    fun `times symbol is converted and evaluated`() {
        val result = useCase("3×4")
        assertTrue(result.isSuccess)
        assertEquals("12", result.getOrNull())
    }

    @Test
    fun `divide symbol is converted and evaluated`() {
        val result = useCase("10÷2")
        assertTrue(result.isSuccess)
        assertEquals("5", result.getOrNull())
    }

    @Test
    fun `complex expression evaluates correctly`() {
        val result = useCase("2+3*4")
        assertTrue(result.isSuccess)
        assertEquals("14", result.getOrNull())
    }

    @Test
    fun `parentheses override precedence`() {
        val result = useCase("(2+3)*4")
        assertTrue(result.isSuccess)
        assertEquals("20", result.getOrNull())
    }

    @Test
    fun `invalid expression returns failure`() {
        // Unmatched parenthesis is truly invalid (not unary-operator ambiguous)
        val result = useCase("(2+3")
        assertTrue(result.isFailure)
    }

    @Test
    fun `division by zero returns failure`() {
        val result = useCase("1/0")
        assertTrue(result.isFailure)
    }

    @Test
    fun `empty expression returns failure`() {
        val result = useCase("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `decimal result strips trailing zeros`() {
        val result = useCase("1/4")
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()?.endsWith("0") ?: false)
    }
}
