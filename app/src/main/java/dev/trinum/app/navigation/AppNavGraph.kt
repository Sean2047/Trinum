package dev.trinum.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.trinum.app.feature.calculator.ui.CalculatorScreen

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
            // Placeholder — implemented in TASK-003 (ConverterScreen)
            Box(modifier = Modifier.fillMaxSize())
        }
        composable(Routes.Table.route) {
            // Placeholder — implemented in TASK-004 (TableScreen)
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
