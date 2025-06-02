package com.aritradas.medai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aritradas.medai.BuildConfig
import com.aritradas.medai.domain.model.GeminiPrescriptionResponse
import com.aritradas.medai.domain.model.Medication
import com.aritradas.medai.domain.model.PrescriptionSummary
import com.aritradas.medai.domain.repository.PrescriptionRepository
import com.aritradas.medai.utils.Resource
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
class PrescriptionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PrescriptionRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val gson = Gson()

    override suspend fun summarizePrescription(imageUri: Uri): Resource<PrescriptionSummary> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = uriToBitmap(imageUri)
                val prompt = """
                    Analyze this prescription image and extract the following information. 
                    Please respond ONLY with valid JSON in exactly this format (no additional text or markdown):
                    
                    {
                        "medications": [
                            {
                                "name": "medication name",
                                "dosage": "dosage amount (e.g., 500mg, 1 tablet)",
                                "frequency": "how often to take (e.g., twice daily, every 8 hours)",
                                "duration": "how long to take (e.g., 7 days, 2 weeks)"
                            }
                        ],
                        "dosageInstructions": ["Take with food", "Take before meals", "Do not crush"],
                        "summary": "Brief summary of the prescription including patient info if visible",
                        "warnings": ["Important warnings or contraindications if any"]
                    }
                    
                    If you cannot clearly read certain information, use "Not clearly visible" for that field.
                    Also make sure the medicines listed exists with the names. Make sure to validate all.
                    Ensure all JSON keys are present even if the arrays are empty.
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text?.trim() ?: throw Exception("No response from Gemini")

                // Parse the JSON response
                val summary = parseGeminiResponse(responseText)
                Resource.Success(summary)

            } catch (e: Exception) {
                Resource.Error("Failed to analyze prescription: ${e.message}")
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            throw Exception("Failed to load image: ${e.message}")
        }
    }

    private fun parseGeminiResponse(responseText: String): PrescriptionSummary {
        return try {
            // Clean the response text (remove any markdown formatting)
            val cleanedResponse = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            // Parse JSON response
            val geminiResponse = gson.fromJson(cleanedResponse, GeminiPrescriptionResponse::class.java)

            // Convert to domain model
            PrescriptionSummary(
                medications = geminiResponse.medications.map { medication ->
                    Medication(
                        name = medication.name,
                        dosage = medication.dosage,
                        frequency = medication.frequency,
                        duration = medication.duration
                    )
                },
                dosageInstructions = geminiResponse.dosageInstructions,
                summary = geminiResponse.summary,
                warnings = geminiResponse.warnings
            )

        } catch (e: JsonSyntaxException) {
            // Fallback parsing if JSON is malformed
            parseFallbackResponse(responseText)
        } catch (e: Exception) {
            // Return error state
            PrescriptionSummary(
                medications = emptyList(),
                dosageInstructions = listOf("Could not parse prescription details"),
                summary = "Failed to analyze prescription image. Raw response: ${responseText.take(100)}...",
                warnings = listOf("Please consult with a healthcare professional for accurate information")
            )
        }
    }

    private fun parseFallbackResponse(responseText: String): PrescriptionSummary {
        // Fallback parsing for when JSON parsing fails
        return PrescriptionSummary(
            medications = extractMedicationsFromText(responseText),
            dosageInstructions = extractInstructionsFromText(responseText),
            summary = responseText.take(300) + if (responseText.length > 300) "..." else "",
            warnings = listOf("AI-generated summary - Please verify with healthcare professional")
        )
    }

    private fun extractMedicationsFromText(text: String): List<Medication> {
        // Simple text parsing for medications
        val medications = mutableListOf<Medication>()
        val lines = text.split("\n")

        lines.forEach { line ->
            // Look for medication patterns
            if (line.contains("mg", ignoreCase = true) ||
                line.contains("tablet", ignoreCase = true) ||
                line.contains("capsule", ignoreCase = true)) {

                medications.add(
                    Medication(
                        name = line.take(50),
                        dosage = "As prescribed",
                        frequency = "As prescribed",
                        duration = "As prescribed"
                    )
                )
            }
        }

        return medications.ifEmpty {
            listOf(
                Medication(
                    name = "Could not extract medication names",
                    dosage = "Please refer to original prescription",
                    frequency = "Please refer to original prescription",
                    duration = "Please refer to original prescription"
                )
            )
        }
    }

    private fun extractInstructionsFromText(text: String): List<String> {
        val instructions = mutableListOf<String>()

        // Look for common instruction keywords
        val instructionKeywords = listOf("take", "with", "before", "after", "daily", "times")
        val lines = text.split("\n")

        lines.forEach { line ->
            if (instructionKeywords.any { keyword ->
                    line.contains(keyword, ignoreCase = true)
                }) {
                instructions.add(line.trim())
            }
        }

        return instructions.ifEmpty {
            listOf("Follow the instructions on the prescription")
        }
    }
}
