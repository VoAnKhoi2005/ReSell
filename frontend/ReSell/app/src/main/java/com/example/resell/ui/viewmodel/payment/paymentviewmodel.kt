package com.example.resell.ui.viewmodel.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Address
import com.example.resell.repository.AddressRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress

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
