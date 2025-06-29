package com.example.resell.ui.viewmodel.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

data class UserProfileUiState(
    val isCurrentUser: Boolean = true,
    val userId: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val rating: String = "",
    val reviewCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val responseRate: String = "",
    val createdAt: String = "",
    val address: String = "",
)

class ProfileDetailViewModel : ViewModel() {

    // ✅ Dữ liệu của chính mình (giả lập)
    private val myProfile = UserProfileUiState(
        isCurrentUser = true,
        userId = "me123",
        name = "Phạm Thành Long",
        avatarUrl = "https://i.pinimg.com/736x/89/81/05/8981056bb076ff4e1bbad051317f8d03.jpg",
        coverUrl = "https://i.pinimg.com/736x/4c/eb/95/4ceb9579b86e93e82aca383cb4e8c996.jpg",
        rating = "4.8",
        reviewCount = 200,
        followerCount = 500,
        followingCount = 100,
        responseRate = "90%",
        createdAt = "6 tháng",
        address = "TP Hồ Chí Minh"
    )

    // ✅ Dữ liệu người dùng khác (giả lập)
    private val otherProfile = UserProfileUiState(
        isCurrentUser = false,
        userId = "user456",
        name = "Nguyễn Văn A",
        avatarUrl = "https://i.pinimg.com/736x/08/54/a2/0854a29393744806e28717620949cbc7.jpg",
        coverUrl = "https://i.pinimg.com/736x/e5/ac/49/e5ac497d3a2d8d45469d7e3e01fe983b.jpg",
        rating = "3.2",
        reviewCount = 37,
        followerCount = 23,
        followingCount = 12,
        responseRate = "Chưa có thông tin",
        createdAt = "1 tháng",
        address = "Huyện Hòa Thành, Tây Ninh"
    )

    // State
    private val _uiState = mutableStateOf(UserProfileUiState())
    val uiState: State<UserProfileUiState> = _uiState

    fun loadProfile(targetUserId: String, currentUserId: String) {
        val isCurrent = targetUserId == currentUserId
        _uiState.value = if (isCurrent) myProfile else otherProfile
    }

    fun toggleFollow() {
        // Follow/Unfollow logic
    }
}
