package com.example.resell.ui.viewmodel.profile

import android.content.Context
import android.net.Uri
import android.util.Log
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
import java.io.File
import java.io.InputStream
import java.io.OutputStream
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
        Log.d("PROFILE_VM", "Loading profile: target=$targetUserId, current=$currentUserId")
        viewModelScope.launch {
            val result = runCatching {
                userRepository.getUserStat(targetUserId)
            }.onFailure {
                Log.e("PROFILE_VM", "Exception when fetching user stat: ${it.message}", it)
            }

            result.getOrNull()?.onRight { stat ->
                Log.d("PROFILE_VM", "Stat from repo: $stat")
                _uiState.value = UserProfileUiState(
                    isCurrentUser = targetUserId == currentUserId,
                    userId = stat.userId,
                    name = stat.username ?: "Không rõ",
                    avatarUrl = stat.avatarURL.orEmpty(),
                    coverUrl = stat.coverURL.orEmpty(),
                    rating = stat.averageRating?.toString() ?: "0.0",
                    reviewCount = stat.reviewNumber,
                    followerCount = stat.followerCount,
                    followingCount = stat.followeeCount,
                    responseRate = formatResponseRate(stat.chatResponsePercentage),
                    createdAt = formatCreatedAt(stat.createAt),
                )
            } ?: Log.e("PROFILE_VM", "UserStat result was null or left for user: $targetUserId")
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
    private fun formatResponseRate(percent: Float?): String {
        return if (percent == null) "N/A" else "${percent.toInt()}%"
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = uriToFile(context, uri)
            if (file != null) {
                val result = userRepository.uploadAvatar(file)
                result.onRight { response ->
                    // Cập nhật ReactiveStore để đồng bộ toàn hệ thống
                    val store = ReactiveStore<User>()
                    val currentUser = store.item.value
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(avatarURL = response.avatarURL)
                        store.set(updatedUser) // ✅ dùng set
                    }

                    // Cập nhật lại UI
                    _uiState.value = _uiState.value.copy(avatarUrl = response.avatarURL)
                }
                result.onLeft {
                    // TODO: xử lý khi lỗi nếu cần
                }
            }
        }
    }


    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("avatar", ".jpg", context.cacheDir)
            val outputStream: OutputStream = tempFile.outputStream()
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



}
