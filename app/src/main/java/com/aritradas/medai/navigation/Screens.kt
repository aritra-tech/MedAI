package com.aritradas.medai.navigation

import com.aritradas.medai.utils.Constants

sealed class Screens(val route: String) {
    data object Login: Screens(Constants.LOGIN_SCREEN)
    data object SignUp : Screens(Constants.SIGNUP_SCREEN)
    data object Forgot: Screens(Constants.FORGOT_PASSWORD_SCREEN)
    data object Onboarding : Screens(Constants.ONBOARDING_SCREEN)
    data object Prescription: Screens(Constants.PRESCRIPTION_SCREEN)
    data object PrescriptionDetails: Screens(Constants.PRESCRIPTION_DETAILS_SCREEN)
    data object PrescriptionSummarize: Screens(Constants.PRESCRIPTION_SUMMARIZE_SCREEN)
    data object Profile: Screens(Constants.PROFILE_SCREEN)
    data object Settings: Screens(Constants.SETTINGS_SCREEN)
    data object Help: Screens(Constants.HELP_SCREEN)
}
