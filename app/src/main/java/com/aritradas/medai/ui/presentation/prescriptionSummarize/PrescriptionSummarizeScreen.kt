package com.aritradas.medai.ui.presentation.prescriptionSummarize

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionSummarizeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Create temporary file for camera
    val createImageFile = {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(null)
        File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Handle successful photo capture
            }
        }
    )
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
        }
    )
    
    // Handle camera action
    val handleTakePhoto = {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        imageUri = photoUri
        cameraLauncher.launch(photoUri)
        showDialog = false
    }
    
    // Handle gallery action
    val handleAddImage = {
        galleryLauncher.launch("image/*")
        showDialog = false
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column {
                    TextButton(
                        onClick = handleTakePhoto,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Take Photo"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Take Photo")
                        }
                    }

                    TextButton(
                        onClick = handleAddImage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "Add Image"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Add Image")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Scan Prescription",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Dashed border container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val dashLength = 10.dp.toPx()
                    val gapLength = 8.dp.toPx()
                    
                    drawRoundRect(
                        color = Color.Gray,
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(dashLength, gapLength)
                            )
                        ),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Upload a prescription",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Take a photo of your prescription or upload an existing image.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(
                            text = "Upload",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
