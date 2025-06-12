package com.aritradas.medai.ui.presentation.prescriptionDetails.state

import com.aritradas.medai.domain.model.SavedPrescription

data class PrescriptionDetailsUiState(
    val prescription: SavedPrescription? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)