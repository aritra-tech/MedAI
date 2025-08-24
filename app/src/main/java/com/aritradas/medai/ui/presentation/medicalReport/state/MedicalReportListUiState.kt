package com.aritradas.medai.ui.presentation.medicalReport.state

import com.aritradas.medai.domain.model.SavedMedicalReport

data class MedicalReportListUiState(
    val isLoading: Boolean = false,
    val reports: List<SavedMedicalReport> = emptyList(),
    val filteredReports: List<SavedMedicalReport> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)
