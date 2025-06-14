package util

import com.squareup.moshi.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @FromJson
    fun fromJson(json: String): LocalDateTime {
        return LocalDateTime.parse(json, formatter)
    }

    @ToJson
    fun toJson(value: LocalDateTime): String {
        return value.format(formatter)
    }
}
