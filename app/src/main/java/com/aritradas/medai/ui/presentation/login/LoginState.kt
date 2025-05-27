package com.aritradas.medai.ui.presentation.login

data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
