package dev.trinum.app.feature.calculator.ui

import dev.trinum.app.domain.model.HistoryEntry

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "",
    val history: List<HistoryEntry> = emptyList(),
    val isError: Boolean = false
)

sealed class CalculatorUiEffect {
    data class CopyToClipboard(val text: String) : CalculatorUiEffect()
    data object ShowClearHistoryConfirmation : CalculatorUiEffect()
}

sealed class CalculatorUiAction {
    data class AppendToExpression(val symbol: String) : CalculatorUiAction()
    data object DeleteLastChar : CalculatorUiAction()
    data object ClearExpression : CalculatorUiAction()
    data object Evaluate : CalculatorUiAction()
    data object CopyResult : CalculatorUiAction()
    data class DeleteHistoryEntry(val id: Long) : CalculatorUiAction()
    data class RestoreHistoryEntry(val id: Long) : CalculatorUiAction()
    data object ClearHistory : CalculatorUiAction()
}

data class CalculatorUiIntent(val expression: String)
