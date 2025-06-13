package com.example.myapplication.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightGray = Color(0xFFD1D5DB)
val SoftBlue = Color(0xFFBFDBFE)
val DarkBlue = Color(0xFF0253C6)
val IconColor = Color(0xFF1C1C1E)
val GrayFont = Color(0xFF8A8A8A)
val LoginButton = Color(0xFF000113)
val MainButton = Color(0xFF94A3B8)
val Red = Color(0xFFFF453A)
val LoginTitle = Color(0xFF1E293B)
val priceColor = Color(0xFFF44336)
val BuyerMessage = Color(0xFFEFEFEF)

val ColorScheme.focusedTextFieldText
@Composable
get() = Color.Black

val ColorScheme.unfocusedTextFieldText
    @Composable
    get() = Color(0xFF475569)

val ColorScheme.textFieldContainer
    @Composable
    get() = Color(0xFFF1F5F9)
