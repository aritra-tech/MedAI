package com.aritradas.medai.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object Login : Screens()

    @Serializable
    data object SignUp : Screens()

    @Serializable
    data object Forgot : Screens()

    @Serializable
    data object Onboarding : Screens()

    @Serializable
    data object Prescription : Screens()

    @Serializable
    data class PrescriptionDetails(val id: String) : Screens()

    @Serializable
    data class PrescriptionSummarize(val hasCameraPermission: Boolean = false) : Screens()

    @Serializable
    data object MedicalReport: Screens()

    @Serializable
    data object Profile : Screens()

    @Serializable
    data object Settings : Screens()

    @Serializable
    data object Help : Screens()

    @Serializable
    data object Loading : Screens()
}
