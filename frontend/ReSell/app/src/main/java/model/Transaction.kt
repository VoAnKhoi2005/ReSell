package model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID


//region User
@JsonClass(generateAdapter = true)
data class FirebaseAuthRequest(
    val firebaseIDToken: String,
    val username: String? = null,
    val password: String? = null,
)

enum class LoginType{
    email, phone, username
}

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String,
    val loginType: LoginType
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
    val fullName: String? = null,
    val citizenId: String? = null
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    val refreshToken: String
)
//endregion

//region Address
@JsonClass(generateAdapter = true)
data class CreateAddressRequest(
    val wardID: String,
    val detail: String,
    val isDefault: Boolean
)

@JsonClass(generateAdapter = true)
data class UpdateAddressRequest(
    val wardID: String? = null,
    val detail: String? = null,
    val isDefault: Boolean? = null
)
//endregion

//region Category
@JsonClass(generateAdapter = true)
data class CreateCategoryRequest(
    val name: String,
    val parentCategoryID: String
)

@JsonClass(generateAdapter = true)
data class UpdateCategoryRequest(
    val name: String? = null,
    //val parentCategoryID: String? = null,
)
//endregion

//region Post
@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    val title: String,
    val description: String,
    val categoryID: String,
    val addressID: String,
    val price: Double
)

@JsonClass(generateAdapter = true)
data class UpdatePostRequest(
    val categoryID: String? = null,
    val addressID: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null
)

@JsonClass(generateAdapter = true)
data class ImageUploadResponse(
    val imageUrls: List<String>,
    val message: String
)
//endregion

//region Order
@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    val postID: String,
    val addressID: String,
    val total: Double
)
//endregion

//region Review
@JsonClass(generateAdapter = true)
data class CreateReviewRequest(
    val orderID: String,
    val rating: Int,
    val comment: String
)
//endregion

//region Message
@JsonClass(generateAdapter = true)
data class CreateConversationRequest(
    val buyerID: String,
    val sellerID: String,
    val postID: String
)

@JsonClass(generateAdapter = true)
data class IncomingSocketMessage(
    val type: String,
    val data: Map<String, Any>
)

enum class SocketMessageType {
    message_send, new_message, typing, error
}

@JsonClass(generateAdapter = true)
data class SocketMessage<T>(
    val type: SocketMessageType,
    val data: T
)

@JsonClass(generateAdapter = true)
data class SendMessagePayload(
    val conversationID: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class TypingPayload(
    val conversationID: String,
    val isTyping: Boolean
)

data class PendingMessage(
    val id: String = UUID.randomUUID().toString(),
    val json: String,
    val raw: Any,
    var status: MessageStatus = MessageStatus.FAILED
)

enum class MessageStatus {
    FAILED,
    SENT,
    PENDING
}
//endregion

//region Notification
data class SaveFCMTokenRequest(
    val token: String,
)
//endregion