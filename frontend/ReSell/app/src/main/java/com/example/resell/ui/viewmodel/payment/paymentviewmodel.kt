package com.example.resell.ui.viewmodel.payment

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Address
import com.example.resell.model.Conversation
import com.example.resell.model.Post
import com.example.resell.repository.AddressRepository
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.OrderRepository
import com.example.resell.repository.OrderRepositoryImpl
import com.example.resell.repository.PaymentRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val messageRepository: MessageRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    val postFlow: StateFlow<Post?> = ReactiveStore<Post>().item
    val conversationFlow: StateFlow<Conversation?> = ReactiveStore<Conversation>().item
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress
    private val _selectedPaymentMethod = MutableStateFlow(0) // 0: COD, 1: ZaloPay
    val selectedPaymentMethod: StateFlow<Int> = _selectedPaymentMethod

    fun selectPaymentMethod(index: Int) {
        _selectedPaymentMethod.value = index
    }

    init {
        savedStateHandle.getStateFlow("selectedAddressId", "")
            .onEach { id ->
                if (id.isNotBlank()) {
                    fetchAddressById(id)
                } else {
                    fetchDefaultAddress()
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchAddressById(addressId: String) {
        viewModelScope.launch {
            addressRepository.getAddressByID(addressId).fold(
                ifLeft = {

                },
                ifRight = {
                    _selectedAddress.value = it
                }
            )
        }
    }
    fun orderWithCOD(){
        viewModelScope.launch {

            val order = orderRepository.getOrderByPostID(postFlow.value!!.id)
            order.fold(
                {
                    Log.e("Get Order",it.message?:"")
                },{
                    it->
                }
            )

            val result = orderRepository.createOrder(postFlow.value!!.id,_selectedAddress.value?.id?:"",conversationFlow.value!!.offer!!)
                result.fold(
                    {
                        Log.e("Create Order",it.message?:"")
                    },{
                            it->

                    }
                )
        }
    }
    fun orderWithZaloPay(){
        viewModelScope.launch {

            val order = orderRepository.getOrderByPostID(postFlow.value!!.id)
            order.fold(
                {
                    Log.e("Get Order",it.message?:"")
                },{
                        it->
                    val pay = paymentRepository.createZaloPayPayment(it.id)
                    pay.fold({
                        Log.e("Payment",it.message?:"")
                    },{ it->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.paymentURL))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // bắt buộc khi mở từ Application context
                        getApplication<Application>().startActivity(intent)
                    })

                }
            )

        }
    }

    private fun fetchDefaultAddress() {
        viewModelScope.launch {
            addressRepository.getDefaultAddress().fold(
                ifLeft = {

                },
                ifRight = {
                    _selectedAddress.value = it
                }
            )
        }
    }
}
