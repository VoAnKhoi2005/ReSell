package com.example.resell.ui.viewmodel.address

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Address
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.User
import com.example.resell.model.Ward
import com.example.resell.repository.AddressRepository
import com.example.resell.store.ReactiveStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSetupViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {
    private val userStore = ReactiveStore<User>()
    var addressList by mutableStateOf<List<Address>>(emptyList())
        private set

    var selectedAddressID by mutableStateOf<String?>(null)

    init {
        fetchAddresses()
    }

    fun fetchAddresses() {
        viewModelScope.launch {
            val userID = userStore.item.value?.id ?: return@launch
            val result = addressRepository.getAddressByUserID(userID)
            result.fold(
                { error -> Log.e("Address", error.message ?: "Unknown error") },
                { list ->
                    addressList = list
                    selectedAddressID = list.find { it.isDefault }?.id
                }
            )
        }
    }

    fun onSelectAddress(address: Address) {
        selectedAddressID = address.id
        // bạn có thể gửi sự kiện hoặc lưu lại để dùng khi thanh toán, v.v.
    }
    fun updateAddressWard(index: Int, ward: Ward, district: District?, province: Province?) {
        val updatedWard = ward.copy(
            district = district?.copy(
                province = province
            )
        )

        addressList = addressList.toMutableList().also {
            val old = it[index]
            it[index] = old.copy(ward = updatedWard)
        }

    }
    fun deleteAddresses(ids: List<String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = addressRepository.deleteAddresses(ids)
            result.fold(
                ifLeft = { error ->
                    Log.e("AddressViewModel", "❌ Failed to delete addresses: ${error.message}")
                },
                ifRight = { success ->
                    if (success) {
                        Log.d("AddressViewModel", "✅ Addresses deleted")
                        fetchAddresses()
                        onSuccess()
                    }
                }
            )

        }
    }


}
