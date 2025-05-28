package com.aritradas.medai.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aritradas.medai.ui.presentation.login.GoogleAuthUiClient
import com.aritradas.medai.ui.presentation.login.LoginScreen
import com.aritradas.medai.ui.presentation.splash.SplashScreen

@Composable
fun Navigation(googleAuthUiClient: GoogleAuthUiClient) {

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
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient
                )
            }
            
            composable(Screens.Onboarding.route) {
                // Onboarding screen composable
            }
            
            composable(Screens.Login.route) {
                LoginScreen(
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient
                )
            }
            
            composable(Screens.Prescription.route) {
                // Prescription screen composable
            }
            
            composable(Screens.ScanPrescription.route) {
                // Scan prescription screen composable
            }
            
            composable(Screens.Profile.route) {
                // Profile screen composable
            }
            
            composable(Screens.Settings.route) {
                // Settings screen composable
            }
        }
    }
}
