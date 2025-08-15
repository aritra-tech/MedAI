package com.aritradas.medai.ui.presentation.medicalReportDetails.state

import com.aritradas.medai.domain.model.SavedMedicalReport

data class MedicalReportDetailsUiState(
    val report: SavedMedicalReport? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean? = null
)
