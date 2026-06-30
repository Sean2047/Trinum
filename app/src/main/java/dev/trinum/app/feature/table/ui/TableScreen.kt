package dev.trinum.app.feature.table.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.trinum.app.domain.model.SavedTable
import dev.trinum.app.feature.table.TableViewModel

private val CELL_HEIGHT = 48.dp

@Composable
fun TableScreen(viewModel: TableViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboard = LocalClipboard.current
    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TableUiEffect.ShowSaveSuccess ->
                    snackbarHostState.showSnackbar("Saved: ${effect.tableName}")
                is TableUiEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
                is TableUiEffect.CopyToClipboard ->
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(null, effect.text)))
            }
        }
    }
    TableContent(uiState = uiState, onAction = viewModel::onAction, snackbarHostState = snackbarHostState)
}

@Composable
internal fun TableContent(
    uiState: TableUiState,
    onAction: (TableUiAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, modifier = modifier) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
            OutlinedTextField(
                value = uiState.currentTableName,
                onValueChange = { onAction(TableUiAction.SetTableName(it)) },
                label = { Text("Table name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
            CellEditBar(uiState = uiState, onContentChange = { r, c, v ->
                onAction(TableUiAction.UpdateCellContent(r, c, v))
            })
            Spacer(Modifier.height(8.dp))
            CellGrid(uiState = uiState, onCellTap = { r, c -> onAction(TableUiAction.SelectCell(r, c)) })
            Spacer(Modifier.height(8.dp))
            TableActionRow(onAction = onAction, modifier = Modifier.fillMaxWidth())
            if (uiState.savedTables.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                SavedTablesList(
                    savedTables = uiState.savedTables,
                    onLoad = { onAction(TableUiAction.LoadTable(it)) },
                    onDelete = { onAction(TableUiAction.DeleteTable(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CellGrid(
    uiState: TableUiState,
    onCellTap: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        for (row in 0 until uiState.rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until uiState.columns) {
                    val coords = row to col
                    CellBox(
                        content = uiState.evaluatedResults[coords] ?: uiState.cells[coords]?.content ?: "",
                        isSelected = uiState.selectedCell == coords,
                        onClick = { onCellTap(row, col) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CellBox(
    content: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .height(CELL_HEIGHT)
            .border(width = borderWidth, color = borderColor)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = content, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CellEditBar(
    uiState: TableUiState,
    onContentChange: (Int, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCell = uiState.selectedCell
    val content = selectedCell?.let { uiState.cells[it]?.content } ?: ""
    val label = selectedCell?.let { (r, c) -> "${('A' + c)}${r + 1}" } ?: "No cell selected"
    OutlinedTextField(
        value = content,
        onValueChange = { v -> selectedCell?.let { (r, c) -> onContentChange(r, c, v) } },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        enabled = selectedCell != null,
    )
}

@Composable
private fun TableActionRow(onAction: (TableUiAction) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onAction(TableUiAction.EvaluateAll) }, modifier = Modifier.weight(1f)) {
            Text("Evaluate")
        }
        Button(onClick = { onAction(TableUiAction.SaveTable) }, modifier = Modifier.weight(1f)) {
            Text("Save")
        }
        Button(onClick = { onAction(TableUiAction.NewTable) }, modifier = Modifier.weight(1f)) {
            Text("New")
        }
    }
}

@Composable
private fun SavedTablesList(
    savedTables: List<SavedTable>,
    onLoad: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text("Saved Tables", style = MaterialTheme.typography.labelLarge)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(savedTables, key = { it.id }) { table ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onLoad(table.id) }, modifier = Modifier.weight(1f)) {
                        Text(table.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(onClick = { onDelete(table.id) }) { Text("✕") }
                }
            }
        }
    }
}
