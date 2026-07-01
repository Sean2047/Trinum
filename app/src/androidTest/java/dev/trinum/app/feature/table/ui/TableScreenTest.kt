package dev.trinum.app.feature.table.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.trinum.app.domain.model.TableCell
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TableScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun action_buttons_are_displayed() {
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("Eval").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("New").assertIsDisplayed()
        composeTestRule.onNodeWithText("Copy").assertIsDisplayed()
    }

    @Test
    fun cell_content_is_displayed_in_grid() {
        val cell = TableCell(id = 0L, tableId = 0L, row = 0, column = 0, content = "42", isFormula = false)
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(cells = mapOf((0 to 0) to cell)),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("42").assertIsDisplayed()
    }

    @Test
    fun tapping_cell_dispatches_SelectCell_action() {
        val actions = mutableListOf<TableUiAction>()
        val cell = TableCell(id = 0L, tableId = 0L, row = 1, column = 0, content = "Tap me", isFormula = false)
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(cells = mapOf((1 to 0) to cell)),
                onAction = { actions.add(it) },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("Tap me").performClick()
        assertTrue(actions.any { it is TableUiAction.SelectCell && it.row == 1 && it.column == 0 })
    }

    @Test
    fun column_and_row_headers_are_displayed() {
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("E").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun copy_button_is_not_enabled_without_selection() {
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("Copy").assertIsNotEnabled()
    }

    @Test
    fun copy_button_is_enabled_when_cell_with_content_is_selected() {
        val cell = TableCell(id = 0L, tableId = 0L, row = 0, column = 0, content = "hello", isFormula = false)
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(cells = mapOf((0 to 0) to cell), selectedCell = 0 to 0, isCopyEnabled = true),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("Copy").assertIsEnabled()
    }

    @Test
    fun copy_button_click_dispatches_CopyCell_action() {
        val actions = mutableListOf<TableUiAction>()
        val cell = TableCell(id = 0L, tableId = 0L, row = 0, column = 0, content = "hello", isFormula = false)
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(cells = mapOf((0 to 0) to cell), selectedCell = 0 to 0, isCopyEnabled = true),
                onAction = { actions.add(it) },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("Copy").performClick()
        assertTrue(actions.any { it is TableUiAction.CopyCell })
    }

    @Test
    fun table_name_is_displayed_in_text_field() {
        composeTestRule.setContent {
            TableContent(
                uiState = TableUiState(currentTableName = "My Budget"),
                onAction = {},
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
        composeTestRule.onNodeWithText("My Budget").assertIsDisplayed()
    }
}
