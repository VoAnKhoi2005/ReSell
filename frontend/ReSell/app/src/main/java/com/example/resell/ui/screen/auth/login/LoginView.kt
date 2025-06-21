package com.example.resell.ui.screen.auth.login


import android.app.Activity
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.resell.R
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.LoginTitle
import com.example.resell.ui.theme.SoftBlue
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.store.DataStore

@Composable
fun LoginScreen(){
  Surface {
        Column(modifier = Modifier.fillMaxSize() ) {
            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)
            ) {
                LoginForm()


            }

        }

    }
}



@Composable
private fun LoginForm() {
    val context = LocalContext.current
    val activity = context as Activity
    var password by remember { mutableStateOf("") }
    var numberPhone by remember { mutableStateOf("") }
    val signInClient = remember { Identity.getSignInClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val name = credential.displayName
                val email = credential.id
                val avatar = credential.profilePictureUri

                Log.d("GoogleSignIn", "Đăng nhập thành công: $email - $name - $avatar")
                //TODO: Đăng nhập

                //TODO: Lấy được User thì gán cho DataStore
               // DataStore.user = ...

                NavigationController.navController.navigate(Screen.Main.route)
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Lỗi khi lấy credential: ${e.message}")
                Toast.makeText(context, "Lỗi khi lấy thông tin", Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Log.d("GoogleSignIn", "Đăng nhập thất bại")
        }

    }

    fun startGoogleLogin() {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.clientID)) // ← đổi thành Web client ID từ Google Console
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        signInClient.beginSignIn(request)
            .addOnSuccessListener { result ->
                try {
                    launcher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("GoogleSignIn", "Couldn't start sign in UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Đăng nhập thất bại: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("Đăng nhập","Đăng nhập thất bại: ${e.localizedMessage}")
            }
    }
    NumberPhoneTextField(
        onNumberPhoneChange = {
            //TODO: Xử lí định dạng số điện thoại
            numberPhone = it
        },
        numberPhone = numberPhone,
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
            //TODO nút đăng nhập bằng số điện thoại

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
            NavigationController.navController.navigate(Screen.Register.route)
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


