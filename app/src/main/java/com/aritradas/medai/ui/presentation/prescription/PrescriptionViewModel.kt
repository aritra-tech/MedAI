package com.aritradas.medai.ui.presentation.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.PrescriptionRepository
import com.aritradas.medai.ui.presentation.prescription.state.PrescriptionListUiState
import com.aritradas.medai.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrescriptionListUiState())
    val uiState: StateFlow<PrescriptionListUiState> = _uiState.asStateFlow()

    fun loadPrescriptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = prescriptionRepository.getSavedPrescriptions()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        prescriptions = result.data ?: emptyList()
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}