package dev.trinum.app.feature.converter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.domain.model.UnitDefinition
import dev.trinum.app.feature.converter.ConverterViewModel

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboard.current
    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ConverterUiEffect.CopyToClipboard ->
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(null, effect.text)))
            }
        }
    }
    ConverterContent(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
internal fun ConverterContent(
    uiState: ConverterUiState,
    onAction: (ConverterUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        CategoryRow(
            selectedCategory = uiState.selectedCategory,
            onSelect = { onAction(ConverterUiAction.SelectCategory(it)) },
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.inputValue,
            onValueChange = { onAction(ConverterUiAction.SetInputValue(it)) },
            label = { Text("Value") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        UnitSelectorRow(
            uiState = uiState,
            onSelectFrom = { onAction(ConverterUiAction.SelectFromUnit(it)) },
            onSelectTo = { onAction(ConverterUiAction.SelectToUnit(it)) },
            onSwap = { onAction(ConverterUiAction.SwapUnits) },
        )
        Spacer(Modifier.height(16.dp))
        ResultSection(
            result = uiState.result,
            isError = uiState.isError,
            onCopy = { onAction(ConverterUiAction.CopyResult) },
        )
    }
}

@Composable
private fun CategoryRow(
    selectedCategory: UnitCategory,
    onSelect: (UnitCategory) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(UnitCategory.entries.toList()) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onSelect(category) },
                label = { Text(category.displayName) },
            )
        }
    }
}

@Composable
private fun UnitSelectorRow(
    uiState: ConverterUiState,
    onSelectFrom: (UnitDefinition) -> Unit,
    onSelectTo: (UnitDefinition) -> Unit,
    onSwap: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        UnitDropdown(
            label = "From",
            selected = uiState.fromUnit,
            options = uiState.availableUnits,
            onSelect = onSelectFrom,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onSwap, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text("⇄", style = MaterialTheme.typography.titleLarge)
        }
        UnitDropdown(
            label = "To",
            selected = uiState.toUnit,
            options = uiState.availableUnits,
            onSelect = onSelectTo,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun UnitDropdown(
    label: String,
    selected: UnitDefinition?,
    options: List<UnitDefinition>,
    onSelect: (UnitDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selected?.displayName ?: label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.displayName) },
                    onClick = { onSelect(unit); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun ResultSection(
    result: String,
    isError: Boolean,
    onCopy: () -> Unit,
) {
    if (result.isEmpty() && !isError) return
    Column {
        Text(
            text = if (isError) "Invalid input" else "= $result",
            style = MaterialTheme.typography.headlineMedium,
        )
        if (result.isNotEmpty() && !isError) {
            TextButton(onClick = onCopy) { Text("Copy result") }
        }
    }
}

private val UnitCategory.displayName: String
    get() = when (this) {
        UnitCategory.LENGTH -> "Length"
        UnitCategory.MASS -> "Mass"
        UnitCategory.TEMPERATURE -> "Temp"
        UnitCategory.VOLUME -> "Volume"
        UnitCategory.AREA -> "Area"
        UnitCategory.SPEED -> "Speed"
        UnitCategory.DIGITAL -> "Digital"
        UnitCategory.TIME -> "Time"
    }
