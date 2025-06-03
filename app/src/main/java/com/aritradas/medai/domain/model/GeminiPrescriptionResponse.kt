package com.aritradas.medai.domain.model

import com.google.gson.annotations.SerializedName

data class GeminiMedicationResponse(
    @SerializedName("name") val name: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("duration") val duration: String
)

data class GeminiPrescriptionResponse(
    @SerializedName("medications") val medications: List<GeminiMedicationResponse> = emptyList(),
    @SerializedName("dosageInstructions") val dosageInstructions: List<String> = emptyList(),
    @SerializedName("summary") val summary: String = "",
    @SerializedName("warnings") val warnings: List<String> = emptyList()
)
