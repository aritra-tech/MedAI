package com.aritradas.medai.ui.presentation.settings

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.MainActivity
import com.aritradas.medai.data.datastore.DataStoreUtil
import com.aritradas.medai.domain.repository.BiometricAuthListener
import com.aritradas.medai.utils.AppBioMetricManager
import com.aritradas.medai.utils.runIO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appBioMetricManager: AppBioMetricManager,
    dataStoreUtil: DataStoreUtil
): ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val userDB = FirebaseFirestore.getInstance()

    private val dataStore = dataStoreUtil.dataStore

    val onLogOutComplete = MutableLiveData<Boolean>()

    val onDeleteAccountComplete = MutableLiveData<Boolean>()
    private val _biometricAuthState = MutableStateFlow(false)
    val biometricAuthState: StateFlow<Boolean> = _biometricAuthState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.map { preferences ->
                preferences[DataStoreUtil.IS_BIOMETRIC_AUTH_SET_KEY] ?: false
            }.collect {
                _biometricAuthState.value = it
            }
        }
    }

    fun showBiometricPrompt(activity: MainActivity) {
        appBioMetricManager.initBiometricPrompt(
            activity = activity,
            listener = object : BiometricAuthListener {
                override fun onBiometricAuthSuccess() {
                    viewModelScope.launch {
                        dataStore.edit { preferences ->
                            preferences[DataStoreUtil.IS_BIOMETRIC_AUTH_SET_KEY] =
                                !_biometricAuthState.value
                        }
                    }
                }

                override fun onUserCancelled() {
                }

                override fun onErrorOccurred() {
                }
            }
        )
    }
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
