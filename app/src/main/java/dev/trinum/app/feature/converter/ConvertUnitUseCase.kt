package dev.trinum.app.feature.converter

import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.domain.model.UnitDefinition
import dev.trinum.app.feature.converter.ui.ConverterUiIntent
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.floor

class ConvertUnitUseCase @Inject constructor() {

    fun unitsFor(category: UnitCategory): List<UnitDefinition> = CATALOG[category] ?: emptyList()

    operator fun invoke(intent: ConverterUiIntent): Result<String> = runCatching {
        val (value, from, to) = intent
        require(from.category == to.category) { "Category mismatch" }
        val raw = if (from.category == UnitCategory.TEMPERATURE) {
            convertTemperature(value, from.id, to.id)
        } else {
            value * from.toBaseRatio / to.toBaseRatio
        }
        formatResult(raw)
    }

    private fun convertTemperature(value: Double, fromId: String, toId: String): Double {
        val kelvin = when (fromId) {
            "celsius" -> value + KELVIN_CELSIUS_OFFSET
            "fahrenheit" -> (value - FAHRENHEIT_FREEZE) * F_TO_C_RATIO + KELVIN_CELSIUS_OFFSET
            else -> value
        }
        return when (toId) {
            "celsius" -> kelvin - KELVIN_CELSIUS_OFFSET
            "fahrenheit" -> (kelvin - KELVIN_CELSIUS_OFFSET) * C_TO_F_RATIO + FAHRENHEIT_FREEZE
            else -> kelvin
        }
    }

    private fun formatResult(value: Double): String {
        require(value.isFinite()) { "Result is not finite" }
        val longMax = Long.MAX_VALUE.toDouble()
        return if (value == floor(value) && value.absoluteValue <= longMax) {
            value.toLong().toString()
        } else {
            "%.8f".format(value).trimEnd('0').trimEnd('.')
        }
    }

    private companion object {
        private const val KELVIN_CELSIUS_OFFSET = 273.15
        private const val FAHRENHEIT_FREEZE = 32.0
        private const val C_TO_F_RATIO = 9.0 / 5.0
        private const val F_TO_C_RATIO = 5.0 / 9.0

        private fun unitOf(
            id: String,
            cat: UnitCategory,
            name: String,
            ratio: Double,
            base: Boolean = false,
        ) = UnitDefinition(id, cat, name, ratio, base)

        private val CATALOG: Map<UnitCategory, List<UnitDefinition>> = mapOf(
            UnitCategory.LENGTH to listOf(
                unitOf("meter", UnitCategory.LENGTH, "Meter", 1.0, true),
                unitOf("kilometer", UnitCategory.LENGTH, "Kilometer", 1_000.0),
                unitOf("centimeter", UnitCategory.LENGTH, "Centimeter", 0.01),
                unitOf("millimeter", UnitCategory.LENGTH, "Millimeter", 0.001),
                unitOf("mile", UnitCategory.LENGTH, "Mile", 1_609.344),
                unitOf("yard", UnitCategory.LENGTH, "Yard", 0.9144),
                unitOf("foot", UnitCategory.LENGTH, "Foot", 0.3048),
                unitOf("inch", UnitCategory.LENGTH, "Inch", 0.0254),
            ),
            UnitCategory.MASS to listOf(
                unitOf("kilogram", UnitCategory.MASS, "Kilogram", 1.0, true),
                unitOf("gram", UnitCategory.MASS, "Gram", 0.001),
                unitOf("milligram", UnitCategory.MASS, "Milligram", 1e-6),
                unitOf("pound", UnitCategory.MASS, "Pound", 0.45359237),
                unitOf("ounce", UnitCategory.MASS, "Ounce", 0.028349523),
                unitOf("tonne", UnitCategory.MASS, "Tonne", 1_000.0),
            ),
            UnitCategory.TEMPERATURE to listOf(
                unitOf("celsius", UnitCategory.TEMPERATURE, "Celsius", 1.0),
                unitOf("fahrenheit", UnitCategory.TEMPERATURE, "Fahrenheit", 1.0),
                unitOf("kelvin", UnitCategory.TEMPERATURE, "Kelvin", 1.0, true),
            ),
            UnitCategory.VOLUME to listOf(
                unitOf("liter", UnitCategory.VOLUME, "Liter", 1.0, true),
                unitOf("milliliter", UnitCategory.VOLUME, "Milliliter", 0.001),
                unitOf("cubic_meter", UnitCategory.VOLUME, "Cubic Meter", 1_000.0),
                unitOf("gallon_us", UnitCategory.VOLUME, "Gallon (US)", 3.785411784),
                unitOf("pint_us", UnitCategory.VOLUME, "Pint (US)", 0.473176473),
                unitOf("fluid_oz_us", UnitCategory.VOLUME, "Fl. Oz (US)", 0.029573529),
            ),
            UnitCategory.AREA to listOf(
                unitOf("sq_meter", UnitCategory.AREA, "m²", 1.0, true),
                unitOf("sq_kilometer", UnitCategory.AREA, "km²", 1_000_000.0),
                unitOf("sq_centimeter", UnitCategory.AREA, "cm²", 0.0001),
                unitOf("sq_foot", UnitCategory.AREA, "ft²", 0.09290304),
                unitOf("sq_inch", UnitCategory.AREA, "in²", 6.4516e-4),
                unitOf("acre", UnitCategory.AREA, "Acre", 4_046.856422),
                unitOf("hectare", UnitCategory.AREA, "Hectare", 10_000.0),
            ),
            UnitCategory.SPEED to listOf(
                unitOf("mps", UnitCategory.SPEED, "m/s", 1.0, true),
                unitOf("kmh", UnitCategory.SPEED, "km/h", 1.0 / 3.6),
                unitOf("mph", UnitCategory.SPEED, "mph", 0.44704),
                unitOf("knot", UnitCategory.SPEED, "Knot", 0.514444),
            ),
            UnitCategory.DIGITAL to listOf(
                unitOf("byte", UnitCategory.DIGITAL, "Byte", 1.0, true),
                unitOf("bit", UnitCategory.DIGITAL, "Bit", 0.125),
                unitOf("kilobyte", UnitCategory.DIGITAL, "Kilobyte", 1_024.0),
                unitOf("megabyte", UnitCategory.DIGITAL, "Megabyte", 1_048_576.0),
                unitOf("gigabyte", UnitCategory.DIGITAL, "Gigabyte", 1_073_741_824.0),
                unitOf("terabyte", UnitCategory.DIGITAL, "Terabyte", 1_099_511_627_776.0),
            ),
            UnitCategory.TIME to listOf(
                unitOf("second", UnitCategory.TIME, "Second", 1.0, true),
                unitOf("millisecond", UnitCategory.TIME, "Millisecond", 0.001),
                unitOf("minute", UnitCategory.TIME, "Minute", 60.0),
                unitOf("hour", UnitCategory.TIME, "Hour", 3_600.0),
                unitOf("day", UnitCategory.TIME, "Day", 86_400.0),
                unitOf("week", UnitCategory.TIME, "Week", 604_800.0),
            ),
        )
    }
}
