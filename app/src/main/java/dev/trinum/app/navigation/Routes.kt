package dev.trinum.app.navigation

sealed class Routes(val route: String) {
    data object Calculator : Routes("calculator")
    data object Converter : Routes("converter")
    data object Table : Routes("table")
}
