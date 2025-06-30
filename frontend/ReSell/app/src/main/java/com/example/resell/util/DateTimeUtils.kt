package com.example.resell.util

import android.util.Log
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

fun getRelativeTime(time: LocalDateTime?): String {
    if (time == null) return "Không rõ"

    val now = LocalDateTime.now(ZoneOffset.UTC)
    val duration = Duration.between(time, now)


    return when {
        duration.toMinutes() < 1 -> "Vừa xong"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
        duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
        duration.toDays() == 1L -> "Hôm qua"
        duration.toDays() < 7 -> "${duration.toDays()} ngày trước"
        duration.toDays() < 30 -> "${duration.toDays() / 7} tuần trước"
        duration.toDays() < 365 -> "${duration.toDays() / 30} tháng trước"
        else -> "${duration.toDays() / 365} năm trước"
    }
}