package com.aritradas.medai.domain.repository

import android.net.Uri
import com.aritradas.medai.domain.model.MedicalReportSummary
import com.aritradas.medai.domain.model.SavedMedicalReport
import com.aritradas.medai.utils.Resource

interface MedicalReportRepository {
    suspend fun validateReport(imageUri: Uri): Resource<Boolean>
    suspend fun summarizeMedicalReport(imageUri: Uri): Resource<MedicalReportSummary>
    suspend fun saveMedicalReport(report: SavedMedicalReport): Resource<String>
    suspend fun getSavedMedicalReports(): Resource<List<SavedMedicalReport>>
    suspend fun getMedicalReportById(id: String): Resource<SavedMedicalReport>
    suspend fun deleteMedicalReportById(id: String): Resource<Boolean>
}
