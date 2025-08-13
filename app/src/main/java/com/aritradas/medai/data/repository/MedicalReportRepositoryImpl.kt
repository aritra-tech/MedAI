package com.aritradas.medai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aritradas.medai.BuildConfig
import com.aritradas.medai.domain.model.GeminiMedicalReportResponse
import com.aritradas.medai.domain.model.MedicalReportSummary
import com.aritradas.medai.domain.model.Medication
import com.aritradas.medai.domain.model.SavedMedicalReport
import com.aritradas.medai.domain.repository.MedicalReportRepository
import com.aritradas.medai.utils.Resource
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalReportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MedicalReportRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val gson = Gson()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun validateReport(imageUri: Uri): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = uriToBitmap(imageUri)
                val prompt = """
                    Analyze this image to determine if it contains a valid medical report, diagnostic test result, lab report, radiology report, or discharge summary.
                    Look for key indicators like: patient info, doctor or facility details, test names and results, impressions/diagnosis, dates, and structured report formatting.
                    Respond with ONLY "true" if this is clearly a medical report/result document, or "false" if it's not.
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text?.trim()?.lowercase() ?: "false"
                val isValid = responseText.contains("true")
                Resource.Success(isValid)
            } catch (e: Exception) {
                Resource.Error("Failed to validate report: ${e.message}")
            }
        }
    }

    override suspend fun summarizeMedicalReport(imageUri: Uri): Resource<MedicalReportSummary> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = uriToBitmap(imageUri)
                val prompt = """
                    Analyze this medical report image and extract the following information.
                    Respond ONLY with valid JSON in exactly this format (no additional text or markdown):
                    {
                        "doctorName": "Doctor or facility name if present, else 'Unknown Doctor'",
                        "medications": [
                            { "name": "", "dosage": "", "frequency": "", "duration": "" }
                        ],
                        "dosageInstructions": ["patient-friendly dosing instructions if present"],
                        "summary": "Plain-English summary of the report findings, diagnosis, and implications",
                        "warnings": ["important warnings or red flags to consider"],
                        "reportReason": "Primary reason/condition/test focus for this report (e.g., CBC, Chest X-ray, Diabetes follow-up)",
                        "stepsToCure": [
                          "Actionable next steps or care plan based on the report (follow-up, lifestyle, treatment notes). If unavailable, provide general guidance to consult doctor."
                        ]
                    }
                    Ensure all keys are present even if arrays are empty.
                    Avoid medical jargon; use layman's terms in the summary and steps.
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText =
                    response.text?.trim() ?: throw Exception("No response from Gemini")

                val summary = parseGeminiResponse(responseText)
                Resource.Success(summary)
            } catch (e: Exception) {
                Resource.Error("Failed to analyze report: ${e.message}")
            }
        }
    }

    override suspend fun saveMedicalReport(report: SavedMedicalReport): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                    ?: return@withContext Resource.Error("User not authenticated. Please log in to save reports.")

                val reportData = hashMapOf(
                    "summary" to report.summary,
                    "savedAt" to report.savedAt,
                    "title" to report.title,
                    "report" to report.report,
                    "reportReason" to report.summary.reportReason,
                    "stepsToCure" to report.summary.stepsToCure
                )

                val documentRef = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("reports")
                    .add(reportData)
                    .await()

                Resource.Success(documentRef.id)
            } catch (e: Exception) {
                Resource.Error("Failed to save report: ${e.message}")
            }
        }
    }

    override suspend fun getSavedMedicalReports(): Resource<List<SavedMedicalReport>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser =
                    auth.currentUser ?: return@withContext Resource.Error("User not authenticated")

                val querySnapshot = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("reports")
                    .orderBy("savedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val reports = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val data = document.data ?: return@mapNotNull null
                        val summaryMap =
                            data["summary"] as? Map<String, Any> ?: return@mapNotNull null

                        val medicationsData =
                            summaryMap["medications"] as? List<Map<String, Any>> ?: emptyList()
                        val medications = medicationsData.map { medMap ->
                            Medication(
                                name = medMap["name"] as? String ?: "",
                                dosage = medMap["dosage"] as? String ?: "",
                                frequency = medMap["frequency"] as? String ?: "",
                                duration = medMap["duration"] as? String ?: ""
                            )
                        }

                        val summary = MedicalReportSummary(
                            doctorName = summaryMap["doctorName"] as? String ?: "Unknown Doctor",
                            medications = medications,
                            dosageInstructions = (summaryMap["dosageInstructions"] as? List<String>)
                                ?: emptyList(),
                            summary = summaryMap["summary"] as? String ?: "",
                            warnings = (summaryMap["warnings"] as? List<String>) ?: emptyList(),
                            reportReason = summaryMap["reportReason"] as? String
                                ?: (data["reportReason"] as? String ?: ""),
                            stepsToCure = (summaryMap["stepsToCure"] as? List<String>)
                                ?: (data["stepsToCure"] as? List<String> ?: emptyList())
                        )

                        SavedMedicalReport(
                            id = document.id,
                            summary = summary,
                            savedAt = (data["savedAt"] as? com.google.firebase.Timestamp)?.toDate()
                                ?: java.util.Date(),
                            title = data["title"] as? String ?: "Untitled Report",
                            report = data["report"] as? String ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                Resource.Success(reports)
            } catch (e: Exception) {
                Resource.Error("Failed to fetch reports: ${e.message}")
            }
        }
    }

    override suspend fun getMedicalReportById(id: String): Resource<SavedMedicalReport> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser =
                    auth.currentUser ?: return@withContext Resource.Error("User not authenticated")

                val document = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("reports")
                    .document(id)
                    .get()
                    .await()

                if (!document.exists()) {
                    return@withContext Resource.Error("Report not found")
                }

                val data = document.data ?: return@withContext Resource.Error("Invalid report data")
                val summaryMap = data["summary"] as? Map<String, Any>
                    ?: return@withContext Resource.Error("Invalid summary data")

                val medicationsData =
                    summaryMap["medications"] as? List<Map<String, Any>> ?: emptyList()
                val medications = medicationsData.map { medMap ->
                    Medication(
                        name = medMap["name"] as? String ?: "",
                        dosage = medMap["dosage"] as? String ?: "",
                        frequency = medMap["frequency"] as? String ?: "",
                        duration = medMap["duration"] as? String ?: ""
                    )
                }

                val summary = MedicalReportSummary(
                    doctorName = summaryMap["doctorName"] as? String ?: "Unknown Doctor",
                    medications = medications,
                    dosageInstructions = (summaryMap["dosageInstructions"] as? List<String>)
                        ?: emptyList(),
                    summary = summaryMap["summary"] as? String ?: "",
                    warnings = (summaryMap["warnings"] as? List<String>) ?: emptyList(),
                    reportReason = summaryMap["reportReason"] as? String
                        ?: (data["reportReason"] as? String ?: ""),
                    stepsToCure = (summaryMap["stepsToCure"] as? List<String>)
                        ?: (data["stepsToCure"] as? List<String> ?: emptyList())
                )

                val report = SavedMedicalReport(
                    id = document.id,
                    summary = summary,
                    savedAt = (data["savedAt"] as? com.google.firebase.Timestamp)?.toDate()
                        ?: java.util.Date(),
                    title = data["title"] as? String ?: "Untitled Report",
                    report = data["report"] as? String ?: ""
                )

                Resource.Success(report)
            } catch (e: Exception) {
                Resource.Error("Failed to fetch report: ${e.message}")
            }
        }
    }

    override suspend fun deleteMedicalReportById(id: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser =
                    auth.currentUser ?: return@withContext Resource.Error("User not authenticated")

                firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("reports")
                    .document(id)
                    .delete()
                    .await()
                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error("Failed to delete report: ${e.message}")
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

    private fun parseGeminiResponse(responseText: String): MedicalReportSummary {
        return try {
            val cleaned = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsed = gson.fromJson(cleaned, GeminiMedicalReportResponse::class.java)

            MedicalReportSummary(
                doctorName = parsed.doctorName,
                medications = parsed.medications.map { med ->
                    Medication(
                        name = med.name,
                        dosage = med.dosage,
                        frequency = med.frequency,
                        duration = med.duration
                    )
                },
                dosageInstructions = parsed.dosageInstructions,
                summary = parsed.summary,
                warnings = parsed.warnings,
                reportReason = parsed.reportReason,
                stepsToCure = parsed.stepsToCure
            )
        } catch (e: JsonSyntaxException) {
            parseFallbackResponse(responseText)
        } catch (e: Exception) {
            MedicalReportSummary(
                doctorName = "Unknown Doctor",
                medications = emptyList(),
                dosageInstructions = listOf("Could not parse report details"),
                summary = "Failed to analyze report image. Raw response: ${responseText.take(100)}...",
                warnings = listOf("Please consult with a healthcare professional for accurate information"),
                reportReason = "Parse error",
                stepsToCure = listOf("Unable to extract next steps. Please follow up with your doctor.")
            )
        }
    }

    private fun parseFallbackResponse(text: String): MedicalReportSummary {
        return MedicalReportSummary(
            doctorName = "Unknown Doctor",
            medications = emptyList(),
            dosageInstructions = emptyList(),
            summary = text.take(300) + if (text.length > 300) "..." else "",
            warnings = listOf("AI-generated summary - Please verify with healthcare professional"),
            reportReason = "Could not extract reason",
            stepsToCure = listOf("General advice: Consult your doctor for interpretation and next steps.")
        )
    }
}
