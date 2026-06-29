package dev.trinum.app.feature.calculator

import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject

class EvaluateExpressionUseCase @Inject constructor() {

    operator fun invoke(expression: String): Result<String> = runCatching {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
        val value = ExpressionBuilder(sanitized).build().evaluate()
        require(!value.isInfinite() && !value.isNaN()) { "Result is not a finite number" }
        if (value == kotlin.math.floor(value) && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            value.toBigDecimal().stripTrailingZeros().toPlainString()
        }
    }
}
