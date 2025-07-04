package com.example.resell.ui.viewmodel.order.MyOrder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.resell.model.OrderStatus
import com.example.resell.model.Post
import com.example.resell.model.ShopOrder
import com.example.resell.repository.OrderRepository
import com.example.resell.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderWithPost(
    val order: ShopOrder,
    val post: Post
)

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val postRepository: PostRepository
): ViewModel() {

    var ordersWithPosts by mutableStateOf<List<OrderWithPost>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadOrders(buyerId: String) {
        viewModelScope.launch {
            isLoading = true
            orderRepository.getOrderByBuyerID(buyerId).fold(
                ifLeft = { /* xử lý lỗi nếu cần */ },
                ifRight = { orders ->
                    val ordersPosts = mutableListOf<OrderWithPost>()
                    for (order in orders) {
                        when(val postEither = postRepository.getPostByID(order.postId)) {
                            is Either.Right -> {
                                val post = postEither.value
                                ordersPosts.add(OrderWithPost(order, post))
                            }
                            is Either.Left -> {
                                // Xử lý nếu không lấy được bài đăng, có thể bỏ qua hoặc log
                            }
                        }
                    }
                    ordersWithPosts = ordersPosts
                }
            )
            isLoading = false
        }
    }

}
