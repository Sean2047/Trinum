package dev.trinum.app.feature.converter.ui

import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.domain.model.UnitDefinition

data class ConverterUiState(
    val selectedCategory: UnitCategory = UnitCategory.LENGTH,
    val inputValue: String = "",
    val fromUnit: UnitDefinition? = null,
    val toUnit: UnitDefinition? = null,
    val result: String = "",
    val availableUnits: List<UnitDefinition> = emptyList(),
    val isError: Boolean = false
)

sealed class ConverterUiEffect {
    data class CopyToClipboard(val text: String) : ConverterUiEffect()
}

sealed class ConverterUiAction {
    data class SelectCategory(val category: UnitCategory) : ConverterUiAction()
    data class SetInputValue(val value: String) : ConverterUiAction()
    data class SelectFromUnit(val unit: UnitDefinition) : ConverterUiAction()
    data class SelectToUnit(val unit: UnitDefinition) : ConverterUiAction()
    data object CopyResult : ConverterUiAction()
    data object SwapUnits : ConverterUiAction()
}

data class ConverterUiIntent(
    val value: Double,
    val from: UnitDefinition,
    val to: UnitDefinition
)
