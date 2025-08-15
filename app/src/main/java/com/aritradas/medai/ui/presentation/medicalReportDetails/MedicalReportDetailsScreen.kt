package com.aritradas.medai.ui.presentation.medicalReportDetails

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aritradas.medai.domain.model.Medication
import com.aritradas.medai.ui.presentation.medicalReportSummarize.MedicalReportSummarizeViewModel
import com.aritradas.medai.ui.presentation.prescriptionSummarize.DrugDetailSheetContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MedicalReportDetailsScreen(
    navController: NavController,
    reportId: String,
    modifier: Modifier = Modifier,
    viewModel: MedicalReportDetailsViewModel = hiltViewModel(),
    reportViewModel: MedicalReportSummarizeViewModel = hiltViewModel()
) {
    val context = navController.context
    val uiState by viewModel.uiState.collectAsState()
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportTypeDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTriggered by remember { mutableStateOf(false) }
    val handleReport = { showReportTypeDialog = true }
    var showDrugDetailModal by remember { mutableStateOf(false) }
    val onShowDrugDetailModal: (Boolean) -> Unit = { showDrugDetailModal = it }
    val drugDetail by reportViewModel.drugDetail.collectAsState()
    val isDrugLoading by reportViewModel.isDrugLoading.collectAsState()
    val drugDetailError by reportViewModel.drugDetailError.collectAsState()

    val handleReportSubmit = {
        if (reportReason.isNotBlank()) {
            showReportDialog = false
            showReportTypeDialog = false
            Toast.makeText(context, "Report has been submitted", Toast.LENGTH_SHORT).show()
            reportReason = ""
        }
    }

    LaunchedEffect(deleteTriggered) {
        if (deleteTriggered) {
            viewModel.deleteReport(reportId)
        }
    }
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted == true) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(reportId) {
        viewModel.loadReport(reportId)
    }

    if (showDrugDetailModal) {
        ModalBottomSheet(
            onDismissRequest = {
                showDrugDetailModal = false
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when {
                    isDrugLoading -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    drugDetail != null -> {
                        DrugDetailSheetContent(detail = drugDetail!!)
                    }

                    drugDetailError != null -> {
                        Text(
                            text = drugDetailError ?: "No data.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Report Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Report"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }

                uiState.report != null -> {
                    val report = uiState.report!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            HeaderCard(
                                title = report.title,
                                doctorName = report.summary.doctorName
                            )
                        }

                        item {
                            SummaryCard(summary = report.summary.summary)
                        }

                        if (report.summary.warnings.isNotEmpty()) {
                            item {
                                WarningsCard(warnings = report.summary.warnings)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚠️ AI-generated content - verify with doctor",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { showReportTypeDialog = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = "Report content",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    if (showReportTypeDialog) {
        Dialog(
            onDismissRequest = { showReportTypeDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Select Report Type",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                reportReason = "Medical Inaccuracy"
                                showReportDialog = true
                                showReportTypeDialog = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = {
                                reportReason = "Medical Inaccuracy"
                                showReportDialog = true
                                showReportTypeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Medical Inaccuracy")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                reportReason = "Misinformation"
                                showReportDialog = true
                                showReportTypeDialog = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = {
                                reportReason = "Misinformation"
                                showReportDialog = true
                                showReportTypeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Misinformation")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                reportReason = ""
                                showReportDialog = true
                                showReportTypeDialog = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = {
                                reportReason = ""
                                showReportDialog = true
                                showReportTypeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Other")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showReportTypeDialog = false }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Content") },
            text = {
                Column {
                    Text("Is this content problematic or incorrect? Please explain below.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Report Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = handleReportSubmit) {
                    Text("Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report") },
            text = { Text("Are you sure you want to delete this report? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deleteTriggered = true
                }) {
                    Text("Okay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HeaderCard(
    title: String,
    doctorName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Doctor",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = doctorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    summary: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Summarize,
                    contentDescription = "Summary",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

@Composable
private fun MedicationsCard(
    medications: List<Medication>,
    onMedicationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = "Medications",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Medications (${medications.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            medications.forEach { medication ->
                MedicationItem(
                    medication = medication,
                    onClick = { onMedicationClick(medication.name) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MedicationItem(
    medication: Medication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (medication.dosage.isNotEmpty()) {
                Text(
                    text = "Dosage: ${medication.dosage}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (medication.frequency.isNotEmpty()) {
                Text(
                    text = "Frequency: ${medication.frequency}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (medication.duration.isNotEmpty()) {
                Text(
                    text = "Duration: ${medication.duration}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun InstructionsCard(
    title: String,
    image: ImageVector? = null,
    instructions: List<String>
) {
    var showTooltip by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (image != null) {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { showTooltip = true },
                        imageVector = image,
                        contentDescription = "Info CTA"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            instructions.forEach { instruction ->
                Text(
                    text = "• $instruction",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
    if (showTooltip) {
        AlertDialog(
            onDismissRequest = { showTooltip = false },
            title = { Text("Note") },
            text = {
                Text("These steps are generated by AI and intended for informational purposes only. Please consult a qualified medical professional for accurate diagnosis and treatment.")
            },
            confirmButton = {
                TextButton(onClick = { showTooltip = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun WarningsCard(
    warnings: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warnings",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Warnings & Precautions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            warnings.forEach { warning ->
                Text(
                    text = "⚠️ $warning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
