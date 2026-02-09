package com.example.wastemanagement.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

object ImageQualityAnalyzer {

    // This is a simple placeholder implementation.
    // A real implementation would involve more complex image processing.
    suspend fun isImageBlurry(context: Context, imageUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                inputStream?.use { 
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true // Don't load the bitmap into memory
                    }
                    BitmapFactory.decodeStream(it, null, options)

                    // A very basic check: if the image resolution is very low, treat it as blurry.
                    val width = options.outWidth
                    val height = options.outHeight
                    return@withContext width < 500 || height < 500
                }
            } catch (e: Exception) {
                // If we can't read the image, assume it's not blurry
                e.printStackTrace()
            }
            return@withContext false
        }
    }
}
