package com.aritradas.medai.ui.presentation.prescription.state

import com.aritradas.medai.domain.model.SavedPrescription

data class PrescriptionListUiState(
    val isLoading: Boolean = false,
    val prescriptions: List<SavedPrescription> = emptyList(),
    val error: String? = null
)
