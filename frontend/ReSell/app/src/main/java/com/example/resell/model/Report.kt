package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class ReportUser(
    @Json(name = "id")
    val id: String,

    @Json(name = "reporter_id")
    val reporterId: String,

    @Json(name = "reported_id")
    val reportedId: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "created_at")
    val createdAt: OffsetDateTime,

    @Json(name = "reporter")
    val reporter: User? = null,

    @Json(name = "reported")
    val reported: User? = null
)

@JsonClass(generateAdapter = true)
data class ReportPost(
    @Json(name = "id")
    val id: String,

    @Json(name = "reporter_id")
    val reporterId: String,

    @Json(name = "reported_id")
    val reportedId: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "created_at")
    val createdAt: OffsetDateTime,

    @Json(name = "reporter")
    val reporter: User? = null,

    @Json(name = "reported")
    val reported: Post? = null
)
