package com.example.resell.repository

import FavoritePost
import com.example.resell.model.*
import arrow.core.Either
import com.example.resell.network.NetworkError
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.time.LocalDate

interface UserRepository{
    suspend fun firebaseAuth(
        firebaseIDToken: String,
        username: String? = null,
        fullname: String? = null,
        password: String? = null
    ): Either<NetworkError, FirebaseAuthResponse>
    suspend fun verifyToken(): Either<NetworkError, VerifyTokenResponse>
    suspend fun loginUser(identifier: String, password: String, loginType: LoginType): Either<NetworkError, LoginResponse>
    suspend fun updateInfo(request: UpdateProfileRequest): Either<NetworkError, User>
    suspend fun getUserStat(userID: String): Either<NetworkError, UserStat>
    suspend fun searchUsername(query: String): Either<NetworkError, List<User>>
    suspend fun changePassword(oldPassword: String, newPassword: String): Either<NetworkError, Boolean>
    suspend fun deleteUser(userID: String): Either<NetworkError, Boolean>
    suspend fun followUser(userID: String): Either<NetworkError, Boolean>
    suspend fun getAllFollows(): Either<NetworkError, List<User>>
    suspend fun unfollowUser(userID: String): Either<NetworkError, Boolean>
    suspend fun uploadAvatar(avatar: File): Either<NetworkError, AvatarUploadResponse>
    suspend fun uploadCover(cover: File): Either<NetworkError, CoverUploadResponse>
}

interface SubscriptionRepository{
    suspend fun getAllPlans(): Either<NetworkError, List<SubscriptionPlan>>
    suspend fun getPlanByID(planID: String): Either<NetworkError, SubscriptionPlan>
}

interface FavoriteRepository{
    suspend fun getFavoritePosts(): Either<NetworkError, List<FavoritePost>>
    suspend fun likePost(postID: String): Either<NetworkError, FavoritePost>
    suspend fun unlikePost(postID: String): Either<NetworkError, Boolean>
    suspend fun isFavorite(postID: String): Either<NetworkError, Boolean>
}

interface PaymentRepository{
    suspend fun createZaloPayPayment(orderID: String): Either<NetworkError, CreateZaloPayPaymentResponse>
}

interface AddressRepository{
    suspend fun createAddress(fullName: String, phone: String, wardID: String, detail: String, isDefault: Boolean): Either<NetworkError, Boolean>
    suspend fun getAddressByID(addressID: String): Either<NetworkError, Address>
    suspend fun getAddressByUserID(userID: String): Either<NetworkError, List<Address>>
    suspend fun getDefaultAddress(): Either<NetworkError, Address>
    suspend fun getAllProvinces(): Either<NetworkError, List<Province>>
    suspend fun getDistricts(provinceID: String): Either<NetworkError, List<District>>
    suspend fun getWards(districtID: String): Either<NetworkError, List<Ward>>
    suspend fun updateAddress(addressID: String, request: UpdateAddressRequest): Either<NetworkError, Boolean>
    suspend fun deleteAddress(addressID: String): Either<NetworkError, Boolean>
    suspend fun deleteAddresses(addressIDs: List<String>): Either<NetworkError, Boolean>
}

interface CategoryRepository{
    suspend fun getAllCategory(): Either<NetworkError, List<Category>>
    suspend fun getCategoryByID(categoryID: String): Either<NetworkError, Category>
    suspend fun getChildrenCategories(categoryID: String): Either<NetworkError, List<Category>>
}

interface PostRepository {
    suspend fun getPosts(
        page: Int, limit: Int,
        status: String? = null,
        minPrice: Int? = null, maxPrice: Int? = null,
        provinceID: String? = null, districtID: String? = null, wardID: String? = null,
        userID: String? = null,
        categoryID: String? = null,
        isFavorite: Boolean? = null,
        isFollowing: Boolean? = null,
        search: String? = null,
    ): Either<NetworkError, GetPostsResponse>
    suspend fun getOwnPosts(
        page: Int, limit: Int,
        status: String? = null,
        minPrice: Int? = null, maxPrice: Int? = null,
        provinceID: String? = null, districtID: String? = null, wardID: String? = null,
        categoryID: String? = null,
        search: String? = null,
    ): Either<NetworkError, GetPostsResponse>
    suspend fun getRecommendationPosts(page: Int, limit: Int): Either<NetworkError, GetPostsResponse>
    suspend fun getPostByID(postID: String): Either<NetworkError, Post>
    suspend fun isPostSold(postID: String): Either<NetworkError, Boolean>
    suspend fun createPost(title: String, description: String,
                           categoryID: String, addressID: String,
                           price: Int
    ): Either<NetworkError, Post>
    suspend fun updatePost(postID: String, request: UpdatePostRequest): Either<NetworkError, Boolean>
    suspend fun hardDeletePost(postID: String): Either<NetworkError, Boolean>
    suspend fun softDeletePost(postID: String): Either<NetworkError, Boolean>
    suspend fun getDeletedPosts(): Either<NetworkError, List<Post>>
    suspend fun restoreDeletedPost(postID: String): Either<NetworkError, Boolean>
    suspend fun uploadPostImage(postID: String, images: List<File>): Either<NetworkError, ImageUploadResponse>
    suspend fun deletePostImages(postID: String, imageURLs: List<String>): Either<NetworkError, Boolean>
}

interface OrderRepository{
    suspend fun createOrder(postID: String, addressID: String, total: Int): Either<NetworkError, Boolean>
    suspend fun deleteOrder(orderID: String): Either<NetworkError, Boolean>
    suspend fun updateOrderStatus(orderID: String, status: OrderStatus): Either<NetworkError, Boolean>
    suspend fun getOrderByPostID(postID: String): Either<NetworkError, ShopOrder>
    suspend fun getOrderByBuyerID(buyerID: String): Either<NetworkError, List<ShopOrder>>
    suspend fun getOrderBySellerID(sellerID: String): Either<NetworkError, List<ShopOrder>>
}

interface ReportRepository{
    suspend fun createReportUser(reportedUserID: String, description: String): Either<NetworkError, Unit>
    suspend fun createReportPost(reportedPostID: String, description: String): Either<NetworkError, Unit>
}

interface ReviewRepository{
    suspend fun createReview(orderID: String, rating: Int, comment: String): Either<NetworkError, Boolean>
    suspend fun getReviewByBuyerID(buyerID: String): Either<NetworkError, List<Review>>
    suspend fun getReviewByPostID(postID: String): Either<NetworkError, Review>
    suspend fun getReviewByOrderID(orderID: String): Either<NetworkError, Review>
    suspend fun deleteReviewByOrderID(orderID: String): Either<NetworkError, Boolean>
}

interface MessageRepository{
    val receivedMessage: SharedFlow<Message>
    val isTyping: StateFlow<Boolean>

    suspend fun createConversation(buyerID: String, sellerID: String, postID: String): Either<NetworkError, Conversation>
    suspend fun updateOffer(conversationID: String, isSelling: Boolean? = null, amount: Int? = null): Either<NetworkError, Conversation>
    suspend fun getConversationByID(conversationID: String): Either<NetworkError, Conversation>
    suspend fun getConversationsByPostID(postID: String): Either<NetworkError, List<Conversation>>
    suspend fun getAllUserConversations(): Either<NetworkError, List<Conversation>>
    suspend fun getConversationByPostAndUserID(postID: String): Either<NetworkError, GetConversationByPostAndUserResponse>
    suspend fun getAllUserConversationsDTO(): Either<NetworkError, List<ConversationStatDTO>>
    suspend fun deleteConversation(conversationID: String): Either<NetworkError, Boolean>
    suspend fun getLatestMessages(conversationID: String, amount: Int): Either<NetworkError,List<Message>>
    suspend fun getLatestMessagesByBatch(conversationID: String, batchSize: Int, page: Int): Either<NetworkError, GetLatestMessagesByBatchResponse>
    suspend fun sendNewMessage(conversationID: String, content: String): Either<ErrorPayload, Message>
    suspend fun sendInChatIndicator(conversationID: String, isInChat: Boolean): Boolean
    suspend fun sendTypingIndicator(conversationID: String, userID: String, isTyping: Boolean)
    suspend fun uploadImage(image: File): Either<NetworkError, String>
}

interface NotificationRepository{
    suspend fun getNotificationsByBatch(batchSize: Int, page: Int): Either<NetworkError, GetNotificationByBatchResponse>
    suspend fun getNotificationsByDate(date: LocalDate): Either<NetworkError, List<Notification>>
    suspend fun getNotificationsByType(type: NotificationType): Either<NetworkError, List<Notification>>
}