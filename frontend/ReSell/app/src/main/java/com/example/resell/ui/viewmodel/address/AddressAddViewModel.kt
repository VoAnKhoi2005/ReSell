package com.example.resell.ui.viewmodel.address

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.UpdateAddressRequest
import com.example.resell.model.Ward
import com.example.resell.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressAddViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var fullName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var detail by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    var selectedProvince by mutableStateOf<Province?>(null)
    var selectedDistrict by mutableStateOf<District?>(null)
    var selectedWard by mutableStateOf<Ward?>(null)

    var isEditMode = false
    private val addressId: String? = savedStateHandle["id"]


    init {
        Log.d("AddressViewModel", "Editing addressId = $addressId")
        addressId?.let {
            isEditMode = true
            loadAddress(it)
        }
    }
    fun updateLocation(province: Province, district: District, ward: Ward) {
        selectedProvince = province
        selectedDistrict = district
        selectedWard = ward
    }

    private fun loadAddress(id: String) {
        viewModelScope.launch {
            addressRepository.getAddressByID(id).fold(
                { /* handle error */ },
                { address ->
                    fullName = address.fullname?:""
                    phoneNumber = address.phone?:""
                    detail = address.detail
                    isDefault = address.isDefault
                    selectedWard = address.ward
                    selectedDistrict = address.ward?.district
                    selectedProvince = address.ward?.district?.province
                }
            )
        }
    }

    fun saveAddress(onSaved: () -> Unit) {
        val wardID = selectedWard?.id ?: return
        if (wardID == null) {
            Log.e("AddressViewModel", "❌ Cannot save, ward is null")
            return
        }
        Log.d("AddressViewModel", "🔄 saveAddress() called in ${if (isEditMode) "EDIT" else "CREATE"} mode")

        viewModelScope.launch {
            val result = if (isEditMode && addressId != null) {
                Log.d("AddressViewModel", "Sending updateAddress with id=$addressId")
                addressRepository.updateAddress(
                    addressId,
                    UpdateAddressRequest(fullName, phoneNumber, wardID, detail, isDefault)
                )
            } else {
                Log.d("AddressViewModel", "Sending createAddress")
                addressRepository.createAddress(fullName, phoneNumber, wardID, detail, isDefault)
            }

            result.fold(
                { Log.e("AddressViewModel", "❌ Failed to save address: $it") },
                { success ->
                    if (success) {
                        Log.d("AddressViewModel", "✅ Address saved successfully")
                        onSaved()
                        reset()
                    }
                }
            )
        }
    }


    fun reset() {
        fullName = ""
        phoneNumber = ""
        detail = ""
        isDefault = false
        selectedProvince = null
        selectedDistrict = null
        selectedWard = null
    }

    fun getLocationString(): String = listOfNotNull(
        selectedWard?.name,
        selectedDistrict?.name,
        selectedProvince?.name
    ).joinToString(", ")
}

