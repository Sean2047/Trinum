package dev.trinum.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.trinum.app.core.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        composeRule.setContent {
            AppTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                    Box(Modifier.padding(padding)) {
                        NavHost(navController, startDestination = Routes.Calculator.route) {
                            composable(Routes.Calculator.route) {}
                            composable(Routes.Converter.route) {}
                            composable(Routes.Table.route) {}
                        }
                    }
                }
            }
        }
    }

    @Test
    fun all_tab_labels_are_displayed() {
        composeRule.onNodeWithText("Calculator").assertIsDisplayed()
        composeRule.onNodeWithText("Converter").assertIsDisplayed()
        composeRule.onNodeWithText("Table").assertIsDisplayed()
    }

    @Test
    fun tapping_converter_tab_selects_it() {
        composeRule.onNodeWithText("Converter").performClick()
        composeRule.onNodeWithText("Converter").assertIsSelected()
    }

    @Test
    fun tapping_table_tab_selects_it() {
        composeRule.onNodeWithText("Table").performClick()
        composeRule.onNodeWithText("Table").assertIsSelected()
    }
}
