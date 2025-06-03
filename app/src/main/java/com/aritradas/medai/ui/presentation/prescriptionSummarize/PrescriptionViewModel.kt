package com.aritradas.medai.ui.presentation.prescriptionSummarize

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.PrescriptionRepository
import com.aritradas.medai.ui.presentation.prescriptionSummarize.state.PrescriptionUiState
import com.aritradas.medai.utils.ImageValidator
import com.aritradas.medai.utils.Resource
import com.aritradas.medai.utils.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    fun validateAndAnalyzePrescription(imageUri: Uri) {
        viewModelScope.launch {
            // First do basic image validation
            when (val basicValidation = ImageValidator.validateImageBasics(context, imageUri)) {
                is ValidationResult.Invalid -> {
                    _uiState.value = _uiState.value.copy(
                        validationError = basicValidation.message
                    )
                    return@launch
                }
                is ValidationResult.Warning -> {
                    // Continue with AI validation but could show warning
                    // For now, we'll proceed
                }
                ValidationResult.Valid -> {
                    // Continue with AI validation
                }
            }
            
            // Then validate with AI
            _uiState.value = _uiState.value.copy(
                isValidating = true,
                error = null,
                validationError = null,
                isValidPrescription = null
            )

            when (val validationResult = prescriptionRepository.validatePrescription(imageUri)) {
                is Resource.Success -> {
                    if (validationResult.data == true) {
                        _uiState.value = _uiState.value.copy(
                            isValidating = false,
                            isValidPrescription = true
                        )
                        // If valid, proceed with analysis
                        analyzePrescription(imageUri)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isValidating = false,
                            isValidPrescription = false,
                            validationError = "This image does not appear to be a valid medical prescription. Please upload a clear image of a doctor's prescription."
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isValidating = false,
                        validationError = validationResult.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isValidating = true
                    )
                }
            }
        }
    }

    private fun analyzePrescription(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = prescriptionRepository.summarizePrescription(imageUri)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        summary = result.data
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearValidationError() {
        _uiState.value = _uiState.value.copy(
            validationError = null,
            isValidPrescription = null
        )
    }

    fun clearSummary() {
        _uiState.value = _uiState.value.copy(
            summary = null,
            isValidPrescription = null,
            validationError = null
        )
    }
}
