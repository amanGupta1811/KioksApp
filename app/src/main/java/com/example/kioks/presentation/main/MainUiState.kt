package com.example.kioks.presentation.main

import android.graphics.Bitmap

sealed class MainUiState {

    object Loading : MainUiState()

    data class TimeUpdated(val time: String) : MainUiState()

    data class RefreshSuccess(val time: String) : MainUiState()

    data class ScreenshotTaken(val bitmap: Bitmap) : MainUiState()

    data class RestartSuccess(val time: String) : MainUiState()

    data class ScreenshotSaved(
        val bitmap: Bitmap,
        val time: String
    ) : MainUiState()

}






