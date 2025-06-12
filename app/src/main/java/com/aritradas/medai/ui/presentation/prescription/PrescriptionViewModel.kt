package com.aritradas.medai.ui.presentation.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.AuthRepository
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
    private val prescriptionRepository: PrescriptionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrescriptionListUiState())
    val uiState: StateFlow<PrescriptionListUiState> = _uiState.asStateFlow()

    fun loadPrescriptions() {
        viewModelScope.launch {
            // Check if user is authenticated before trying to load prescriptions
            if (authRepository.getCurrentUser() == null) {
                // User is not authenticated, set empty state without error
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    prescriptions = emptyList(),
                    filteredPrescriptions = emptyList(),
                    error = null
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = prescriptionRepository.getSavedPrescriptions()) {
                is Resource.Success -> {
                    val prescriptions = result.data ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        prescriptions = prescriptions,
                        filteredPrescriptions = prescriptions
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

    fun searchPrescriptions(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        val filtered = if (query.isEmpty()) {
            _uiState.value.prescriptions
        } else {
            _uiState.value.prescriptions.filter { prescription ->
                val queryLower = query.lowercase()

                // Search in title
                prescription.title.lowercase().contains(queryLower) ||

                        // Search in doctor name
                        prescription.summary.doctorName.lowercase().contains(queryLower) ||

                        // Search in medication names
                        prescription.summary.medications.any { medication ->
                            medication.name.lowercase().contains(queryLower)
                        } ||

                        // Search in summary text
                        prescription.summary.summary.lowercase().contains(queryLower) ||

                        // Search in dosage instructions
                        prescription.summary.dosageInstructions.any { instruction ->
                            instruction.lowercase().contains(queryLower)
                        } ||

                        // Search in warnings
                        prescription.summary.warnings.any { warning ->
                            warning.lowercase().contains(queryLower)
                        }
            }
        }

        _uiState.value = _uiState.value.copy(filteredPrescriptions = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
