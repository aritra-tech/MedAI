package com.aritradas.medai.domain.repository

import android.net.Uri
import com.aritradas.medai.domain.model.PrescriptionSummary
import com.aritradas.medai.domain.model.SavedPrescription
import com.aritradas.medai.utils.Resource

interface PrescriptionRepository {
    suspend fun validatePrescription(imageUri: Uri): Resource<Boolean>
    suspend fun summarizePrescription(imageUri: Uri): Resource<PrescriptionSummary>
    suspend fun validatePrescription(imageUris: List<Uri>): Resource<Boolean>
    suspend fun summarizePrescription(imageUris: List<Uri>): Resource<PrescriptionSummary>
    suspend fun savePrescription(prescription: SavedPrescription): Resource<String>
    suspend fun getSavedPrescriptions(): Resource<List<SavedPrescription>>
    suspend fun getPrescriptionById(id: String): Resource<SavedPrescription>
    suspend fun deletePrescriptionById(id: String): Resource<Boolean>
}
