package com.aritradas.medai.domain.model

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String
)

data class PrescriptionSummary(
    val medications: List<Medication>,
    val dosageInstructions: List<String>,
    val summary: String,
    val warnings: List<String>
)