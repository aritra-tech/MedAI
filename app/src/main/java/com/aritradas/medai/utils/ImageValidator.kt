package com.aritradas.medai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object ImageValidator {
    
    fun validateImageBasics(context: Context, imageUri: Uri): ValidationResult {
        return try {
            val bitmap = uriToBitmap(context, imageUri)
            
            // Check image dimensions
            if (bitmap.width < 100 || bitmap.height < 100) {
                return ValidationResult.Invalid("Image is too small. Please use a higher resolution image.")
            }
            
            // Check if image is too large (might be a screenshot or processed image)
            if (bitmap.width > 4000 || bitmap.height > 4000) {
                return ValidationResult.Warning("Image is very large. Consider using a smaller, clearer image for better results.")
            }
            
            // Check aspect ratio (prescriptions are usually rectangular documents)
            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            if (aspectRatio < 0.3 || aspectRatio > 3.0) {
                return ValidationResult.Warning("Unusual image proportions detected. Ensure the entire prescription is visible.")
            }
            
            ValidationResult.Valid
            
        } catch (e: Exception) {
            ValidationResult.Invalid("Unable to process image: ${e.message}")
        }
    }
    
    private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
    data class Warning(val message: String) : ValidationResult()
}