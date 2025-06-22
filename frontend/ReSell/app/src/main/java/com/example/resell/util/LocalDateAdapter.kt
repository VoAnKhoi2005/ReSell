package com.example.resell.util

import com.squareup.moshi.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @FromJson
    fun fromJson(json: String): LocalDate = LocalDate.parse(json, formatter)

    @ToJson
    fun toJson(value: LocalDate): String = value.format(formatter)
}