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
                    You are a medical report assistant. Analyze the attached medical report image and produce a precise, patient‑friendly summary.
                    
                    Return ONLY valid JSON (no preface, no markdown fences) with these keys:
                    {
                      "doctorName": "Doctor/facility name if present, else 'Unknown Doctor'",
                      "reportReason": "Primary reason, exam/test name or focus (e.g., FibroScan, CBC, Chest X‑ray)",
                      "summary": "Patient‑friendly narrative that mirrors the example style below",
                      "warnings": ["Important warnings/red flags/notes if present"]
                    }
                    
                    Build the "summary" value as a clear narrative suitable for patients. If the report is a quantitative test (e.g., FibroScan, labs, imaging), follow this structure when information is available:
                    - Patient: <patient name>
                    - Exam Date: <date>
                    - 1) Main metric(s): For each key parameter, include:
                      • Parameter name
                      • The patient's result with unit (e.g., Median E: 3.8 kPa, CAP: 154 dB/m)
                      • A one‑line interpretation in lay terms (e.g., "well within the normal range; no evidence of significant fibrosis")
                    - Technical quality (if present): (e.g., IQR/Med %, number of valid measurements) and what it means for reliability
                    - Overall Conclusion: a concise bottom‑line statement in plain English
                    - Add a final disclaimer: "This is a summary of the provided report. Discuss results with your doctor, who will interpret them in the context of your overall health."
                    
                    Requirements:
                    - Keep explanations non‑alarmist and in plain English.
                    - Include units and normal/abnormal interpretation when inferable.
                    - If specific fields (name/date/parameters) are missing, omit those lines and still provide a sensible conclusion.
                    - Do NOT invent values; only interpret what is visible. If unsure, say so briefly.
                    - The JSON must be strictly valid. The narrative goes inside the single string field "summary" and may include headings and bullets.
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

                        val summary = MedicalReportSummary(
                            doctorName = summaryMap["doctorName"] as? String ?: "Unknown Doctor",
                            summary = summaryMap["summary"] as? String ?: "",
                            warnings = (summaryMap["warnings"] as? List<String>) ?: emptyList(),
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

                val summary = MedicalReportSummary(
                    doctorName = summaryMap["doctorName"] as? String ?: "Unknown Doctor",
                    summary = summaryMap["summary"] as? String ?: "",
                    warnings = (summaryMap["warnings"] as? List<String>) ?: emptyList()
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
                summary = parsed.summary,
                warnings = parsed.warnings
            )
        } catch (e: JsonSyntaxException) {
            parseFallbackResponse(responseText)
        } catch (e: Exception) {
            MedicalReportSummary(
                doctorName = "Unknown Doctor",
                summary = "Failed to analyze report image. Raw response: ${responseText.take(100)}...",
                warnings = listOf("Please consult with a healthcare professional for accurate information")
            )
        }
    }

    private fun parseFallbackResponse(text: String): MedicalReportSummary {
        return MedicalReportSummary(
            doctorName = "Unknown Doctor",
            summary = text.take(300) + if (text.length > 300) "..." else "",
            warnings = listOf("AI-generated summary - Please verify with healthcare professional")
        )
    }
}
