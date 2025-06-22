package com.example.resell.repository

import com.example.resell.model.*
import arrow.core.Either
import com.example.resell.network.NetworkError
import java.time.LocalDate

interface UserRepository{
    suspend fun firebaseAuth(firebaseIDToken: String, username: String? = null, password: String? = null): Either<NetworkError, LoginResponse>
    suspend fun loginUser(identifier: String, password: String, loginType: LoginType): Either<NetworkError, LoginResponse>
    suspend fun updateInfo(request: UpdateProfileRequest): Either<NetworkError, Boolean>
    suspend fun changePassword(oldPassword: String, newPassword: String): Either<NetworkError, Boolean>
    suspend fun deleteUser(userID: String): Either<NetworkError, Boolean>
    suspend fun followUser(userID: String): Either<NetworkError, Boolean>
    suspend fun getAllFollows(): Either<NetworkError, List<User>>
    suspend fun unfollowUser(userID: String): Either<NetworkError, Boolean>
}

interface AddressRepository{
    suspend fun createAddress(wardID: String, detail: String, isDefault: Boolean): Either<NetworkError, Boolean>
    suspend fun getAddressByID(addressID: String): Either<NetworkError, Address>
    suspend fun getAddressByUserID(userID: String): Either<NetworkError, List<Address>>
    suspend fun getAllProvinces(): Either<NetworkError, List<com.example.resell.model.Province>>
    suspend fun getDistricts(provinceID: String): Either<NetworkError, List<District>>
    suspend fun getWards(districtID: String): Either<NetworkError, List<com.example.resell.model.Ward>>
    suspend fun updateAddress(addressID: String, request: UpdateAddressRequest): Either<NetworkError, Boolean>
    suspend fun deleteAddress(addressID: String): Either<NetworkError, Boolean>
}

interface CategoryRepository{
    suspend fun getAllCategory(): Either<NetworkError, List<com.example.resell.model.Category>>
    suspend fun getCategoryByID(categoryID: String): Either<NetworkError, com.example.resell.model.Category>
    suspend fun createCategory(name: String, parentCategoryID: String): Either<NetworkError, Boolean>
    suspend fun updateCategory(categoryID: String, request: UpdateCategoryRequest): Either<NetworkError, Boolean>
    suspend fun deleteCategory(categoryID: String): Either<NetworkError, Boolean>
    suspend fun getChildrenCategories(categoryID: String): Either<NetworkError, List<com.example.resell.model.Category>>
}

interface PostRepository {
    suspend fun getPostByID(postID: String): Either<NetworkError, Post>
    suspend fun createPost(title: String, description: String,
                           categoryID: String, addressID: String,
                           price: Double): Either<NetworkError, Boolean>
    suspend fun updatePost(postID: String, request: UpdatePostRequest): Either<NetworkError, Boolean>
    suspend fun hardDeletePost(postID: String): Either<NetworkError, Boolean>
    suspend fun softDeletePost(postID: String): Either<NetworkError, Boolean>
    suspend fun getDeletedPosts(): Either<NetworkError, List<Post>>
    suspend fun restoreDeletedPost(postID: String): Either<NetworkError, Boolean>
    suspend fun uploadPostImage(postID: String): Either<NetworkError, ImageUploadResponse>
}

interface OrderRepository{
    suspend fun createOrder(postID: String, addressID: String, total: Double): Either<NetworkError, Boolean>
    suspend fun deleteOrder(orderID: String): Either<NetworkError, Boolean>
    suspend fun updateOrderStatus(orderID: String, status: OrderStatus): Either<NetworkError, Boolean>
    suspend fun getOrderByPostID(postID: String): Either<NetworkError, ShopOrder>
    suspend fun getOrderByBuyerID(buyerID: String): Either<NetworkError, List<ShopOrder>>
    suspend fun getOrderBySellerID(sellerID: String): Either<NetworkError, List<ShopOrder>>
}

interface ReviewRepository{
    suspend fun createReview(orderID: String, rating: Int, comment: String): Either<NetworkError, Boolean>
    suspend fun getReviewByBuyerID(buyerID: String): Either<NetworkError, List<Review>>
    suspend fun getReviewByPostID(postID: String): Either<NetworkError, Review>
    suspend fun getReviewByOrderID(orderID: String): Either<NetworkError, Review>
    suspend fun deleteReviewByOrderID(orderID: String): Either<NetworkError, Boolean>
}

interface MessageRepository{
    suspend fun createConversation(buyerID: String, sellerID: String, postID: String): Either<NetworkError, Conversation>
    suspend fun getConversationByID(conversationID: String): Either<NetworkError, Conversation>
    suspend fun deleteConversation(conversationID: String): Either<NetworkError, Boolean>
    suspend fun getConversationsByPostID(postID: String): Either<NetworkError, List<Conversation>>
    suspend fun getLatestMessage(conversationID: String, amount: Int): Either<NetworkError,List<Message>>
    suspend fun getMessageInRange(conversationID: String, start: Int, end: Int): Either<NetworkError,List<Message>>
    suspend fun sendNewMessage(conversationID: String, content: String): Either<ErrorPayload, Message>
    suspend fun sendInChatIndicator(conversationID: String, isInChat: Boolean): Boolean
    suspend fun sendTypingIndicator(conversationID: String, userID: String, isTyping: Boolean)
}

interface NotificationRepository{
    suspend fun getNotificationsByBatch(batchSize: Int, page: Int): Either<NetworkError, List<Notification>>
    suspend fun getNotificationsByDate(date: LocalDate): Either<NetworkError, List<Notification>>
    suspend fun getNotificationsByType(type: NotificationType): Either<NetworkError, List<Notification>>
}