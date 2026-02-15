package com.example.kioks.domainRepostry

import android.graphics.Bitmap

interface ScreenshotRepository {
    suspend fun save(bitmap: Bitmap): Boolean
}
