package com.example.resell.ui.screen.address

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import androidx.compose.foundation.lazy.items


@Composable
fun WardSelectScreen(
    selectedDistrict: String,
    onWardSelected: (String) -> Unit
) {
    val wards = when (selectedDistrict) {
        "Quận 1" -> listOf("Phường Bến Nghé", "Phường Bến Thành", "Phường Cầu Kho")
        "Quận 7" -> listOf("Phường Tân Phong", "Phường Tân Quy", "Phường Tân Kiểng")
        else -> listOf("Phường A", "Phường B")
    }

    var search by remember { mutableStateOf("") }

    val filtered = wards.filter {
        it.contains(search, ignoreCase = true)
    }

    Column {
        TopBar(
            titleText = "Chọn Phường/Xã",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Tìm kiếm") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        LazyColumn {
            items(filtered) { ward ->
                Text(
                    text = ward,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            NavigationController.sharedRegionSelection =
                                NavigationController.sharedRegionSelection.copy(ward=ward)


                            NavigationController.navController.popBackStack()
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
