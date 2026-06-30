package dev.trinum.app.feature.table.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
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
        composeTestRule.onNodeWithText("Evaluate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("New").assertIsDisplayed()
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
