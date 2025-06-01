package com.aritradas.medai.ui.presentation.prescriptionSummarize.state

import com.aritradas.medai.domain.model.PrescriptionSummary

data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val summary: PrescriptionSummary? = null,
    val error: String? = null
)
