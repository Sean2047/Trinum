package dev.trinum.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.trinum.app.feature.calculator.ui.CalculatorScreen
import dev.trinum.app.feature.converter.ui.ConverterScreen
import dev.trinum.app.feature.table.ui.TableScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Calculator.route
    ) {
        composable(Routes.Calculator.route) {
            CalculatorScreen()
        }
        composable(Routes.Converter.route) {
            ConverterScreen()
        }
        composable(Routes.Table.route) {
            TableScreen()
        }
    }
}
