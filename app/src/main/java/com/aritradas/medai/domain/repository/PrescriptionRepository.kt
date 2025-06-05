package com.aritradas.medai.domain.repository

import android.net.Uri
import com.aritradas.medai.domain.model.PrescriptionSummary
import com.aritradas.medai.domain.model.SavedPrescription
import com.aritradas.medai.utils.Resource

interface PrescriptionRepository {
    suspend fun validatePrescription(imageUri: Uri): Resource<Boolean>
    suspend fun summarizePrescription(imageUri: Uri): Resource<PrescriptionSummary>
    suspend fun savePrescription(prescription: SavedPrescription): Resource<String>
}
