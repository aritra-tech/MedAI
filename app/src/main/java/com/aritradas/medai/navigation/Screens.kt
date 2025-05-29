package com.aritradas.medai.navigation

import com.aritradas.medai.utils.Constants

sealed class Screens(val route: String) {
    data object Splash : Screens(Constants.SPLASH_SCREEN)
    data object Onboarding : Screens(Constants.ONBOARDING_SCREEN)
    data object Login: Screens(Constants.LOGIN_SCREEN)
    data object Prescription: Screens(Constants.PRESCRIPTION_SCREEN)
    data object ScanPrescription: Screens(Constants.SCAN_PRESCRIPTION_SCREEN)
    data object Profile: Screens(Constants.PROFILE_SCREEN)
    data object Settings: Screens(Constants.SETTINGS_SCREEN)
    data object Help: Screens(Constants.HELP_SCREEN)
}