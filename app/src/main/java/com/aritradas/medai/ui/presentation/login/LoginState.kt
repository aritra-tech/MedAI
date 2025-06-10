package com.aritradas.medai.ui.presentation.login

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)
