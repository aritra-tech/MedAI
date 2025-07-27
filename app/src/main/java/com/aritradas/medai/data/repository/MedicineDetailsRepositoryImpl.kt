package com.aritradas.medai.data.repository

import android.content.Context
import com.aritradas.medai.BuildConfig
import com.aritradas.medai.domain.model.DrugResult
import com.aritradas.medai.domain.model.GeminiMedicineResponse
import com.aritradas.medai.domain.repository.MedicineDetailsRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineDetailsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MedicineDetailsRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val gson = Gson()

    override suspend fun getDrugInfo(genericName: String): DrugResult? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Provide detailed information about the medicine: "$genericName"
                
                Please respond ONLY with valid JSON in exactly this format (no additional text or markdown):
                
                {
                    "uses": "Detailed description of what this medicine is used for, including the medical conditions it treats. Be specific about the primary indications and therapeutic uses.",
                    "howItWorks": "Clear explanation of how this medicine works in the body (mechanism of action). Explain in simple terms how it achieves its therapeutic effect.",
                    "benefits": "Key therapeutic benefits and positive effects of this medicine. Include what patients can expect when taking this medication.",
                    "sideEffects": "Common and important side effects that patients should be aware of. Include both mild and serious side effects, and mention when to contact a doctor."
                }
                
                Important guidelines:
                - Provide accurate, medically sound information
                - Focus on medicines commonly available in India and internationally
                - Include both brand names and generic equivalents if relevant
                - Keep explanations clear and understandable for patients
                - If this is a combination medicine, explain each component briefly
                - If the medicine name seems incorrect or unclear, provide the best possible information based on similar medicines
                - Be comprehensive but concise in each section
                - Use professional medical language but keep it patient-friendly
                
                Medicine to analyze: $genericName
            """.trimIndent()

            val inputContent = content {
                text(prompt)
            }

            val response = generativeModel.generateContent(inputContent)
            val responseText = response.text?.trim() ?: return@withContext null

            parseGeminiMedicineResponse(responseText, genericName)

        } catch (e: Exception) {
            // Return null if request fails
            null
        }
    }

    private fun parseGeminiMedicineResponse(responseText: String, medicineName: String): DrugResult? {
        return try {
            val cleanedResponse = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val geminiResponse = gson.fromJson(cleanedResponse, GeminiMedicineResponse::class.java)

            DrugResult(
                medicineName = medicineName,
                uses = geminiResponse.uses,
                howItWorks = geminiResponse.howItWorks,
                benefits = geminiResponse.benefits,
                sideEffects = geminiResponse.sideEffects
            )

        } catch (e: JsonSyntaxException) {
            // Fallback parsing if JSON is malformed
            parseFallbackMedicineResponse(responseText, medicineName)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseFallbackMedicineResponse(responseText: String, medicineName: String): DrugResult? {
        return try {
            // Simple fallback parsing - extract information from plain text
            val sections = responseText.split("\n").filter { it.isNotBlank() }

            var uses = "Information not available"
            var howItWorks = "Information not available"
            var benefits = "Information not available"
            var sideEffects = "Information not available"

            // Try to extract information from plain text
            sections.forEach { line ->
                val lowerLine = line.lowercase()
                when {
                    lowerLine.contains("use") && lowerLine.contains(":") -> {
                        uses = line.substringAfter(":").trim()
                    }
                    lowerLine.contains("work") && lowerLine.contains(":") -> {
                        howItWorks = line.substringAfter(":").trim()
                    }
                    lowerLine.contains("benefit") && lowerLine.contains(":") -> {
                        benefits = line.substringAfter(":").trim()
                    }
                    lowerLine.contains("side effect") && lowerLine.contains(":") -> {
                        sideEffects = line.substringAfter(":").trim()
                    }
                }
            }

            DrugResult(
                medicineName = medicineName,
                uses = uses,
                howItWorks = howItWorks,
                benefits = benefits,
                sideEffects = sideEffects
            )
        } catch (e: Exception) {
            null
        }
    }
}