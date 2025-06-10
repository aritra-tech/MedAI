package com.aritradas.medai.ui.presentation.splash

import androidx.lifecycle.ViewModel
import com.aritradas.medai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isUserSignedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }
}