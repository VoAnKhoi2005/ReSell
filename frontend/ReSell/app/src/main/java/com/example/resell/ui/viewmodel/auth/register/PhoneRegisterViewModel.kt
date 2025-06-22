package com.example.resell.ui.viewmodel.auth.register

import androidx.lifecycle.ViewModel
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PhoneRegisterViewModel @Inject constructor() : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError

    fun onPhoneChanged(input: String) {
        val formatted = input.filter { it.isDigit() } // chỉ lấy số
        _phoneNumber.value = formatted

        _phoneError.value =if (formatted.isNullOrBlank())
        {
            "Không được để trống"
        }
        else if (formatted.length != 10 || formatted.get(0)!='0') {
            "Số điện thoại không hợp lệ"
        }
        else null
    }
    fun onAuthClicked(){
        if (phoneError.value.isNullOrBlank() && phoneNumber.value.isNotBlank()){

            // ✅ Navigate sau khi đã gán xong
            NavigationController.navController.navigate(Screen.PhoneAuth.route+"/${phoneNumber.value}")
        }
    }

}
