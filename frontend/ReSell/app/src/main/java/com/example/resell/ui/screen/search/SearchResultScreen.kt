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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.ui.components.CategoryFilterBottomSheet
import com.example.resell.ui.components.PriceFilterBottomSheet
import com.example.resell.ui.components.ProductPostItem
import com.example.resell.ui.components.formatPrice

import com.example.resell.ui.viewmodel.search.SearchResultUiState
import com.example.resell.ui.viewmodel.search.SearchResultViewModel
import com.example.resell.util.getRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(searchQuery: String = "") {
    val viewModel: SearchResultViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    var showRegionSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showPriceSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInitial(searchQuery)
    }
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItem ->
                if (lastVisibleItem == state.filteredPosts.lastIndex) {
                    viewModel.loadMore()
                }
            }
    }
    Scaffold(
        topBar = {
            TopBar(
                showSearch = true,
                searchQuery = state.searchQuery,
                onClearSearch = {
                    viewModel.clearSearchQuery()
                    NavigationController.navController.popBackStack() // 👈 quay lại màn Search
                },
                showBackButton = true,
                showEmailIcon = true,
                showNotificationIcon = true,
                onBackClick = { NavigationController.navController.popBackStack() }
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
                    .clickable { showRegionSheet = true }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pin_duotone),
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Khu vực: ", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = state.region.name ?: "Toàn quốc",
                    style = MaterialTheme.typography.labelMedium.copy(color = DarkBlue)
                )
            }

            // Bộ lọc
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(20.dp)
                )

                FilterButton(label = state.selectedCategory ?: "Danh mục") {
                    showCategorySheet = true
                }

                val priceLabel = state.selectedPriceRange?.let { (min, max) ->
                    "Giá: ${formatPrice(min.toFloat())} - ${formatPrice(max.toFloat())}"
                } ?: "Giá"


                FilterButton(label = priceLabel) {
                    showPriceSheet = true
                }
            }



            if (state.filteredPosts.isEmpty()) {
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
                LazyColumn(modifier = Modifier.fillMaxSize(),state = listState) {
                    items(state.filteredPosts) { post ->
                        ProductPostItem(
                            title = post.title,
                            time = getRelativeTime(post.createdAt),
                            imageUrl = post.thumbnail,
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

        // Bottom Sheets
        if (showRegionSheet) {
            RegionFilterBottomSheet(
                state = state,
                onDismiss = { showRegionSheet = false },
                onApply = { viewModel.applyLocationSelection() },
                onSelectProvince = { viewModel.setProvince(it) },
                onSelectDistrict = { viewModel.setDistrict(it) },
                onSelectWard = { viewModel.setWard(it) }
            )
        }


        if (showCategorySheet) {
            CategoryFilterBottomSheet(
                onDismissRequest = { showCategorySheet = false },
                onCategorySelected = { category ->
                    viewModel.setSelectedCategory(category)
                    showCategorySheet = false
                }
            )
        }

        if (showPriceSheet) {
            PriceFilterBottomSheet(
                initialMin = state.selectedPriceRange?.first ?: 0,
                initialMax = state.selectedPriceRange?.second ?: 100_000_000,
                onDismissRequest = { showPriceSheet = false },
                onApply = { min, max ->
                    viewModel.setSelectedPriceRange(min to max)
                    showPriceSheet = false
                }
            )
        }
    }
}



// UI (Compose)
// UI (Compose)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionFilterBottomSheet(
    state: SearchResultUiState,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onSelectProvince: (String?) -> Unit,
    onSelectDistrict: (String?) -> Unit,
    onSelectWard: (String?) -> Unit
) {
    ModalBottomSheet(onDismissRequest = {
        onDismiss()
        onApply()
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Chọn khu vực", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (state.provinces.isNotEmpty()) {
                DropdownSelector(
                    label = "Tỉnh/Thành phố",
                    options = listOf("Toàn quốc") + state.provinces.map { it.name },
                    selectedOption = state.region.province ?: "Toàn quốc",
                    onSelect = { selected ->
                        onSelectProvince(if (selected == "Toàn quốc") null else selected)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.region.province != null) {
                DropdownSelector(
                    label = "Quận/Huyện",
                    options = listOf("Tất cả quận/huyện") + state.districts.map { it.name },
                    selectedOption = state.region.district ?: "Tất cả quận/huyện",
                    onSelect = { selected ->
                        onSelectDistrict(if (selected == "Tất cả quận/huyện") null else selected)
                    }
                )
            } else {
                Text("Vui lòng chọn Tỉnh/Thành phố trước", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.region.district != null) {
                DropdownSelector(
                    label = "Phường/Xã",
                    options = listOf("Tất cả phường/xã") + state.wards.map { it.name },
                    selectedOption = state.region.ward ?: "Tất cả phường/xã",
                    onSelect = { selected ->
                        onSelectWard(if (selected == "Tất cả phường/xã") null else selected)
                        onDismiss()
                        onApply()
                    }
                )
            } else {
                Text("Vui lòng chọn Quận/Huyện trước", color = Color.Gray)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText = selectedOption ?: ""



    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedText,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedText = selectionOption
                        expanded = false
                        onSelect(selectionOption)
                    }
                )
            }
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



