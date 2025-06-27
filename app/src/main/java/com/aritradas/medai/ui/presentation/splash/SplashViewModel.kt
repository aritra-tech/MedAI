package com.aritradas.medai.ui.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.AuthRepository
import com.aritradas.medai.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigationDestination = MutableStateFlow<String?>(null)
    val navigationDestination: StateFlow<String?> = _navigationDestination.asStateFlow()

    init {
        checkUserAuthAndNavigate()
    }

    private fun checkUserAuthAndNavigate() {
        viewModelScope.launch {
            // Add a small delay to show splash screen
            delay(1500L)

            val destination = if (isUserSignedIn()) {
                Screens.Prescription.route
            } else {
                Screens.Onboarding.route
            }

            _navigationDestination.value = destination
            _isLoading.value = false
        }
    }

    fun isUserSignedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }

    fun onNavigationComplete() {
        _navigationDestination.value = null
    }
}
