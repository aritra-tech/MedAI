package com.aritradas.medai.domain.repository

import com.aritradas.medai.domain.model.DrugResult

interface MedicineDetailsRepository {
    suspend fun getDrugInfo(genericName: String): DrugResult?
}