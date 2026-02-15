package com.example.kioks.dataRepostry

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.example.kioks.domainRepostry.ScreenshotRepository

class ScreenshotRepositoryImpl(
    private val context: Context
) : ScreenshotRepository {

    override suspend fun save(bitmap: Bitmap): Boolean {
        return try {
            val filename = "screenshot_${System.currentTimeMillis()}.png"

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/KioskApp"
                )
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return false

            context.contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}


