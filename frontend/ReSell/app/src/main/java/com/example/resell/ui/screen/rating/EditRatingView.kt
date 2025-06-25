package com.example.resell.ui.screen.rating
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.White2
import com.example.resell.ui.theme.Yellow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import com.example.resell.ui.theme.White

@Composable
fun ReviewProductScreen() {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Đánh giá sản phẩm",
                showBackButton = true,
                onBackClick = { NavigationController.navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔽 Ảnh sản phẩm
            ProductPostItemHorizontalImage(
                title = "Plushie",
                time =  "1 giờ trước",
                imageUrl ="https://i.pinimg.com/736x/15/35/c7/1535c759a5e617be2794d128e070a0bf.jpg",
                price = 199000,
                address = "Quận 1, TP.HCM",
                showExtraInfo = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 🔽 Chất lượng sản phẩm
            Text(
                text = "Chất lượng sản phẩm",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    val starRes = when {
                        rating >= index + 1 -> R.drawable.fullstar
                        rating >= index + 0.5 -> R.drawable.halfstar
                        else -> R.drawable.emptystar
                    }

                    Image(
                        painter = painterResource(id = starRes),
                        contentDescription = "Star ${index + 1}",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = index + 1 }
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Nhận xét của bạn") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 🔽 Nút gửi
            Button(
                onClick = {
                    // TODO: Gửi đánh giá (rating, comment)
                    NavigationController.navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gửi đánh giá", fontSize = 16.sp, color = White2)
            }
        }
    }
}
