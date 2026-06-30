package dev.trinum.app.feature.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.domain.model.UnitDefinition
import dev.trinum.app.feature.converter.ui.ConverterUiAction
import dev.trinum.app.feature.converter.ui.ConverterUiEffect
import dev.trinum.app.feature.converter.ui.ConverterUiIntent
import dev.trinum.app.feature.converter.ui.ConverterUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val convertUnit: ConvertUnitUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    private val _effects = Channel<ConverterUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<ConverterUiState> = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), _state.value)

    fun onAction(action: ConverterUiAction) {
        when (action) {
            is ConverterUiAction.SelectCategory -> selectCategory(action.category)
            is ConverterUiAction.SetInputValue -> setInput(action.value)
            is ConverterUiAction.SelectFromUnit -> selectFrom(action.unit)
            is ConverterUiAction.SelectToUnit -> selectTo(action.unit)
            ConverterUiAction.SwapUnits -> swapUnits()
            ConverterUiAction.CopyResult -> copyResult()
        }
    }

    private fun selectCategory(category: UnitCategory) {
        val units = convertUnit.unitsFor(category)
        val from = units.firstOrNull()
        val to = units.getOrNull(1)
        val (result, isError) = computeResult(_state.value.inputValue, from, to)
        _state.update {
            it.copy(
                selectedCategory = category,
                availableUnits = units,
                fromUnit = from,
                toUnit = to,
                result = result,
                isError = isError,
            )
        }
    }

    private fun setInput(value: String) {
        val (result, isError) = computeResult(value, _state.value.fromUnit, _state.value.toUnit)
        _state.update { it.copy(inputValue = value, result = result, isError = isError) }
    }

    private fun selectFrom(unit: UnitDefinition) {
        val (result, isError) = computeResult(_state.value.inputValue, unit, _state.value.toUnit)
        _state.update { it.copy(fromUnit = unit, result = result, isError = isError) }
    }

    private fun selectTo(unit: UnitDefinition) {
        val (result, isError) = computeResult(_state.value.inputValue, _state.value.fromUnit, unit)
        _state.update { it.copy(toUnit = unit, result = result, isError = isError) }
    }

    private fun swapUnits() {
        val state = _state.value
        val (result, isError) = computeResult(state.inputValue, state.toUnit, state.fromUnit)
        _state.update { it.copy(fromUnit = state.toUnit, toUnit = state.fromUnit, result = result, isError = isError) }
    }

    private fun copyResult() {
        val state = _state.value
        if (state.result.isNotEmpty() && !state.isError) {
            viewModelScope.launch { _effects.send(ConverterUiEffect.CopyToClipboard(state.result)) }
        }
    }

    private fun computeResult(
        inputValue: String,
        from: UnitDefinition?,
        to: UnitDefinition?,
    ): Pair<String, Boolean> {
        val value = inputValue.toDoubleOrNull()
        if (value == null || from == null || to == null) return "" to false
        return convertUnit(ConverterUiIntent(value, from, to)).fold(
            onSuccess = { result -> result to false },
            onFailure = { error ->
                Timber.d(error, "Conversion failed")
                "" to true
            },
        )
    }

    private fun buildInitialState(): ConverterUiState {
        val units = convertUnit.unitsFor(UnitCategory.LENGTH)
        return ConverterUiState(
            selectedCategory = UnitCategory.LENGTH,
            availableUnits = units,
            fromUnit = units.firstOrNull(),
            toUnit = units.getOrNull(1),
        )
    }
}
