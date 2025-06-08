package com.aritradas.medai.ui.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aritradas.medai.utils.runIO
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {

    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val userDB = FirebaseFirestore.getInstance()

    val onLogOutComplete = MutableLiveData<Boolean>()

    val onDeleteAccountComplete = MutableLiveData<Boolean>()
    private val _biometricAuthState = MutableStateFlow(false)
    val biometricAuthState: StateFlow<Boolean> = _biometricAuthState

    fun logout() = runIO {
        FirebaseAuth.getInstance().signOut()
        onLogOutComplete.postValue(true)
    }


    fun deleteAccount() = runIO {
        user?.let {
            userDB.collection("users").document(it.uid).delete()
        }
        onDeleteAccountComplete.postValue(true)
    }
}