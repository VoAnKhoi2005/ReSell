package com.example.resell.ui


import model.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //region User
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Boolean

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @PUT("api/user/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Boolean

    @PUT("api/user/change_password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Boolean

    @DELETE("api/user/delete/{user_id}")
    suspend fun deleteUser(@Path("user_id") userID: String): Boolean

    @POST("api/user/follow/{user_id}")
    suspend fun followUser(@Path("user_id") userID: String): Boolean

    @GET("api/user/follow/all")
    suspend fun getAllFollow(): List<User>

    @DELETE("api/user/unfollow/{user_id}")
    suspend fun unfollowUser(@Path("user_id") userID: String): Boolean
    //endregion

    //region Address
    @POST("api/address/user")
    suspend fun createAddress(
        @Body request: CreateAddressRequest
    ): Boolean

    @GET("api/address/{address_id}")
    suspend fun getAddressByID(@Path("address_id") addressID: String): Address

    @GET("api/address/user/{user_id}")
    suspend fun getAddressByUserID(@Path("user_id") userID: String): List<Address>

    @GET("api/address/provinces/all")
    suspend fun getAllProvinces(): List<Province>

    @GET("api/address/districts/{province_id}")
    suspend fun getDistricts(@Path("province_id") provinceID: String): List<District>

    @GET("api/address/wards/{district_id}")
    suspend fun getWards(@Path("district_id") districtID: String): List<Ward>

    @PUT("api/address/update/{address_id}")
    suspend fun updateAddress(
        @Path("address_id") addressID: String,
        @Body request: UpdateAddressRequest
    ): Boolean

    @DELETE("api/address/{address_id}")
    suspend fun deleteAddress(@Path("address_id") addressID: String): Boolean
    //endregion

    //region Category
    @GET("api/categories")
    suspend fun getAllCategory(): List<Category>

    @GET("api/categories/{category_id}")
    suspend fun getCategoryByID(@Path("category_id") categoryID: String): Category

    @POST("api/admin/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Boolean

    @PUT("api/admin/categories/{category_id}")
    suspend fun updateCategory(
        @Path("category_id") categoryID: String,
        @Body request: UpdateCategoryRequest
    ): Boolean

    @DELETE("api/admin/categories/{category_id}")
    suspend fun deleteCategory(@Path("category_id") categoryID: String): Boolean

    @GET("api/categories/{category_id}/children")
    suspend fun getCategoryChildren(@Path("category_id") categoryID: String): List<Category>
    //endregion

    //region Post
    @GET("")
    suspend fun getPosts():List<Post>
    //endregion

    //region Order
    @POST("api/order/create")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Boolean

    @DELETE("api/order/{order_id}")
    suspend fun deleteOrder(@Path("order_id") orderID: String): Boolean

    @PUT("api/order/{order_id}/set_status/{status}")
    suspend fun updateOrderStatus(
        @Path("order_id") orderID: String,
        @Path("status") status: OrderStatus
    ): Boolean

    @GET("api/order/post/{post_id}")
    suspend fun getOrderByPostID(@Path("post_id") postID: String): ShopOrder

    @GET("api/order/buyer/{buyer_id}")
    suspend fun getOrderByBuyerID(@Path("buyer_id") buyerID: String): List<ShopOrder>

    @GET("api/order/seller/{seller_id}")
    suspend fun getOrderBySellerID(@Path("seller_id") sellerID: String): List<ShopOrder>
    //endregion
}