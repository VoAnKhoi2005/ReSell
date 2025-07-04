package com.example.resell.ui.viewmodel.order.MyOrder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.OrderStatus
import com.example.resell.model.ShopOrder
import com.example.resell.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {
    var orders by mutableStateOf<List<ShopOrder>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadOrders(buyerId: String) {
        viewModelScope.launch {
            isLoading = true
            orderRepository.getOrderByBuyerID(buyerId).fold(
                ifLeft = { /* handle error */ },
                ifRight = { orders = it }
            )
            isLoading = false
        }
    }

    fun getOrdersByStatus(status: OrderStatus?): List<ShopOrder> {
        return if (status == null) orders
        else orders.filter { it.status == status }
    }
}
