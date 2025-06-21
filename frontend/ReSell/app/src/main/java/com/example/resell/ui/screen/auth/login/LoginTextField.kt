package com.example.resell.ui.screen.auth.login

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@Composable
fun LoginTextField(
    value: String,
    modifier: Modifier = Modifier,
    lable: String,
    onTextChange: (String) -> Unit
){
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onTextChange,
        label = {
            Text(
                text = lable,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        )

    )
}
@Composable
fun NumberPhoneTextField(
    numberPhone: String,
    modifier: Modifier = Modifier,
    onNumberPhoneChange: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = numberPhone,
        onValueChange = onNumberPhoneChange,
        label = {
            Text(
                text = "Số điện thoại",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        )

    )
}
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    lable :String = "Mật khẩu"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(
                text = lable,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
        },
        modifier = modifier,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        ),
        trailingIcon = {
            val label = if (passwordVisible) "Ẩn" else "Hiện"
            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                Text(label, color = Color.Black)
            }
        },
        singleLine = true
    )
}