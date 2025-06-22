package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

// region User

@JsonClass(generateAdapter = true)
data class FirebaseAuthRequest(
    @Json(name = "firebase_id_token") val firebaseIDToken: String,
    val username: String? = null,
    val password: String? = null
)

enum class LoginType {
    @Json(name = "email")
    EMAIL,

    @Json(name = "phone")
    PHONE,

    @Json(name = "username")
    USERNAME
}

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String,
    @Json(name = "login_type") val loginType: LoginType
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val user: User,
    val token: AuthToken
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "citizen_id") val citizenId: String? = null
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    @Json(name = "old_password") val oldPassword: String,
    @Json(name = "new_password") val newPassword: String
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @Json(name = "refresh_token") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class AvatarUploadResponse(
    @Json(name = "avatar_url") val avatarURL: String
)

// endregion

// region Address

@JsonClass(generateAdapter = true)
data class CreateAddressRequest(
    @Json(name = "ward_id") val wardID: String,
    val detail: String,
    @Json(name = "is_default") val isDefault: Boolean
)

@JsonClass(generateAdapter = true)
data class UpdateAddressRequest(
    @Json(name = "ward_id") val wardID: String? = null,
    val detail: String? = null,
    @Json(name = "is_default") val isDefault: Boolean? = null
)

// endregion

// region Category

@JsonClass(generateAdapter = true)
data class CreateCategoryRequest(
    val name: String,
    @Json(name = "parent_category_id") val parentCategoryID: String
)

@JsonClass(generateAdapter = true)
data class UpdateCategoryRequest(
    val name: String? = null
    // parentCategoryID intentionally removed
)

// endregion

// region Post

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    val title: String,
    val description: String,
    @Json(name = "category_id") val categoryID: String,
    @Json(name = "address_id") val addressID: String,
    val price: Double
)

@JsonClass(generateAdapter = true)
data class GetPostsResponse(
    val data: List<Post>? = null,
    @Json(name = "has_more")
    val hasMore: Boolean,
    val limit: Int,
    val page: Int,
    val total: Int,
)

@JsonClass(generateAdapter = true)
data class UpdatePostRequest(
    @Json(name = "category_id") val categoryID: String? = null,
    @Json(name = "address_id") val addressID: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null
)

@JsonClass(generateAdapter = true)
data class ImageUploadResponse(
    val imageURLs: List<String>,
    val message: String? = null
)

// endregion

// region Order

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    @Json(name = "post_id") val postID: String,
    @Json(name = "address_id") val addressID: String,
    val total: Double
)

// endregion

// region Review

@JsonClass(generateAdapter = true)
data class CreateReviewRequest(
    @Json(name = "order_id") val orderID: String,
    val rating: Int,
    val comment: String
)

// endregion

// region Message

@JsonClass(generateAdapter = true)
data class CreateConversationRequest(
    @Json(name = "buyer_id") val buyerID: String,
    @Json(name = "seller_id") val sellerID: String,
    @Json(name = "post_id") val postID: String
)

enum class SocketMessageType {
    @Json(name = "send_message")
    SEND_MESSAGE,

    @Json(name = "new_message")
    NEW_MESSAGE,

    @Json(name = "typing")
    TYPING,

    @Json(name = "error")
    ERROR,

    @Json(name = "in_chat")
    IN_CHAT
}

@JsonClass(generateAdapter = true)
data class SocketMessage<T>(
    val type: SocketMessageType,
    val data: T
)

@JsonClass(generateAdapter = true)
data class SendMessagePayload(
    @Json(name = "temp_message_id") val tempMessageID: String,
    @Json(name = "message") val message: Message
)

@JsonClass(generateAdapter = true)
data class NewMessagePayload(
    @Json(name = "temp_message_id") val tempMessageID: String = UUID.randomUUID().toString(),
    @Json(name = "conversation_id") val conversationID: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class TypingIndicatorPayload(
    @Json(name = "conversation_id") val conversationID: String,
    @Json(name = "user_id") val userID: String,
    @Json(name = "is_typing") val isTyping: Boolean
)

@JsonClass(generateAdapter = true)
data class InChatIndicatorPayload(
    @Json(name = "temp_message_id") val tempMessageID: String = UUID.randomUUID().toString(),
    @Json(name = "conversation_id") val conversationID: String,
    @Json(name = "is_in_chat") val isInChat: Boolean
)

@JsonClass(generateAdapter = true)
data class ErrorPayload(
    @Json(name = "temp_message_id") val tempMessageID: String? = null,
    @Json(name = "error") val error: String
)

@JsonClass(generateAdapter = true)
data class PendingMessage(
    val id: String = UUID.randomUUID().toString(),
    val json: String,
    val raw: Any
)

sealed class AckResult {
    data class Success(val payload: SendMessagePayload) : AckResult()
    data class Error(val error: ErrorPayload) : AckResult()
}

// endregion

// region Notification

@JsonClass(generateAdapter = true)
data class SaveFCMTokenRequest(
    val token: String
)

// endregion

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String? = null,
    val errors: Map<String, String>? = null
)
