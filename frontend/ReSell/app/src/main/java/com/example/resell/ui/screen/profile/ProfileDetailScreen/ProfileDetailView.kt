package com.example.resell.ui.screen.profile.ProfileDetailScreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.resell.R
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.ProfileHeaderSection
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.postmanagement.ApproveScreen
import com.example.resell.ui.screen.postmanagement.NotApprovedScreen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.viewmodel.profile.ProfileDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileDetailScreen(
    targetUserId: String,
    viewModel: ProfileDetailViewModel = hiltViewModel()
) {
    Log.d("PROFILE_DETAIL", "Rendering ProfileDetailScreen with userId = $targetUserId")

    val state by viewModel.uiState
    val currentUserId = ReactiveStore<User>().item.value?.id ?: ""

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(context, it) }
    }
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadCover(context, it) }
    }

    LaunchedEffect(targetUserId, currentUserId) {
        viewModel.loadProfile(targetUserId, currentUserId)
    }

    val pagerState = rememberPagerState(pageCount = { ProfileDetailTab.entries.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedTabIndex = remember { mutableStateOf(0) }

    // Đồng bộ pager với tab index
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex.value = pagerState.currentPage
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            titleText = state.name.ifBlank { "Thông tin người dùng" },
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        ProfileHeaderSection(
            state = state,
            onEditClick = if (state.isCurrentUser) {
                { NavigationController.navController.navigate(Screen.AccountSetting.route) }
            } else null,
            onChangeAvatarClick = if (state.isCurrentUser) {
                { launcher.launch("image/*") }
            } else null,
            onChangeCoverClick = if (state.isCurrentUser) {
                { coverLauncher.launch("image/*") }
            } else null,
            onFollowClick = {
                viewModel.toggleFollow()
            }
        )

        ProfileTabsPager(
            pagerState = pagerState,
            selectedTabIndex = selectedTabIndex.value,
            onTabSelected = { index ->
                selectedTabIndex.value = index
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            isCurrentUser = state.isCurrentUser
        )
    }
}




enum class ProfileDetailTab(
    val text: String
){
    Approved(
        text ="ĐANG HIỂN THỊ (0)"
    ),
    Sold(
        text ="ĐÃ BÁN (0)"
    )

}
@Composable
fun ProfileTabsPager(
    selectedTabIndex: Int,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit,
    isCurrentUser: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    // 👇 Label tab động theo người dùng
    val tabs = listOf(
        if (isCurrentUser) "ĐANG HIỂN THỊ (0)" else "SẢN PHẨM (0)",
        "ĐÃ BÁN (0)"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTabIndex == index,
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    onClick = {
                        onTabSelected(index)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ApproveScreen(isCurrentUser = isCurrentUser)
                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NotApprovedScreen(isCurrentUser = isCurrentUser)
                    }
                }
            }
        }

    }
}

