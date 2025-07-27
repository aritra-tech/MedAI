package com.aritradas.medai.domain.model

data class OpenFdaResponse(
    val results: List<DrugResult>? = null
)

data class DrugResult(
    val medicineName: String,
    val uses: String,
    val howItWorks: String,
    val benefits: String,
    val sideEffects: String
)

// Gemini response model
data class GeminiMedicineResponse(
    val uses: String,
    val howItWorks: String,
    val benefits: String,
    val sideEffects: String
)

data class OpenFdaInfo(
    val brand_name: List<String>? = null,
    val generic_name: List<String>? = null,
    val manufacturer_name: List<String>? = null
)

