package com.aritradas.medai.ui.presentation.profile

import androidx.lifecycle.ViewModel
import com.aritradas.medai.domain.model.UserData
import com.aritradas.medai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _userData.value = UserData(
                userId = currentUser.uid,
                username = currentUser.displayName,
                profilePictureUrl = currentUser.photoUrl?.toString()
            )
        }
    }
}