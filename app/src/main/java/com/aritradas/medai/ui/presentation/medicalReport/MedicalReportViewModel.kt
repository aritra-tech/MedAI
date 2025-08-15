package com.aritradas.medai.ui.presentation.medicalReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.medai.domain.repository.MedicalReportRepository
import com.aritradas.medai.ui.presentation.medicalReport.state.MedicalReportListUiState
import com.aritradas.medai.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicalReportViewModel @Inject constructor(
    private val reportRepository: MedicalReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalReportListUiState())
    val uiState: StateFlow<MedicalReportListUiState> = _uiState.asStateFlow()

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = reportRepository.getSavedMedicalReports()) {
                is Resource.Success -> {
                    val reports = result.data ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reports = reports,
                        filteredReports = reports
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        reports = emptyList(),
                        filteredReports = emptyList()
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun searchReports(query: String) {
        val lowered = query.lowercase()
        _uiState.value = _uiState.value.copy(searchQuery = query)

        val filtered = if (lowered.isBlank()) {
            _uiState.value.reports
        } else {
            _uiState.value.reports.filter { report ->
                report.title.lowercase().contains(lowered) ||
                        report.summary.summary.lowercase().contains(lowered) ||
                        report.summary.reportReason.lowercase().contains(lowered)
            }
        }
        _uiState.value = _uiState.value.copy(filteredReports = filtered)
    }
}
