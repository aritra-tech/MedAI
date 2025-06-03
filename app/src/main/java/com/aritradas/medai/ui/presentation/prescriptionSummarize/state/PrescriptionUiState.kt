package com.aritradas.medai.ui.presentation.prescriptionSummarize.state

import com.aritradas.medai.domain.model.PrescriptionSummary

data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val isValidating: Boolean = false,
    val isValidPrescription: Boolean? = null,
    val validationError: String? = null,
    val summary: PrescriptionSummary? = null,
    val error: String? = null
)
