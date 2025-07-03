package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.LocalDateTime
import java.util.UUID

// region User

@JsonClass(generateAdapter = true)
data class VerifyTokenResponse(
    @Json(name = "user") val user: User,
    @Json(name = "token") val token: AuthToken,
)

@JsonClass(generateAdapter = true)
data class FirebaseAuthRequest(
    @Json(name = "firebase_id_token") val firebaseIDToken: String,
    val username: String? = null,
    val fullname: String? = null,
    val password: String? = null
)

@JsonClass(generateAdapter = true)
data class FirebaseAuthResponse(
    val user: User? = null,
    val token: AuthToken? = null,
    @Json(name = "first_time_login") val firstTimeLogin: Boolean
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
    val email: String? = null,
    val phone: String? = null,
    @Json(name = "fullname") val fullName: String? = null,
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

@JsonClass(generateAdapter = true)
data class CoverUploadResponse(
    @Json(name = "cover_url") val coverURL: String
)
// endregion

//region Favorite

@JsonClass(generateAdapter = true)
data class LikePostRequest(
    @Json(name = "post_id") val postID: String,
)

//endregion

//region Payment

@JsonClass(generateAdapter = true)
data class CreateZaloPayPaymentResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "payment_url")
    val paymentURL: String
)

//endregion

// region Address

@JsonClass(generateAdapter = true)
data class CreateAddressRequest(
    @Json(name = "fullname") val fullName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "ward_id") val wardID: String,
    @Json(name = "detail") val detail: String,
    @Json(name = "is_default") val isDefault: Boolean
)

@JsonClass(generateAdapter = true)
data class UpdateAddressRequest(
    @Json(name = "fullname") val fullName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "ward_id") val wardID: String? = null,
    val detail: String? = null,
    @Json(name = "is_default") val isDefault: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class DeleteAddressesRequest(
    @Json(name = "address_ids") val addressIDs: List<String>
)

// endregion

// region Category
// endregion

// region Post

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    val title: String,
    val description: String,
    @Json(name = "category_id") val categoryID: String,
    @Json(name = "ward_id") val wardID: String,
    val price: Int
)

@JsonClass(generateAdapter = true)
data class GetPostsResponse(
    val data: List<PostData>? = null,
    @Json(name = "has_more")
    val hasMore: Boolean,
    val limit: Int,
    val page: Int,
    val total: Int,
)

@JsonClass(generateAdapter = true)
data class UpdatePostRequest(
    @Json(name = "category_id") val categoryID: String? = null,
    @Json(name = "ward_id") val wardID: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null
)

@JsonClass(generateAdapter = true)
data class ImageUploadResponse(
    @Json(name="image_urls") val imageURLs: List<String>,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class DeletePostImagesRequest(
    @Json(name = "image_urls")
    val imageUrls: List<String>
)

// endregion

// region Order

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    @Json(name = "post_id") val postID: String,
    @Json(name = "address_id") val addressID: String,
    val total: Int
)

// endregion

//region Report

@JsonClass(generateAdapter = true)
data class ReportUserRequest(
    @Json(name = "reported_id")
    val reportedId: String,

    @Json(name = "description")
    val description: String
)

@JsonClass(generateAdapter = true)
data class ReportPostRequest(
    @Json(name = "reported_id")
    val reportedId: String,

    @Json(name = "description")
    val description: String
)

//endregion

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
data class ConversationStatDTO(
    @Json(name = "conversation_id")
    val conversationId: String,
    @Json(name = "seller_id")
    val sellerId: String,
    @Json(name = "seller_full_name")
    val sellerFullName: String,
    @Json(name = "seller_avatar")
    val sellerAvatar: String? = null,
    @Json(name = "buyer_id")
    val buyerId: String,
    @Json(name = "buyer_full_name")
    val buyerFullName: String,
    @Json(name = "buyer_avatar")
    val buyerAvatar: String? = null,
    @Json(name = "post_id")
    val postId: String,
    @Json(name = "post_title")
    val postTitle: String,
    @Json(name = "post_thumbnail")
    val postThumbnail: String,
    @Json(name = "last_message")
    val lastMessage: String,
    @Json(name = "created_at")
    val createdAt: LocalDateTime,
    @Json(name = "last_updated_at")
    val lastUpdatedAt: LocalDateTime
)

@JsonClass(generateAdapter = true)
data class GetConversationByPostAndUserResponse(
    @Json(name = "conversation") val conversation: Conversation?=null,
    @Json(name = "is_exist") val isExist: Boolean,
)

@JsonClass(generateAdapter = true)
data class GetLatestMessagesByBatchResponse(
    @Json(name = "messages") val messages: List<Message>,
    @Json(name = "total_batch_count") val totalBatchCount: Int,
)

@JsonClass(generateAdapter = true)
data class CreateConversationRequest(
    @Json(name = "buyer_id") val buyerID: String,
    @Json(name = "seller_id") val sellerID: String,
    @Json(name = "post_id") val postID: String
)

@JsonClass(generateAdapter = true)
data class UpdateOfferRequest(
    @Json(name = "conversation_id") val conversationID: String,
    @Json(name = "is_selling") val isSelling: Boolean? = null,
    @Json(name = "amount") val amount: Int? = null
)

enum class SocketMessageType {
    @Json(name = "ack_message")
    ACK_MESSAGE,

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
data class ACKMessagePayload(
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
    data class Success(val payload: ACKMessagePayload) : AckResult()
    data class Error(val error: ErrorPayload) : AckResult()
}

// endregion

// region Notification

@JsonClass(generateAdapter = true)
data class SaveFCMTokenRequest(
    val token: String
)

@JsonClass(generateAdapter = true)
data class GetNotificationByBatchResponse(
    val notifications: List<Notification>,
    @Json(name = "total_batch_count")
    val totalBatchCount: Int
)

// endregion

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String? = null,
    val errors: Map<String, String>? = null
)
