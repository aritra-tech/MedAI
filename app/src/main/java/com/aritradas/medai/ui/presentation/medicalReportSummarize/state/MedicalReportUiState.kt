package com.aritradas.medai.ui.presentation.medicalReportSummarize.state

import com.aritradas.medai.domain.model.MedicalReportSummary

data class MedicalReportUiState(
    val isLoading: Boolean = false,
    val isValidating: Boolean = false,
    val isValidReport: Boolean? = null,
    val validationError: String? = null,
    val summary: MedicalReportSummary? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,
    val report: String = ""
)
