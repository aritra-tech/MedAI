package com.aritradas.medai.ui.presentation.onboarding

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.aritradas.medai.domain.repository.AuthRepository
import com.aritradas.medai.utils.Resource
import com.aritradas.medai.utils.runIO
import com.aritradas.medai.utils.withIOContext
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    private suspend fun authenticateWithFirebase(idToken: String): Resource<AuthResult> =
        withIOContext {
            Timber.d("Attempting to authenticate with Firebase using Google ID Token.")
            authRepository.signInWithGoogle(idToken)
        }

    fun signInWithGoogle(idToken: String) = runIO {
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                isLoginSuccess = false,
            )
        }

        when (val authResult = authenticateWithFirebase(idToken)) {
            is Resource.Success -> {
                val firebaseUserId = authResult.data?.user?.uid

                if (firebaseUserId.isNullOrBlank()) {
                    handleError("Authentication successful but user ID missing. Please try again.")
                    return@runIO
                }

                // Sign up/login successful, update state to trigger navigation
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isLoginSuccess = true
                    )
                }
            }

            is Resource.Error -> {
                handleError(authResult.message ?: "Google sign-in failed.")
            }

            is Resource.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun handleError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = message,
                isLoginSuccess = false,
            )
        }
    }

    fun onErrorMessageHandled() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetLoginState() {
        Timber.d("Resetting Login State")
        _uiState.update { it.copy(isLoginSuccess = false, error = null) }
    }

    fun logout() = runIO {
        authRepository.signOut()
    }
}
