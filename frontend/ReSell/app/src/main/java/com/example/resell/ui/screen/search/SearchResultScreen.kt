import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.White
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import com.example.resell.ui.components.CategoryFilterBottomSheet
import com.example.resell.ui.components.PriceFilterBottomSheet
import com.example.resell.ui.components.ProductPostItem
import com.example.resell.ui.components.formatPrice
import com.example.resell.ui.screen.home.ProductPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(searchQuery: String = "") {
    var localSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        localSearchQuery = searchQuery
    }

    var showSheet by remember { mutableStateOf(false) }
    var region by remember { mutableStateOf(RegionSelection()) }

    //quản lý sate cho danh mục sheet
    var showCategorySheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }


    //quản lý state cho giá sheet
    var showPriceSheet by remember { mutableStateOf(false) }
    var selectedPriceRange by remember { mutableStateOf<Pair<Float, Float>?>(null) }


    //test filter
    val allPosts = remember {
        listOf(
            ProductPost("1", "Xe đạp thể thao", "1 giờ trước", "https://via.placeholder.com/150", 1200000, "Xe cộ", "TP.HCM"),
            ProductPost("2", "iPhone 12", "2 giờ trước", "https://via.placeholder.com/150", 10000000, "Đồ điện tử", "Hà Nội"),
            ProductPost("3", "Tủ lạnh Toshiba", "Hôm qua", "https://via.placeholder.com/150", 3000000, "Tủ lạnh, máy lạnh, máy giặt", "Đà Nẵng"),
            ProductPost("4", "Chó Poodle 2 tháng", "3 ngày trước", "https://via.placeholder.com/150", 2500000, "Thú cưng", "TP.HCM"),
            ProductPost("5", "Máy lạnh LG 2HP", "Hôm nay", "https://via.placeholder.com/150", 5000000, "Tủ lạnh, máy lạnh, máy giặt", "TP.HCM")
        )
    }
    val filteredPosts = remember(localSearchQuery, selectedCategory, selectedPriceRange, region) {
        allPosts.filter { post ->
            val matchKeyword = localSearchQuery.isBlank() || post.title.contains(localSearchQuery, ignoreCase = true)
            val matchCategory = selectedCategory == null || post.category == selectedCategory
            val matchPrice = selectedPriceRange == null ||
                    (post.price in selectedPriceRange!!.first.toInt()..selectedPriceRange!!.second.toInt())
            val matchRegion = region.province == null || post.address.contains(region.province!!, ignoreCase = true)
            matchKeyword && matchCategory && matchPrice && matchRegion
        }
    }

    //update region mỗi khi quay lại màn hình chọn filter địa chỉ
    /*LaunchedEffect(NavigationController.sharedRegionSelection) {
        region = NavigationController.sharedRegionSelection
    }*/





    Scaffold(
        topBar = {
            TopBar(
                showSearch = true,
                showBackButton = true,
                showEmailIcon = true,
                showNotificationIcon = true,
                onBackClick = { NavigationController.navController.popBackStack() },
                onSearchNavigate = {
                    NavigationController.navController.navigate(Screen.Search.route)
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
            // Khu vực
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSheet = true }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pin_duotone),
                    contentDescription = "Bộ lọc khu vực",
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
            // Bộ lọc: Danh mục & Giá
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Bộ lọc theo giá và danh mục",
                    tint = DarkBlue,
                    modifier = Modifier.size(20.dp)
                )

                FilterButton(label = selectedCategory ?: "Danh mục") {
                    showCategorySheet = true
                }


                val priceLabel = selectedPriceRange?.let { (min, max) ->
                    "Giá: ${formatPrice(min)} - ${formatPrice(max)}"
                } ?: "Giá"

                FilterButton(label = priceLabel) {
                    showPriceSheet = true
                }


            }


            // Nội dung kết quả
            Spacer(modifier = Modifier.height(16.dp))
            if (localSearchQuery.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Kết quả cho từ khóa: \"$localSearchQuery\"",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Xóa",
                        color = Color.Red,
                        modifier = Modifier
                            .clickable { localSearchQuery = "" }
                            .padding(4.dp)
                    )
                }
            }



            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có kết quả tìm kiếm",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredPosts) { post ->
                        ProductPostItem(
                            title = post.title,
                            time = post.time,
                            imageUrl = post.imageUrl,
                            price = post.price,
                            category = post.category,
                            address = post.address,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            NavigationController.navController.navigate(Screen.ProductDetail.route + "/${post.id}")
                        }
                    }
                }
            }


        }

        // Bottom Sheet lọc khu vực
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
        if (showCategorySheet) {
            CategoryFilterBottomSheet(
                onDismissRequest = { showCategorySheet = false },
                onCategorySelected = { category ->
                    selectedCategory = category

                }

            )
        }
        if (showPriceSheet) {
            PriceFilterBottomSheet(
                initialMin = selectedPriceRange?.first ?: 0f,
                initialMax = selectedPriceRange?.second ?: 100_000_000f,
                onDismissRequest = { showPriceSheet = false },
                onApply = { min, max ->
                    selectedPriceRange = min to max
                    showPriceSheet = false
                }
            )
        }


    }
}

//  Dữ liệu chọn khu vực
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

@Composable//lọc khu vực
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
@Composable//nút lọc danh mục và giá
fun FilterButton(
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = LightGray,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Mở $label",
            tint = Color.Gray
        )
    }
}



