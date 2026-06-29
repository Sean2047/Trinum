package dev.trinum.app.domain.model

data class UnitDefinition(
    val id: String,
    val category: UnitCategory,
    val displayName: String,
    val toBaseRatio: Double,
    val isBaseUnit: Boolean = false
)
