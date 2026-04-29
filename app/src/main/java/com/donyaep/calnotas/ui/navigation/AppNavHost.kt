package com.donyaep.calnotas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.donyaep.calnotas.ui.screens.about.AboutScreen
import com.donyaep.calnotas.ui.screens.customcalculator.CustomCalculatorScreen
import com.donyaep.calnotas.ui.screens.defaultcalculator.DefaultCalculatorScreen
import com.donyaep.calnotas.ui.screens.help.HelpScreen
import com.donyaep.calnotas.ui.screens.home.HomeScreen
import com.donyaep.calnotas.ui.screens.settings.SettingsScreen
import com.donyaep.calnotas.ui.screens.update.UpdateScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(
                onNavigateToDefault = { navController.navigate(AppDestination.DefaultCalculator.route) },
                onNavigateToCustom = { navController.navigate(AppDestination.CustomCalculator.route) },
                onNavigateToSettings = { navController.navigate(AppDestination.Settings.route) },
                onNavigateToHelp = { navController.navigate(AppDestination.Help.route) },
                onNavigateToAbout = { navController.navigate(AppDestination.About.route) },
                onNavigateToUpdate = { navController.navigate(AppDestination.Update.route) }
            )
        }
        composable(AppDestination.DefaultCalculator.route) {
            DefaultCalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(AppDestination.CustomCalculator.route) {
            CustomCalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToHelp = { navController.navigate(AppDestination.Help.route) },
                onNavigateToAbout = { navController.navigate(AppDestination.About.route) },
                onNavigateToUpdate = { navController.navigate(AppDestination.Update.route) }
            )
        }
        composable(AppDestination.Help.route) {
            HelpScreen(onBack = { navController.popBackStack() })
        }
        composable(AppDestination.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(AppDestination.Update.route) {
            UpdateScreen(onBack = { navController.popBackStack() })
        }
    }
}
