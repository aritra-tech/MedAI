package com.aritradas.medai.domain.model

import java.util.Date

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String
)

data class PrescriptionSummary(
    val doctorName: String = "",
    val medications: List<Medication>,
    val dosageInstructions: List<String>,
    val summary: String,
    val warnings: List<String>,
    val report: String = ""
)

data class SavedPrescription(
    val id: String = "",
    val summary: PrescriptionSummary,
    val savedAt: Date = Date(),
    val title: String = "",
    val report: String = ""
)
