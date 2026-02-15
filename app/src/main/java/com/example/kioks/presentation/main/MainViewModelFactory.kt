package com.example.kioks.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kioks.domain.usecase.GetCurrentTimeUseCase
import com.example.kioks.domain.usecase.SaveScreenshotUseCase

class MainViewModelFactory(
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    private val saveScreenshotUseCase: SaveScreenshotUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(getCurrentTimeUseCase, saveScreenshotUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


