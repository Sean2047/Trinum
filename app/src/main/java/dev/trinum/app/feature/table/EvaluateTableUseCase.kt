package dev.trinum.app.feature.table

import dev.trinum.app.feature.table.ui.TableUiIntent
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject
import kotlin.math.floor

class EvaluateTableUseCase @Inject constructor() {

    operator fun invoke(intent: TableUiIntent): Result<Map<Pair<Int, Int>, String>> = runCatching {
        val cache = mutableMapOf<Pair<Int, Int>, Double>()
        intent.cells.keys.associateWith { coords ->
            val content = intent.cells[coords] ?: ""
            formatValue(resolveCell(coords, content, intent.cells, mutableSetOf(), cache))
        }
    }

    private fun resolveCell(
        coords: Pair<Int, Int>,
        content: String,
        cells: Map<Pair<Int, Int>, String>,
        visiting: MutableSet<Pair<Int, Int>>,
        cache: MutableMap<Pair<Int, Int>, Double>,
    ): Double {
        val cached = cache[coords]
        if (cached != null || coords in visiting) return cached ?: 0.0
        val value = computeCellValue(coords, content, cells, visiting, cache)
        cache[coords] = value
        return value
    }

    private fun computeCellValue(
        coords: Pair<Int, Int>,
        content: String,
        cells: Map<Pair<Int, Int>, String>,
        visiting: MutableSet<Pair<Int, Int>>,
        cache: MutableMap<Pair<Int, Int>, Double>,
    ): Double {
        if (!content.startsWith("=")) return content.toDoubleOrNull() ?: 0.0
        visiting.add(coords)
        val result = evaluateFormula(content, cells, visiting, cache)
        visiting.remove(coords)
        return result
    }

    private fun evaluateFormula(
        formula: String,
        cells: Map<Pair<Int, Int>, String>,
        visiting: MutableSet<Pair<Int, Int>>,
        cache: MutableMap<Pair<Int, Int>, Double>,
    ): Double {
        val expr = CELL_REF_REGEX.replace(formula.removePrefix("=")) { match ->
            val refCoords = parseRef(match.value)
            val refContent = refCoords?.let { cells[it] } ?: ""
            if (refCoords != null) resolveCell(refCoords, refContent, cells, visiting, cache).toString()
            else "0"
        }
        return ExpressionBuilder(expr).build().evaluate()
    }

    private fun parseRef(ref: String): Pair<Int, Int>? {
        val match = CELL_REF_REGEX.find(ref) ?: return null
        val col = match.groupValues[1].fold(0) { acc, c -> acc * ALPHABET_SIZE + (c - 'A' + 1) } - 1
        val row = match.groupValues[2].toInt() - 1
        return row to col
    }

    private fun formatValue(value: Double): String {
        if (!value.isFinite()) return "ERR"
        return if (value == floor(value)) value.toLong().toString()
        else "%.8f".format(value).trimEnd('0').trimEnd('.')
    }

    private companion object {
        private const val ALPHABET_SIZE = 26
        private val CELL_REF_REGEX = Regex("([A-Z]+)(\\d+)")
    }
}
