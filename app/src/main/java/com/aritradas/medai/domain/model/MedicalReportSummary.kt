package com.aritradas.medai.domain.model

import java.util.Date

data class MedicalReportSummary(
    val doctorName: String = "",
    val medications: List<Medication> = emptyList(),
    val dosageInstructions: List<String> = emptyList(),
    val summary: String = "",
    val warnings: List<String> = emptyList(),
    val reportReason: String = "",
    val stepsToCure: List<String> = emptyList()
)

data class SavedMedicalReport(
    val id: String = "",
    val summary: MedicalReportSummary,
    val savedAt: Date = Date(),
    val title: String = "",
    val report: String = "",
)
