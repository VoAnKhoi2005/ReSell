package com.example.resell.ui.viewmodel.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Address
import com.example.resell.model.User
import com.example.resell.repository.AddressRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.AuthTokenManager
import com.example.resell.store.ReactiveStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

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

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authTokenManager: AuthTokenManager,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(UserProfileUiState())
    val uiState: State<UserProfileUiState> = _uiState

    fun loadProfile(targetUserId: String, currentUserId: String) {
        val isCurrent = targetUserId == currentUserId

        viewModelScope.launch {
            val user = if (isCurrent) {
                ReactiveStore<User>().item.value
            } else {
                userRepository.getUserById(targetUserId).getOrNull()
            }

            val stat = userRepository.getUserStat(targetUserId).getOrNull()

            val address = addressRepository.getAddressByUserID(targetUserId)
                .getOrNull()
                ?.firstOrNull { it.isDefault } // Ưu tiên default address
                ?: addressRepository.getAddressByUserID(targetUserId)
                    .getOrNull()
                    ?.firstOrNull() // fallback nếu không có default


            if (user != null && stat != null) {
                _uiState.value = UserProfileUiState(
                    isCurrentUser = isCurrent,
                    userId = user.id,
                    name = user.fullName,
                    avatarUrl = user.avatarURL ?: "",
                    coverUrl = "",
                    rating = stat.averageRating.toString(),
                    reviewCount = stat.reviewNumber,
                    followerCount = stat.followerCount,
                    followingCount = stat.followeeCount,
                    responseRate = "N/A",
                    createdAt = formatCreatedAt(user.createdAt),
                    address = formatAddress(address)
                )
            }
        }
    }


    fun toggleFollow() {
        // TODO: Gọi userRepository.follow hoặc unfollow nếu có
        // Ví dụ:
        // userRepository.follow(userId)
    }

    private fun formatCreatedAt(dateTime: LocalDateTime?): String {
        return if (dateTime == null) "Không rõ"
        else {
            val now = LocalDateTime.now()
            val months = ChronoUnit.MONTHS.between(dateTime, now)
            if (months <= 0) "Mới tham gia"
            else "$months tháng"
        }
    }

    private fun formatAddress(address: Address?): String {
        return if (address == null) {
            "Chưa có địa chỉ"
        } else {
            buildString {
                append(address.detail)
                address.ward?.let { ward ->
                    append(", ${ward.name}")
                    ward.district?.let { district ->
                        append(", ${district.name}")
                        district.province?.let { province ->
                            append(", ${province.name}")
                        }
                    }
                }
            }
        }
    }


}
