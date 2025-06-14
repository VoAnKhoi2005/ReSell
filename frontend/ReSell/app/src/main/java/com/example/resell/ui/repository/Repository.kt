package com.example.resell.ui.repository

import model.*
import arrow.core.Either
import com.example.resell.ui.domain.NetworkError

interface UserRepository{
    suspend fun registerUser(username: String, email: String, phone: String, password: String): Either<NetworkError, Boolean>
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
    suspend fun getAllProvinces(): Either<NetworkError, List<Province>>
    suspend fun getDistricts(provinceID: String): Either<NetworkError, List<District>>
    suspend fun getWards(districtID: String): Either<NetworkError, List<Ward>>
    suspend fun updateAddress(addressID: String, request: UpdateAddressRequest): Either<NetworkError, Boolean>
    suspend fun deleteAddress(addressID: String): Either<NetworkError, Boolean>
}

interface CategoryRepository{
    suspend fun getAllCategory(): Either<NetworkError, List<Category>>
    suspend fun getCategoryByID(categoryID: String): Either<NetworkError, Category>
    suspend fun createCategory(name: String, parentCategoryID: String): Either<NetworkError, Boolean>
    suspend fun updateCategory(categoryID: String, request: UpdateCategoryRequest): Either<NetworkError, Boolean>
    suspend fun deleteCategory(categoryID: String): Either<NetworkError, Boolean>
    suspend fun getChildrenCategories(categoryID: String): Either<NetworkError, List<Category>>
}

interface PostRepository {
    suspend fun getPosts(): Either<NetworkError, List<Post>>
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
}