package com.example.resell.ui.screen.auth.register

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.R
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.auth.login.LoginTextField
import com.example.resell.ui.screen.auth.login.PasswordTextField
import com.example.resell.ui.theme.LoginTitle
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.auth.register.PhoneRegisterViewModel

@Composable
fun PhoneRegisterScreen(){
    Surface {
        Column(modifier = Modifier.fillMaxSize() ) {
            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)
            ) {
                PhoneRegisterForm()


            }

        }

    }
}

@Composable
private fun PhoneRegisterForm(viewModel: PhoneRegisterViewModel = hiltViewModel()) {
    val phone by viewModel.phoneNumber.collectAsState()
    val error by viewModel.phoneError.collectAsState()
    LoginTextField(
        value = phone,
        onTextChange = { viewModel.onPhoneChanged(it) },
        lable = "Số điện thoại",
        modifier = Modifier.fillMaxWidth()
    )
    if (!error.isNullOrBlank()) {
        Text(
            text = error!!,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = {viewModel.onAuthClicked()},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)


    ) {
        Text(
            text = "Xác minh",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
        )
    }
    Spacer(modifier = Modifier.height(30.dp))
    SigninText(
        onSigninClick = {
            // TODO: Navigate sang màn hình đăng nhập
            NavigationController.navController.navigate(Screen.Login.route)
        }
    )
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
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(SoftBlue)

        )
        Row(
            modifier = Modifier.padding(top = 80.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                colorFilter = ColorFilter.tint(White2),
                contentDescription = stringResource(
                    id = R.string.app_logo
                ),


                )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.the_resell),
                    style = MaterialTheme.typography.headlineMedium,
                    color = White2
                )
                Text(
                    text = stringResource(id = R.string.the_slogan),
                    style = MaterialTheme.typography.titleMedium,
                    color = White2
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