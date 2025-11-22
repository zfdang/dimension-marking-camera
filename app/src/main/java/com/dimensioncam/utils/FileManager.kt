package com.dimensioncam.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Utility class for file operations
 */
class FileManager(private val context: Context) {
    
    private val imagesDir = File(context.filesDir, "images").apply {
        if (!exists()) mkdirs()
    }
    
    private val thumbnailsDir = File(context.filesDir, "thumbnails").apply {
        if (!exists()) mkdirs()
    }
    
    /**
     * Copy a photo from URI to internal storage
     * 
     * @param uri Source URI
     * @return File path of the copied photo, or null if failed
     */
    suspend fun copyPhotoToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val destFile = File(imagesDir, fileName)
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Save thumbnail bitmap to internal storage
     * 
     * @param bitmap Thumbnail bitmap
     * @param photoId Associated photo ID
     * @return File path of the saved thumbnail
     */
    suspend fun saveThumbnail(bitmap: Bitmap, photoId: Long): String = withContext(Dispatchers.IO) {
        val fileName = "thumb_$photoId.jpg"
        val file = File(thumbnailsDir, fileName)
        
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
        }
        
        file.absolutePath
    }
    
    /**
     * Delete a file
     */
    suspend fun deleteFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get URI for a file using FileProvider
     */
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    /**
     * Create a temporary file for camera capture
     */
    fun createTempImageFile(): File {
        val fileName = "temp_capture_${System.currentTimeMillis()}.jpg"
        return File(context.cacheDir, fileName)
    }
    
    /**
     * Clean up orphaned files (files not referenced in database)
     * Note: This should be called with a list of valid file paths from database
     */
    suspend fun cleanupOrphanedFiles(validPaths: Set<String>) = withContext(Dispatchers.IO) {
        // Clean up images
        imagesDir.listFiles()?.forEach { file ->
            if (file.absolutePath !in validPaths) {
                file.delete()
            }
        }
        
        // Clean up thumbnails
        thumbnailsDir.listFiles()?.forEach { file ->
            if (file.absolutePath !in validPaths) {
                file.delete()
            }
        }
    }
}
