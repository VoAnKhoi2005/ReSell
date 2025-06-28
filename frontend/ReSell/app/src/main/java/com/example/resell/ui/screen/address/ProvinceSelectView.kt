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
fun ProvinceSelectScreen(
    onProvinceSelected: (String) -> Unit
) {
    val provinces = listOf("TP.HCM", "Hà Nội", "Đà Nẵng", "Tây Ninh", "Cần Thơ")
    var search by remember { mutableStateOf("") }

    val filtered = provinces.filter {
        it.contains(search, ignoreCase = true)
    }

    Column {
        TopBar(
            titleText = "Chọn Tỉnh/Thành phố",
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
            items(filtered) { province ->
                Text(
                    text = province,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {//filterdiachi
                            NavigationController.sharedRegionSelection =
                                NavigationController.sharedRegionSelection.copy(province = province)


                            NavigationController.navController.popBackStack()
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
