package com.aritradas.medai.data.repository

import com.aritradas.medai.domain.model.DrugResult
import com.aritradas.medai.domain.repository.MedicineDetailsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineDetailsRepositoryImpl @Inject constructor() : MedicineDetailsRepository {
    override suspend fun getDrugInfo(genericName: String): DrugResult? {
        return null
    }
}