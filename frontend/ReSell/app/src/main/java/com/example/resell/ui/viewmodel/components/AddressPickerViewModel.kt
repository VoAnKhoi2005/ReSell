package com.example.resell.ui.viewmodel.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.AddressRepository
import com.example.resell.model.Province
import com.example.resell.model.District
import com.example.resell.model.Ward
import com.example.resell.ui.components.AddressSelectionStep
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch



@HiltViewModel
class AddressPickerViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    var provinces by mutableStateOf<List<Province>>(emptyList())
        private set
    var districts by mutableStateOf<List<District>>(emptyList())
        private set
    var wards by mutableStateOf<List<Ward>>(emptyList())
        private set

    var selectedProvince by mutableStateOf<Province?>(null)
    var selectedDistrict by mutableStateOf<District?>(null)
    var selectedWard by mutableStateOf<Ward?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectionStep by mutableStateOf<AddressSelectionStep>(AddressSelectionStep.Province)


    init {
        loadProvinces()
    }

    fun loadProvinces() {
        viewModelScope.launch {
            isLoading = true
            repository.getAllProvinces().fold(
                { errorMessage = "Không thể tải tỉnh/thành phố" },
                {
                    provinces = it
                    errorMessage = null
                }
            )
            isLoading = false
        }
    }

    fun onProvinceSelected(province: Province) {
        selectedProvince = province
        selectedDistrict = null
        selectedWard = null
        selectionStep = AddressSelectionStep.District
        loadDistricts(province.id)
    }

    fun onDistrictSelected(district: District) {
        selectedDistrict = district
        selectedWard = null
        selectionStep = AddressSelectionStep.Ward
        loadWards(district.id)
    }

    fun onWardSelected(ward: Ward) {
        selectedWard = ward
    }

    fun onBackStep() {
        selectionStep = when (selectionStep) {
            AddressSelectionStep.District -> AddressSelectionStep.Province
            AddressSelectionStep.Ward -> AddressSelectionStep.District
            else -> selectionStep
        }
    }
    fun getSelectedLocationName(): String {
        val province = selectedProvince
        val district = selectedDistrict
        val ward = selectedWard

        return if (province == null && district == null && ward == null) {
            "Toàn quốc"
        } else {
            listOfNotNull(ward?.name, district?.name, province?.name)
                .joinToString(", ")
        }
    }

    private fun loadDistricts(provinceId: String) {
        viewModelScope.launch {
            isLoading = true
            repository.getDistricts(provinceId).fold(
                { errorMessage = "Không thể tải quận/huyện" },
                {
                    districts = it
                    errorMessage = null
                }
            )
            isLoading = false
        }
    }

    private fun loadWards(districtId: String) {
        viewModelScope.launch {
            isLoading = true
            repository.getWards(districtId).fold(
                { errorMessage = "Không thể tải phường/xã" },
                {
                    wards = it
                    errorMessage = null
                }
            )
            isLoading = false
        }
    }

    fun reset() {
        selectedProvince = null
        selectedDistrict = null
        selectedWard = null
        districts = emptyList()
        wards = emptyList()
        loadProvinces()
    }
}
