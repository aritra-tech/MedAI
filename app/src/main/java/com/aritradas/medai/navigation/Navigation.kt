package com.aritradas.medai.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aritradas.medai.ui.presentation.onboarding.WelcomeScreen
import com.aritradas.medai.ui.presentation.prescription.PrescriptionScreen
import com.aritradas.medai.ui.presentation.prescriptionDetails.PrescriptionDetailsScreen
import com.aritradas.medai.ui.presentation.prescriptionSummarize.PrescriptionSummarizeScreen
import com.aritradas.medai.ui.presentation.profile.HelpScreen
import com.aritradas.medai.ui.presentation.profile.ProfileScreen
import com.aritradas.medai.ui.presentation.settings.SettingsScreen
import com.aritradas.medai.ui.presentation.splash.SplashScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val bottomBarScreens = listOf(
        Screens.Prescription.route,
        Screens.Profile.route
    )
    
    val showBottomBar = currentRoute in bottomBarScreens

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Splash.route) {
                SplashScreen(
                    navController = navController
                )
            }
            
            composable(Screens.Onboarding.route) {
                WelcomeScreen(
                    navController
                )
            }
            
            composable(Screens.Prescription.route) {
                PrescriptionScreen(
                    navController = navController,
                    navigateToDetailsScreen = { id ->
                        navController.navigate("${Screens.PrescriptionDetails.route}/$id")
                    }
                )
            }

            composable(
                route = "${Screens.PrescriptionDetails.route}/{id}",
                arguments = listOf(navArgument("id") { type = StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""

                PrescriptionDetailsScreen(
                    navController = navController,
                    prescriptionId = id,
                )
            }
            
            composable(Screens.PrescriptionSummarize.route) {
                PrescriptionSummarizeScreen(
                    navController = navController
                )
            }
            
            composable(Screens.Profile.route) {
                ProfileScreen(
                    navController = navController
                )
            }
            
            composable(Screens.Settings.route) {
                SettingsScreen(
                    navController = navController
                )
            }

            composable(Screens.Help.route) {
                HelpScreen(
                    navController = navController
                )
            }
        }
    }
}
