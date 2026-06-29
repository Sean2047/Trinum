package dev.trinum.app.feature.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.trinum.app.domain.model.HistoryEntry
import dev.trinum.app.domain.repository.HistoryRepository
import dev.trinum.app.feature.calculator.ui.CalculatorUiAction
import dev.trinum.app.feature.calculator.ui.CalculatorUiEffect
import dev.trinum.app.feature.calculator.ui.CalculatorUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val evaluateExpression: EvaluateExpressionUseCase,
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    private data class LocalState(
        val expression: String = "",
        val result: String = "",
        val isError: Boolean = false,
    )

    private val _localState = MutableStateFlow(LocalState())
    private val _effects = Channel<CalculatorUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<CalculatorUiState> = combine(
        _localState,
        historyRepository.observeRecent(),
    ) { local, history ->
        CalculatorUiState(
            expression = local.expression,
            result = local.result,
            isError = local.isError,
            history = history,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), CalculatorUiState())

    fun onAction(action: CalculatorUiAction) {
        when (action) {
            is CalculatorUiAction.AppendToExpression -> append(action.symbol)
            CalculatorUiAction.DeleteLastChar -> deleteLast()
            CalculatorUiAction.ClearExpression -> clearExpression()
            CalculatorUiAction.Evaluate -> evaluate()
            CalculatorUiAction.CopyResult -> copyResult()
            is CalculatorUiAction.DeleteHistoryEntry -> deleteEntry(action.id)
            CalculatorUiAction.ClearHistory -> clearHistory()
        }
    }

    private fun append(symbol: String) {
        _localState.update { it.copy(expression = it.expression + symbol, result = "", isError = false) }
    }

    private fun deleteLast() {
        _localState.update { it.copy(expression = it.expression.dropLast(1), result = "", isError = false) }
    }

    private fun clearExpression() {
        _localState.update { LocalState() }
    }

    private fun evaluate() {
        val expr = _localState.value.expression
        if (expr.isBlank()) return
        viewModelScope.launch {
            evaluateExpression(expr).fold(
                onSuccess = { value ->
                    _localState.update { it.copy(result = value, isError = false) }
                    historyRepository.insert(
                        HistoryEntry(expression = expr, result = value, timestamp = System.currentTimeMillis()),
                    )
                },
                onFailure = { error ->
                    Timber.d(error, "Expression evaluation failed")
                    _localState.update { it.copy(result = "Error", isError = true) }
                },
            )
        }
    }

    private fun copyResult() {
        val local = _localState.value
        if (local.result.isNotEmpty() && !local.isError) {
            viewModelScope.launch { _effects.send(CalculatorUiEffect.CopyToClipboard(local.result)) }
        }
    }

    private fun deleteEntry(id: Long) {
        viewModelScope.launch { historyRepository.deleteById(id) }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
            _effects.send(CalculatorUiEffect.ShowClearHistoryConfirmation)
        }
    }
}
