package com.example.kioks.presentation.main

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kioks.domain.usecase.GetCurrentTimeUseCase
import com.example.kioks.domain.usecase.SaveScreenshotUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    private val saveScreenshotUseCase: SaveScreenshotUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MainUiState>(MainUiState.Loading)

    val uiState: StateFlow<MainUiState> = _uiState

    private var clockJob: Job? = null

    fun startClock() {
        clockJob = viewModelScope.launch {
            while (isActive) {
                val time = getCurrentTimeUseCase()
                _uiState.value = MainUiState.TimeUpdated(time)
                delay(1000)
            }
        }
    }

    fun stopClock() {
        clockJob?.cancel()
    }

    fun refreshApp() {
        val time = getCurrentTimeUseCase()
        _uiState.value = MainUiState.RefreshSuccess(time)
    }

    fun getCurrentTime(): String {
        return getCurrentTimeUseCase()
    }

    fun saveScreenshot(bitmap: Bitmap) {
        viewModelScope.launch {
            val success = saveScreenshotUseCase(bitmap)

            if (success) {
                val time = getCurrentTimeUseCase()
                _uiState.value = MainUiState.ScreenshotSaved(bitmap, time)
            }
        }
    }



}

