package com.donyaep.calnotas.ui.navigation

sealed class AppDestination(val route: String) {
    object Home : AppDestination("home")
    object DefaultCalculator : AppDestination("default_calculator")
    object CustomCalculator : AppDestination("custom_calculator")
    object Settings : AppDestination("settings")
    object Help : AppDestination("help")
    object About : AppDestination("about")
    object Update : AppDestination("update")
}
