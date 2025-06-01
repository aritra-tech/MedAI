package com.aritradas.medai.domain.repository

import android.net.Uri
import com.aritradas.medai.domain.model.PrescriptionSummary
import com.aritradas.medai.utils.Resource

interface PrescriptionRepository {
    suspend fun summarizePrescription(imageUri: Uri): Resource<PrescriptionSummary>
}