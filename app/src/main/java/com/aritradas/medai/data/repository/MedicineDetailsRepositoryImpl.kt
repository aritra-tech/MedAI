package com.aritradas.medai.data.repository

import com.aritradas.medai.domain.model.DrugResult
import com.aritradas.medai.domain.repository.MedicineDetailsRepository
import com.aritradas.medai.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MedicineDetailsRepositoryImpl @Inject constructor() : MedicineDetailsRepository {
    override suspend fun getDrugInfo(genericName: String): DrugResult? = withContext(Dispatchers.IO) {
        val searchParam = "openfda.generic_name:$genericName"
        val response = RetrofitClient.openFdaService.getDrugDetails(searchParam)
        if (response.isSuccessful) {
            response.body()?.results?.firstOrNull()
        } else null
    }
}