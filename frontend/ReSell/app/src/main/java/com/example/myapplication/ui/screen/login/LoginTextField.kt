package com.example.myapplication.ui.screen.login

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.ui.theme.MainButton
import com.example.myapplication.ui.theme.focusedTextFieldText
import com.example.myapplication.ui.theme.textFieldContainer
import com.example.myapplication.ui.theme.unfocusedTextFieldText

@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    lable: String,
    trailing: String
){
    TextField(
        modifier = modifier,
        value = "",
        onValueChange = {},
        label ={
            Text(text=lable,
                style=MaterialTheme.typography.labelMedium,
                color = Color.Black)
        },
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.unfocusedTextFieldText,
            focusedTextColor = MaterialTheme.colorScheme.focusedTextFieldText,
            unfocusedContainerColor = MaterialTheme.colorScheme.textFieldContainer,
            focusedContainerColor = MaterialTheme.colorScheme.textFieldContainer

        ),
        trailingIcon = {
            TextButton(onClick = {}) {
                Text(text = trailing, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.Black)

            }
        }

    )



}