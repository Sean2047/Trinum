package dev.trinum.app.feature.calculator.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun result_is_displayed_after_evaluate_action() {
        var uiState by mutableStateOf(CalculatorUiState(expression = "2+3", result = ""))

        composeTestRule.setContent {
            CalculatorContent(
                uiState = uiState,
                onAction = { action ->
                    if (action == CalculatorUiAction.Evaluate) {
                        uiState = uiState.copy(result = "5", isError = false)
                    }
                },
            )
        }

        composeTestRule.onNodeWithContentDescription("=").performClick()
        composeTestRule.onNodeWithText("= 5").assertIsDisplayed()
    }

    @Test
    fun error_state_displays_error_result() {
        composeTestRule.setContent {
            CalculatorContent(
                uiState = CalculatorUiState(expression = "(2+3", result = "Error", isError = true),
                onAction = {},
            )
        }

        composeTestRule.onNodeWithText("= Error").assertIsDisplayed()
    }

    @Test
    fun expression_is_displayed_in_display_area() {
        composeTestRule.setContent {
            CalculatorContent(
                uiState = CalculatorUiState(expression = "42", result = ""),
                onAction = {},
            )
        }

        composeTestRule.onNodeWithText("42").assertIsDisplayed()
    }
}
