package com.dimensioncam.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.Marking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Utility class for exporting images with markings
 */
class ImageExporter(private val context: Context) {
    
    /**
     * Export a photo with markings to the device gallery
     * 
     * @param photoUri URI of the original photo
     * @param markings List of markings to render
     * @param arrowStyle Global arrow style
     * @param fileName Output file name
     * @return Uri of the exported image, or null if failed
     */
    suspend fun exportMarkedImage(
        photoUri: Uri,
        markings: List<Marking>,
        arrowStyle: ArrowStyle,
        fileName: String
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // Load original image
            val originalBitmap = loadBitmap(photoUri) ?: return@withContext null
            
            // Create mutable bitmap for drawing
            val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)
            
            // Get density (use 1.0 for export to match original resolution)
            val density = 1.0f
            
            // Render all markings
            markings.sortedBy { it.displayOrder }.forEach { marking ->
                MarkingRenderer.drawMarking(
                    canvas = canvas,
                    marking = marking,
                    imageWidth = resultBitmap.width,
                    imageHeight = resultBitmap.height,
                    arrowStyle = arrowStyle,
                    density = density
                )
            }
            
            // Save to gallery
            val savedUri = saveToGallery(resultBitmap, fileName)
            
            // Clean up
            resultBitmap.recycle()
            originalBitmap.recycle()
            
            savedUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Load bitmap from URI
     */
    private fun loadBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Save bitmap to device gallery
     */
    private fun saveToGallery(bitmap: Bitmap, fileName: String): Uri? {
        val outputStream: OutputStream?
        val uri: Uri?
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above - use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            
            outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            // Android 9 and below - use file system
            @Suppress("DEPRECATION")
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(picturesDir, fileName)
            
            outputStream = FileOutputStream(imageFile)
            uri = Uri.fromFile(imageFile)
        }
        
        return try {
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Generate a marked thumbnail for preview
     * 
     * @param photoUri URI of the original photo
     * @param markings List of markings
     * @param arrowStyle Global arrow style
     * @param maxSize Maximum dimension (width or height) in pixels
     * @return Bitmap thumbnail, or null if failed
     */
    suspend fun generateThumbnail(
        photoUri: Uri,
        markings: List<Marking>,
        arrowStyle: ArrowStyle,
        maxSize: Int = 400
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // Load and resize original image
            val originalBitmap = loadBitmap(photoUri) ?: return@withContext null
            val scaledBitmap = scaleBitmap(originalBitmap, maxSize)
            originalBitmap.recycle()
            
            // Create mutable copy for drawing
            val resultBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true)
            scaledBitmap.recycle()
            
            val canvas = Canvas(resultBitmap)
            val density = 1.0f
            
            // Render markings
            markings.sortedBy { it.displayOrder }.forEach { marking ->
                MarkingRenderer.drawMarking(
                    canvas = canvas,
                    marking = marking,
                    imageWidth = resultBitmap.width,
                    imageHeight = resultBitmap.height,
                    arrowStyle = arrowStyle,
                    density = density
                )
            }
            
            resultBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Scale bitmap to fit within maxSize while maintaining aspect ratio
     */
    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        val scale = (maxSize.toFloat() / maxOf(width, height)).coerceAtMost(1.0f)
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
