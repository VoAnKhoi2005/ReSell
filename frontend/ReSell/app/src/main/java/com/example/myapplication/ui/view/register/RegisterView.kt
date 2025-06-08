package com.example.myapplication.ui.view.register
import Roboto
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.view.login.LoginTextField
import com.example.myapplication.ui.theme.LoginButton
import com.example.myapplication.ui.theme.LoginTitle
import com.example.myapplication.ui.theme.Red
import com.example.myapplication.ui.theme.SoftBlue

@Composable
fun RegisterScreen(){
    Surface {
        Column(modifier = Modifier.fillMaxSize() ) {
            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)
            ) {
                RegisterForm()
                LoginSection()


            }

        }

    }
}

@Composable
private fun LoginSection() {
    Box(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.8f)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter

    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF828282),
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("Đã có tài khoản?")
                }
                withStyle(
                    style = SpanStyle(
                        color = LoginButton,
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append(" ")
                    append("Đăng nhập")
                }
            }
        )

    }
}

@Composable
private fun RegisterForm() {
    LoginTextField(
        lable = "Email",
        trailing = "",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        lable = "Tên đăng nhập",
        trailing = "",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        lable = "Số điện thoại",
        trailing = "",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        lable = "Password",
        trailing = "",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(20.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)


    ) {
        Text(
            text = "Đăng ký",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun TopSection() {
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f),
            painter = painterResource(id = R.drawable.subtract),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Row(
            modifier = Modifier.padding(top = 80.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(
                    id = R.string.app_logo
                )

            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.the_resell),
                    style = MaterialTheme.typography.headlineMedium,
                    color = LoginButton
                )
                Text(
                    text = stringResource(id = R.string.the_slogan),
                    style = MaterialTheme.typography.titleMedium,
                    color = LoginButton
                )
            }

        }
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = stringResource(id = R.string.register),
            color = LoginTitle,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter)
        )


    }
}


