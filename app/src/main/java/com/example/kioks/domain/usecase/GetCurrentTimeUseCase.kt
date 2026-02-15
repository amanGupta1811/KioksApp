package com.example.kioks.domain.usecase

import com.example.kioks.domain.repository.TimeRepository

class GetCurrentTimeUseCase(
    private val repository: TimeRepository
) {

    operator fun invoke(): String {
        return repository.getCurrentTime()
    }
}

