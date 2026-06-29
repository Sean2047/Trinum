package dev.trinum.app.domain.model

data class HistoryEntry(
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long
)
