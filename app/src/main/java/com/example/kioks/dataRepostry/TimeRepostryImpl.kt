package com.example.kioks.dataRepostry

import com.example.kioks.domain.repository.TimeRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeRepositoryImpl : TimeRepository {

    override fun getCurrentTime(): String {
        return SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }
}


