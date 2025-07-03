package com.example.resell.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.UpdateProfileRequest
import com.example.resell.model.User
import com.example.resell.repository.AddressRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {
    private val userStore = ReactiveStore<User>()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _defaultAddressText = MutableStateFlow("")
    val defaultAddressText: StateFlow<String> = _defaultAddressText

    fun setPhone(phone: String){
        _phone.value=phone
    }
    fun setName(name: String){
        _name.value=name
    }

    fun setEmail(email: String){
        _email.value=email
    }



    init {
        viewModelScope.launch {
            userStore.item.collect { user ->
                _currentUser.value = user
                _phone.value = user?.phone ?: ""
                _name.value = user?.fullName ?: ""
                _email.value = user?.email ?: ""
            }
        }

        viewModelScope.launch {
            addressRepository.getDefaultAddress().fold(
                { _defaultAddressText.value = "Không có địa chỉ mặc định" },
                { address ->
                    _defaultAddressText.value = listOfNotNull(
                        address.detail,
                        address.ward?.name,
                        address.ward?.district?.name,
                        address.ward?.district?.province?.name
                    ).joinToString(", ")
                }
            )
        }
    }


    fun saveChanges(
    ) {
        viewModelScope.launch {
            val request = UpdateProfileRequest(
                fullName = _name.value,
                phone = _phone.value,
                email =_email.value
            )

            val result = userRepository.updateInfo(request)
            result.fold(
                {
                    EventBus.sendEvent(Event.Toast("Cập nhật thất bại: ${it.message}"))
                },
                {
                   userStore.set(it)
                    EventBus.sendEvent(Event.Toast("Cập nhật thành công"))
                }
            )
        }
    }


}
