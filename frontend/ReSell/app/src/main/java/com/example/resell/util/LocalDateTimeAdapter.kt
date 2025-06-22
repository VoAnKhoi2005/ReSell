package com.example.resell.util

import com.squareup.moshi.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class LocalDateTimeAdapter {
    @FromJson
    fun fromJson(json: String): LocalDateTime {
        return try {
            Instant.parse(json).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (e: DateTimeParseException) {
            LocalDateTime.parse(json, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @ToJson
    fun toJson(value: LocalDateTime): String {
        return value.atZone(ZoneId.systemDefault()).toInstant().toString()
    }
}
