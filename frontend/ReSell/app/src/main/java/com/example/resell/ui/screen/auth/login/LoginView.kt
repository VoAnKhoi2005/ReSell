package com.example.resell.ui.screen.auth.login


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.R
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.LoginTitle
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.auth.login.LoginViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.provider.Settings
import android.widget.Toast


@Composable
fun LoginScreen(
    viewModel : LoginViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user by viewModel.user.collectAsState()
  Surface(
  ) {
        Column(modifier = Modifier.fillMaxSize().navigationBarsPadding().verticalScroll(rememberScrollState()) ) {
            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)
            ) {
                LoginForm(viewModel,coroutineScope)
                Spacer(modifier = Modifier.height(40.dp))


            }

        }

    }
}



@Composable
private fun LoginForm(viewModel: LoginViewModel,
                      coroutineScope: CoroutineScope) {
    val context = LocalContext.current
    val activity = context as Activity
    var password by remember { mutableStateOf("123456789") }
    var username by remember { mutableStateOf("user4") }
    val signInClient = remember { Identity.getSignInClient(context) }
    val error by viewModel.loginError.collectAsState()

    val TYPE_NO_CREDENTIAL = "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL"

    fun startGoogleLogin() {
        coroutineScope.launch {
            try {
                val googleIDOption = GetGoogleIdOption.Builder()
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIDOption)
                    .build()

                val credentialManager = CredentialManager.create(activity)
                val result = credentialManager.getCredential(activity, request)

                viewModel.launchGoogleSignIn(result)
            } catch (e: GetCredentialException) {
                if (e.type == TYPE_NO_CREDENTIAL) {
                    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    }
                    activity.startActivity(intent)
                } else {
                    Toast.makeText(context, "Lỗi đăng nhập: ${e.type}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi không xác định: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    if (!error.isNullOrBlank()) {
        Text(
            text = error!!,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    LoginTextField(
        onTextChange = {
            //TODO:
            username = it
        },
        value = username,
        lable ="Tên người dùng",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))
    PasswordTextField(
        password = password,
        onPasswordChange = {
            password = it
                           },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))
    Text(
        text = "Quên mật khẩu?",
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO: Xử lý khi click vào "Quên mật khẩu?"
                //Chưa có view
            },
        color = Color.Blue
    )
    Spacer(modifier = Modifier.height(20.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = {
            //TODO nút đăng nhập bằng username
            Log.d("Login", "✅ Đăng nhập: ${username} ${password}")
            viewModel.launchUsernameSignIn(username,password)
        },

        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)


    ) {
        Text(
            text = "Đăng nhập",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    GoogleSignInButton( onClick =  {startGoogleLogin()}, modifier =  Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(12.dp))
    SignupText(
        onSignupClick = {
            // TODO: Navigate sang màn hình đăng ký
            NavigationController.navController.navigate(Screen.PhoneRegister.route)
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
                )

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
            text = stringResource(id = R.string.login),
            color = LoginTitle,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter)
        )


    }
}
@Composable
fun GoogleSignInButton(
    text: String = "Đăng nhập với Google",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hoặc",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(bottom = 16.dp).fillMaxWidth(),
        textAlign = TextAlign.Center,

    )
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google), // 👈 icon Google
                contentDescription = "Google Sign-In",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = text,
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
@Composable
fun SignupText(
    onSignupClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        append("Chưa có tài khoản? ")

        // Gắn tag cho phần "Đăng ký"
        pushStringAnnotation(
            tag = "SIGNUP",
            annotation = "signup"
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue, // màu xanh giống link
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Đăng ký")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGNUP", start = offset, end = offset)
                .firstOrNull()?.let {
                    onSignupClick()
                }
        },
        modifier = Modifier.padding(top = 16.dp)
    )
}


