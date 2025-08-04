package com.aritradas.medai.domain.model

data class OpenFdaResponse(
    val results: List<DrugResult>? = null
)

data class GeminiMedicineResponse(
    val uses: List<String>,
    val howItWorks: List<String>,
    val benefits: List<String>,
    val sideEffects: List<String>
)

data class DrugResult(
    val medicineName: String,
    val uses: List<String>,
    val howItWorks: List<String>,
    val benefits: List<String>,
    val sideEffects: List<String>
)


data class OpenFdaInfo(
    val brand_name: List<String>? = null,
    val generic_name: List<String>? = null,
    val manufacturer_name: List<String>? = null
)

