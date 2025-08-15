package com.aritradas.medai.ui.presentation.medicalReportDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.MedicalReportRepository
import com.aritradas.medai.ui.presentation.medicalReportDetails.state.MedicalReportDetailsUiState
import com.aritradas.medai.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicalReportDetailsViewModel @Inject constructor(
    private val repository: MedicalReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalReportDetailsUiState())
    val uiState: StateFlow<MedicalReportDetailsUiState> = _uiState.asStateFlow()

    fun loadReport(reportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.getMedicalReportById(reportId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        report = result.data,
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    // already loading
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.deleteMedicalReportById(reportId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isDeleted = false
                    )
                }

                is Resource.Loading -> {
                    // already loading
                }
            }
        }
    }
}
