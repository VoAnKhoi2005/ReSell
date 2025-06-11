package com.example.myapplication.ui.model

data class Province(
    val id: String,
    val name: String,
    val districts: List<District>
)
