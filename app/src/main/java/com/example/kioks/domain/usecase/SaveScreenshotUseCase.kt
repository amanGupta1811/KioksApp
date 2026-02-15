package com.example.kioks.domain.usecase

import android.graphics.Bitmap
import com.example.kioks.domainRepostry.ScreenshotRepository

class SaveScreenshotUseCase(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Boolean {
        return repository.save(bitmap)
    }
}




