package com.aritradas.medai.domain.model

data class OpenFdaResponse(
    val results: List<DrugResult>? = null
)

data class DrugResult(
    val active_ingredient: List<String>? = null,
    val purpose: List<String>? = null,
    val indications_and_usage: List<String>? = null,
    val warnings: List<String>? = null,
    val dosage_and_administration: List<String>? = null,
    val openfda: OpenFdaInfo? = null
)

data class OpenFdaInfo(
    val brand_name: List<String>? = null,
    val generic_name: List<String>? = null,
    val manufacturer_name: List<String>? = null
)

