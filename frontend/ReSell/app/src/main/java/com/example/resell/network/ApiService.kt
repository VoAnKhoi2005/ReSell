package com.example.resell.network


import com.example.resell.model.Address
import com.example.resell.model.ChangePasswordRequest
import com.example.resell.model.Conversation
import com.example.resell.model.CreateAddressRequest
import com.example.resell.model.CreateCategoryRequest
import com.example.resell.model.CreateConversationRequest
import com.example.resell.model.CreateOrderRequest
import com.example.resell.model.CreatePostRequest
import com.example.resell.model.CreateReviewRequest
import com.example.resell.model.District
import com.example.resell.model.FirebaseAuthRequest
import com.example.resell.model.ImageUploadResponse
import com.example.resell.model.LoginRequest
import com.example.resell.model.LoginResponse
import com.example.resell.model.Message
import com.example.resell.model.Notification
import com.example.resell.model.NotificationType
import com.example.resell.model.OrderStatus
import com.example.resell.model.Post
import com.example.resell.model.Review
import com.example.resell.model.SaveFCMTokenRequest
import com.example.resell.model.ShopOrder
import com.example.resell.model.UpdateAddressRequest
import com.example.resell.model.UpdateCategoryRequest
import com.example.resell.model.UpdatePostRequest
import com.example.resell.model.UpdateProfileRequest
import com.example.resell.model.User
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface ApiService {

    //region User
    @POST("auth/firebase")
    suspend fun firebaseAuth(
        @Body request: FirebaseAuthRequest
    ): LoginResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @PUT("user/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Boolean

    @PUT("user/change_password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Boolean

    @DELETE("user/delete/{user_id}")
    suspend fun deleteUser(@Path("user_id") userID: String): Boolean

    @POST("user/follow/{user_id}")
    suspend fun followUser(@Path("user_id") userID: String): Boolean

    @GET("user/follow/all")
    suspend fun getAllFollow(): List<User>

    @DELETE("user/unfollow/{user_id}")
    suspend fun unfollowUser(@Path("user_id") userID: String): Boolean
    //endregion

    //region Address
    @POST("address/user")
    suspend fun createAddress(
        @Body request: CreateAddressRequest
    ): Boolean

    @GET("address/{address_id}")
    suspend fun getAddressByID(@Path("address_id") addressID: String): Address

    @GET("address/user/{user_id}")
    suspend fun getAddressByUserID(@Path("user_id") userID: String): List<Address>

    @GET("address/provinces/all")
    suspend fun getAllProvinces(): List<com.example.resell.model.Province>

    @GET("address/districts/{province_id}")
    suspend fun getDistricts(@Path("province_id") provinceID: String): List<District>

    @GET("address/wards/{district_id}")
    suspend fun getWards(@Path("district_id") districtID: String): List<com.example.resell.model.Ward>

    @PUT("address/update/{address_id}")
    suspend fun updateAddress(
        @Path("address_id") addressID: String,
        @Body request: UpdateAddressRequest
    ): Boolean

    @DELETE("address/{address_id}")
    suspend fun deleteAddress(@Path("address_id") addressID: String): Boolean
    //endregion

    //region Category
    @GET("categories")
    suspend fun getAllCategory(): List<com.example.resell.model.Category>

    @GET("categories/{category_id}")
    suspend fun getCategoryByID(@Path("category_id") categoryID: String): com.example.resell.model.Category

    @POST("admin/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Boolean

    @PUT("admin/categories/{category_id}")
    suspend fun updateCategory(
        @Path("category_id") categoryID: String,
        @Body request: UpdateCategoryRequest
    ): Boolean

    @DELETE("admin/categories/{category_id}")
    suspend fun deleteCategory(@Path("category_id") categoryID: String): Boolean

    @GET("categories/{category_id}/children")
    suspend fun getCategoryChildren(@Path("category_id") categoryID: String): List<com.example.resell.model.Category>
    //endregion

    //region Post
    @GET("posts/{post_id}")
    suspend fun getPostByID(@Path("post_id") postID: String): Post

    @POST("posts")
    suspend fun createPost(
        @Body request: CreatePostRequest
    ): Boolean

    @PUT("posts/{post_id}")
    suspend fun updatePost(
        @Path("post_id") postID: String,
        @Body request: UpdatePostRequest
    ): Boolean

    @DELETE("posts/{post_id}")
    suspend fun hardDeletePost(@Path("post_id") postID: String): Boolean

    @DELETE("posts/{post_id}/soft-delete")
    suspend fun softDeletePost(@Path("post_id") postID: String): Boolean

    @GET("posts/trash")
    suspend fun getAllDeletedPosts(): List<Post>

    @PUT("posts/{post_id}/restore")
    suspend fun restoreDeletedPost(@Path("post_id") postID: String): Boolean

    @Multipart
    @POST("images/{id}")
    suspend fun uploadPostImages(
        @Path("id") postId: String,
        @Part images: List<MultipartBody.Part>
    ): ImageUploadResponse
    //endregion

    //region Order
    @POST("order/create")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Boolean

    @DELETE("order/{order_id}")
    suspend fun deleteOrder(@Path("order_id") orderID: String): Boolean

    @PUT("order/{order_id}/set_status/{status}")
    suspend fun updateOrderStatus(
        @Path("order_id") orderID: String,
        @Path("status") status: OrderStatus
    ): Boolean

    @GET("order/post/{post_id}")
    suspend fun getOrderByPostID(@Path("post_id") postID: String): ShopOrder

    @GET("order/buyer/{buyer_id}")
    suspend fun getOrderByBuyerID(@Path("buyer_id") buyerID: String): List<ShopOrder>

    @GET("order/seller/{seller_id}")
    suspend fun getOrderBySellerID(@Path("seller_id") sellerID: String): List<ShopOrder>
    //endregion

    //region Review
    @POST("review/create")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Boolean

    @GET("review/buyer/{buyer_id}")
    suspend fun getReviewByBuyerID(@Path("buyer_id") buyerID: String): List<Review>

    @GET("review/post/{post_id}")
    suspend fun getReviewByPostID(@Path("post_id") postID: String): Review

    @GET("review/order/{order_id}")
    suspend fun getReviewByOrderID(@Path("order_id") orderID: String): Review

    @DELETE("review/order/{order_id}")
    suspend fun deleteReviewByOrderID(@Path("order_id") orderID: String): Boolean
    //endregion

    //region Message
    @POST("conversation/create")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): Conversation

    @GET("conversation/{conv_id}")
    suspend fun getConversationByID(@Path("conv_id") conversationID: String): Conversation

    @GET("conversation/post/{post_id}")
    suspend fun getConversationByPostID(@Path("post_id") postID: String): List<Conversation>

    @GET("conversation/all")
    suspend fun getAllConversations(): List<Conversation>

    @DELETE("conversation/{conv_id}")
    suspend fun deleteConversation(@Path("conv_id") conversationID: String): Boolean

    @GET("conversation/{conv_id}/messages/latest/{amount}")
    suspend fun getLatestMessages(
        @Path("conv_id") conversationID: String,
        @Path("amount") amount:Int
    ): List<Message>

    @GET("conversation/{conv_id}/messages/in_range")
    suspend fun getMessagesInRange(
        @Path("conv_id") conversationID: String,
        @Query("start") start: Int,
        @Query("end") end: Int
    ): List<Message>
    //endregion

    //region com.example.resell.model.Notification
    @GET("notification/batch/{batch_size}/{page}")
    suspend fun getNotificationsByBatch(
        @Path("batch_size") batchSize: Int,
        @Path("page") page: Int,
    ): List<Notification>

    @GET("notification/date/{date}")
    suspend fun getNotificationsByDate(@Path("date") date: String): List<Notification>

    @GET("notification/type/{type}")
    suspend fun getNotificationsByType(@Path("type") type: NotificationType): List<Notification>

    @POST("notification/FCM")
    suspend fun saveFCMToken(
        @Body request: SaveFCMTokenRequest
    ): Boolean

    @DELETE("notification/FCM")
    suspend fun deleteFCMToken(): Boolean
    //endregion
}