package dev.trinum.app.feature.calculator.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.trinum.app.domain.model.HistoryEntry
import dev.trinum.app.feature.calculator.CalculatorViewModel

private const val HISTORY_WEIGHT = 0.5f

private val keyRows = listOf(
    listOf("AC", "DEL", "(", ")"),
    listOf("7", "8", "9", "÷"),
    listOf("4", "5", "6", "×"),
    listOf("1", "2", "3", "-"),
    listOf(".", "0", "+", "="),
)

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CalculatorUiEffect.CopyToClipboard ->
                    clipboardManager.setText(AnnotatedString(effect.text))
                CalculatorUiEffect.ShowClearHistoryConfirmation ->
                    snackbarHostState.showSnackbar("History cleared")
            }
        }
    }

    CalculatorContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun CalculatorContent(
    uiState: CalculatorUiState,
    onAction: (CalculatorUiAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            CalculatorDisplay(
                uiState = uiState,
                onCopyResult = { onAction(CalculatorUiAction.CopyResult) },
            )
            CalculatorKeypad(
                onAction = onAction,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
            if (uiState.history.isNotEmpty()) {
                HistorySection(
                    history = uiState.history,
                    onDelete = { onAction(CalculatorUiAction.DeleteHistoryEntry(it)) },
                    onClearAll = { onAction(CalculatorUiAction.ClearHistory) },
                    modifier = Modifier.fillMaxWidth().weight(HISTORY_WEIGHT),
                )
            }
        }
    }
}

@Composable
private fun CalculatorDisplay(
    uiState: CalculatorUiState,
    onCopyResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = uiState.expression.ifEmpty { "0" },
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (uiState.result.isEmpty()) "" else "= ${uiState.result}",
                style = MaterialTheme.typography.displaySmall,
                color = if (uiState.isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(enabled = uiState.result.isNotEmpty() && !uiState.isError) {
                    onCopyResult()
                },
            )
        }
    }
}

@Composable
private fun CalculatorKeypad(
    onAction: (CalculatorUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(4.dp)) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { symbol ->
                    CalcButton(
                        symbol = symbol,
                        onClick = { onAction(symbolToAction(symbol)) },
                        modifier = Modifier.weight(1f).fillMaxHeight().padding(vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = when {
        symbol == "=" -> MaterialTheme.colorScheme.primary
        symbol in listOf("AC", "DEL") -> MaterialTheme.colorScheme.secondaryContainer
        symbol in listOf("+", "-", "×", "÷") -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        symbol == "=" -> MaterialTheme.colorScheme.onPrimary
        symbol in listOf("AC", "DEL") -> MaterialTheme.colorScheme.onSecondaryContainer
        symbol in listOf("+", "-", "×", "÷") -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Button(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = symbol },
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text = symbol, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun HistorySection(
    history: List<HistoryEntry>,
    onDelete: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "History", style = MaterialTheme.typography.labelLarge)
            TextButton(onClick = onClearAll) { Text("Clear All") }
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(history, key = { it.id }) { entry ->
                HistoryItem(entry = entry, onDelete = { onDelete(entry.id) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun HistoryItem(
    entry: HistoryEntry,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.expression, style = MaterialTheme.typography.bodySmall)
            Text(text = "= ${entry.result}", style = MaterialTheme.typography.bodyMedium)
        }
        TextButton(
            onClick = onDelete,
            modifier = Modifier.semantics { contentDescription = "Delete history entry" },
        ) {
            Text("✕")
        }
    }
}

private fun symbolToAction(symbol: String): CalculatorUiAction = when (symbol) {
    "AC" -> CalculatorUiAction.ClearExpression
    "DEL" -> CalculatorUiAction.DeleteLastChar
    "=" -> CalculatorUiAction.Evaluate
    else -> CalculatorUiAction.AppendToExpression(symbol)
}
