import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resell.R
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen() {
    var showSheet by remember { mutableStateOf(false) }
    var region by remember { mutableStateOf(RegionSelection()) }

    Scaffold(
        topBar = {
            TopBar(
                showSearch = true,
                showBackButton = true,
                showEmailIcon = true,
                showNotificationIcon = true,
                onBackClick = { NavigationController.navController.popBackStack() },
                onSearchNavigate = {
                    // Điều hướng sang trang nhập từ khóa nếu cần
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 🔽 Khu vực
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSheet = true }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Bộ lọc",
                    tint = DarkBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Khu vực: ", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = region.province ?: "Toàn quốc",
                    style = MaterialTheme.typography.labelMedium.copy(color = DarkBlue)
                )
            }

            // 📄 Nội dung kết quả
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hiển thị kết quả tìm kiếm...",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 📦 Bottom Sheet lọc khu vực
        if (showSheet) {
            RegionFilterBottomSheet(
                region = region,
                onDismissRequest = { showSheet = false },
                onProvinceClick = {
                    showSheet = false
                    NavigationController.navController.navigate(Screen.ProvinceSelect.route)
                },
                onDistrictClick = {
                    showSheet = false
                    NavigationController.navController.navigate(Screen.DistrictSelect.route)
                },
                onWardClick = {
                    showSheet = false
                    NavigationController.navController.navigate(Screen.WardSelect.route)
                }
            )
        }
    }
}

// ✅ Dữ liệu chọn khu vực
data class RegionSelection(
    val province: String? = null,
    val district: String? = null,
    val ward: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionFilterBottomSheet(
    region: RegionSelection,
    onDismissRequest: () -> Unit,
    onProvinceClick: () -> Unit,
    onDistrictClick: () -> Unit,
    onWardClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = White,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lọc theo khu vực",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FilterRow("Chọn Tỉnh/Thành phố", region.province ?: "Chưa chọn", onProvinceClick)
            FilterRow("Chọn Quận/Huyện", region.district ?: "Chưa chọn", onDistrictClick)
            FilterRow("Chọn Phường/Xã", region.ward ?: "Chưa chọn", onWardClick)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            painter = painterResource(id = R.drawable.arrowforward),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
