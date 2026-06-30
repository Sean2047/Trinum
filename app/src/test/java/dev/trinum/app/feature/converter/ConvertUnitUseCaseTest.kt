package dev.trinum.app.feature.converter

import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.feature.converter.ui.ConverterUiIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertUnitUseCaseTest {

    private val useCase = ConvertUnitUseCase()

    private fun units(category: UnitCategory) = useCase.unitsFor(category)
    private fun unit(category: UnitCategory, id: String) = units(category).first { it.id == id }

    @Test
    fun `meters to kilometers converts correctly`() {
        val from = unit(UnitCategory.LENGTH, "meter")
        val to = unit(UnitCategory.LENGTH, "kilometer")
        val result = useCase(ConverterUiIntent(1_000.0, from, to))
        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull())
    }

    @Test
    fun `kilometers to miles converts correctly`() {
        val from = unit(UnitCategory.LENGTH, "kilometer")
        val to = unit(UnitCategory.LENGTH, "mile")
        val result = useCase(ConverterUiIntent(1.0, from, to))
        assertTrue(result.isSuccess)
        // 1 km = ~0.62137 miles
        val value = result.getOrNull()?.toDouble() ?: 0.0
        assertTrue("Expected ~0.62 miles, got $value", value in 0.621..0.622)
    }

    @Test
    fun `same unit returns identical value`() {
        val m = unit(UnitCategory.LENGTH, "meter")
        val result = useCase(ConverterUiIntent(42.0, m, m))
        assertTrue(result.isSuccess)
        assertEquals("42", result.getOrNull())
    }

    @Test
    fun `celsius to fahrenheit freezing point`() {
        val c = unit(UnitCategory.TEMPERATURE, "celsius")
        val f = unit(UnitCategory.TEMPERATURE, "fahrenheit")
        val result = useCase(ConverterUiIntent(0.0, c, f))
        assertTrue(result.isSuccess)
        assertEquals("32", result.getOrNull())
    }

    @Test
    fun `celsius to fahrenheit boiling point`() {
        val c = unit(UnitCategory.TEMPERATURE, "celsius")
        val f = unit(UnitCategory.TEMPERATURE, "fahrenheit")
        val result = useCase(ConverterUiIntent(100.0, c, f))
        assertTrue(result.isSuccess)
        assertEquals("212", result.getOrNull())
    }

    @Test
    fun `celsius to kelvin converts correctly`() {
        val c = unit(UnitCategory.TEMPERATURE, "celsius")
        val k = unit(UnitCategory.TEMPERATURE, "kelvin")
        val result = useCase(ConverterUiIntent(0.0, c, k))
        assertTrue(result.isSuccess)
        assertEquals("273.15", result.getOrNull())
    }

    @Test
    fun `negative forty celsius equals negative forty fahrenheit`() {
        val c = unit(UnitCategory.TEMPERATURE, "celsius")
        val f = unit(UnitCategory.TEMPERATURE, "fahrenheit")
        val result = useCase(ConverterUiIntent(-40.0, c, f))
        assertTrue(result.isSuccess)
        assertEquals("-40", result.getOrNull())
    }

    @Test
    fun `kilograms to pounds converts correctly`() {
        val kg = unit(UnitCategory.MASS, "kilogram")
        val lb = unit(UnitCategory.MASS, "pound")
        val result = useCase(ConverterUiIntent(1.0, kg, lb))
        assertTrue(result.isSuccess)
        val value = result.getOrNull()?.toDouble() ?: 0.0
        assertTrue("Expected ~2.205 lbs, got $value", value in 2.204..2.206)
    }

    @Test
    fun `zero input returns zero`() {
        val from = unit(UnitCategory.LENGTH, "kilometer")
        val to = unit(UnitCategory.LENGTH, "mile")
        val result = useCase(ConverterUiIntent(0.0, from, to))
        assertTrue(result.isSuccess)
        assertEquals("0", result.getOrNull())
    }

    @Test
    fun `unitsFor returns correct count for each category`() {
        assertEquals(8, useCase.unitsFor(UnitCategory.LENGTH).size)
        assertEquals(6, useCase.unitsFor(UnitCategory.MASS).size)
        assertEquals(3, useCase.unitsFor(UnitCategory.TEMPERATURE).size)
        assertEquals(6, useCase.unitsFor(UnitCategory.VOLUME).size)
        assertEquals(7, useCase.unitsFor(UnitCategory.AREA).size)
        assertEquals(4, useCase.unitsFor(UnitCategory.SPEED).size)
        assertEquals(6, useCase.unitsFor(UnitCategory.DIGITAL).size)
        assertEquals(6, useCase.unitsFor(UnitCategory.TIME).size)
    }
}
