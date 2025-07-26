package com.aritradas.medai.network

import com.aritradas.medai.domain.model.OpenFdaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MedAIService {
    @GET("label.json")
    suspend fun getDrugDetails(
        @Query("search") search: String
    ): Response<OpenFdaResponse>
}

