package com.example.resell.ui.viewmodel.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.CategoryNode
import com.example.resell.model.District
import com.example.resell.model.GetPostsResponse
import com.example.resell.model.Post
import com.example.resell.model.PostData
import com.example.resell.model.Province
import com.example.resell.model.Ward
import com.example.resell.repository.AddressRepository
import com.example.resell.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.example.resell.model.Category
import com.example.resell.model.buildCategoryTree
import com.example.resell.model.printCategoryTree
import com.example.resell.repository.CategoryRepository

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val addressRepository: AddressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var currentPage = 1
    private var isLoadingMore = false
    private var allProvinces: List<Province> = emptyList()
    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState: StateFlow<SearchResultUiState> = _uiState
    private val _selectedCategory = mutableStateOf<Category?>(null)
    val selectedCategory: State<Category?> = _selectedCategory

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        applyLocationSelection()
    }
    private val _categoryTree = mutableStateOf<List<CategoryNode>>(emptyList())
    val categoryTree: State<List<CategoryNode>> = _categoryTree

    init {
        viewModelScope.launch {
            val result = addressRepository.getAllProvinces()
            result.fold(
                { Log.e("SearchResult", it.message ?: "Error") },
                { provinces ->
                    allProvinces = provinces
                    _uiState.update { it.copy(provinces = provinces) }
                }
            )
            val cate = categoryRepository.getAllCategory()
            cate.fold(
                {
                    Log.e("SearchResult", it.message ?: "Error")
                },{
                    _categoryTree.value = buildCategoryTree(it)
                    printCategoryTree(_categoryTree.value)
                }
            )
        }
    }

    fun loadInitial(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reloadPosts()
    }
    fun setProvince(provinceName: String?) {
        if (provinceName == null) {
            _uiState.update {
                it.copy(
                    region = RegionSelection(province = null, district = null, ward = null),
                    districts = emptyList(),
                    wards = emptyList()
                )
            }
            return
        }
        val province = allProvinces.find { it.name == provinceName } ?: return
        viewModelScope.launch {
            val districtResult = addressRepository.getDistricts(province.id)
            districtResult.fold(
                {
                    Log.e("SearchResult", it.message ?: "Lỗi khi lấy huyện")
                },
                { districts ->
                    _uiState.update {
                        it.copy(
                            region = RegionSelection(province = provinceName, district = null, ward = null),
                            districts = districts,
                            wards = emptyList()
                        )
                    }
                }
            )
        }

    }

    fun setDistrict(districtName: String?) {
        if (districtName == null) {
            _uiState.update {
                it.copy(
                    region = it.region.copy(district = null, ward = null),
                    wards = emptyList()
                )
            }
            return
        }
        val province = allProvinces.find { it.name == _uiState.value.region.province } ?: return
        viewModelScope.launch {
            val districtList = addressRepository.getDistricts(province.id)
            districtList.fold(
                {
                    Log.e("SearchResult", it.message ?: "Lỗi khi lấy huyện")
                },
                { districts ->
                    val district = districts.find { it.name == districtName } ?: return@fold
                    val wardResult = addressRepository.getWards(district.id)
                    wardResult.fold(
                        {
                            Log.e("SearchResult", it.message ?: "Lỗi khi lấy xã")
                        },
                        { wards ->
                            _uiState.update {
                                it.copy(
                                    region = it.region.copy(district = districtName, ward = null),
                                    wards = wards
                                )
                            }
                        }
                    )
                }
            )
        }

    }

    fun setWard(wardName: String?) {
        _uiState.update {
            it.copy(region = it.region.copy(ward = wardName))
        }
    }
    fun applyLocationSelection() {
        val provinceId = getProvinceId()
        val districtId = getDistrictId()
        val wardId = getWardId()

        reloadPosts(provinceId, districtId, wardId)
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reloadPosts()
    }



    fun setSelectedPriceRange(range: Pair<Int, Int>?) {
        _uiState.update { it.copy(selectedPriceRange = range) }
        applyLocationSelection()
    }


    fun clearSearchQuery() {
        _uiState.update { it.copy(searchQuery = "") }
        reloadPosts()
    }


    private fun reloadPosts(provinceID :String="",districtID:String="",wardID:String="") {
        val state = _uiState.value
        viewModelScope.launch {
            val posts = postRepository.getPosts(
                page = 1,
                limit =20,
                status = "approved",
                search = state.searchQuery,
                categoryID = _selectedCategory.value?.id?:"",
                minPrice = state.selectedPriceRange?.first?:0,
                maxPrice = state.selectedPriceRange?.second?:100000000,
                provinceID = provinceID,
                districtID = districtID,
                wardID = wardID

            )
            posts.fold(
                {
                    error->
                    Log.e("GetSearchPost","${error.message}")

                },
                { success ->
                    _uiState.update { it.copy(filteredPosts = success.data?:emptyList()) }
                    isLoadingMore = success.hasMore
                }
            )

        }
    }
    fun loadMore(){
        if (!isLoadingMore) return
        val provinceId = getProvinceId()
        val districtId = getDistrictId()
        val wardId = getWardId()
        val state = _uiState.value
        currentPage++
        viewModelScope.launch {
            val posts = postRepository.getPosts(
                page = currentPage,
                limit =20,
                status = "approved",
                search = state.searchQuery,
                categoryID = state.selectedCategory,
                minPrice = state.selectedPriceRange?.first?:0,
                maxPrice = state.selectedPriceRange?.second?:100000000,
                provinceID = provinceId,
                districtID = districtId,
                wardID = wardId
            )
            posts.fold(
                {
                        error->
                    Log.e("GetSearchPost","${error.message}")

                },
                { success ->
                    val currentList = _uiState.value.filteredPosts
                    _uiState.update {
                        it.copy(filteredPosts = currentList + (success.data ?: emptyList()))
                    }
                    isLoadingMore = success.hasMore
                }
            )

        }
    }
    private fun getProvinceId(): String =
        allProvinces.find { it.name == _uiState.value.region.province }?.id ?: ""

    private fun getDistrictId(): String =
        _uiState.value.districts.find { it.name == _uiState.value.region.district }?.id ?: ""

    private fun getWardId(): String =
        _uiState.value.wards.find { it.name == _uiState.value.region.ward }?.id ?: ""

}
data class RegionSelection(
    val province: String? = null,
    val district: String? = null,
    val ward: String? = null
) {
    val name: String
        get() = when {
            province == null && district == null && ward == null -> "Toàn quốc"
            else -> listOfNotNull(province, district, ward).joinToString(", ")
        }
}



data class SearchResultUiState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedPriceRange: Pair<Int, Int>? = null,
    val region: RegionSelection = RegionSelection(),
    val filteredPosts: List<PostData> = emptyList(),

    val provinces: List<Province> = emptyList(),
    val districts: List<District> = emptyList(),
    val wards: List<Ward> = emptyList()
)

