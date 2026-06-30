package dev.trinum.app.feature.converter.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.trinum.app.domain.model.UnitCategory
import dev.trinum.app.domain.model.UnitDefinition
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun result_displays_after_conversion() {
        val meter = UnitDefinition("meter", UnitCategory.LENGTH, "Meter", 1.0, true)
        val kilometer = UnitDefinition("kilometer", UnitCategory.LENGTH, "Kilometer", 1_000.0)
        composeTestRule.setContent {
            ConverterContent(
                uiState = ConverterUiState(
                    selectedCategory = UnitCategory.LENGTH,
                    inputValue = "1000",
                    fromUnit = meter,
                    toUnit = kilometer,
                    result = "1",
                    availableUnits = listOf(meter, kilometer),
                ),
                onAction = {},
            )
        }
        composeTestRule.onNodeWithText("= 1").assertIsDisplayed()
    }

    @Test
    fun error_state_displays_invalid_input_message() {
        composeTestRule.setContent {
            ConverterContent(
                uiState = ConverterUiState(isError = true, result = ""),
                onAction = {},
            )
        }
        composeTestRule.onNodeWithText("Invalid input").assertIsDisplayed()
    }

    @Test
    fun category_chips_are_displayed() {
        composeTestRule.setContent {
            ConverterContent(uiState = ConverterUiState(), onAction = {})
        }
        composeTestRule.onNodeWithText("Length").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mass").assertIsDisplayed()
    }
}
