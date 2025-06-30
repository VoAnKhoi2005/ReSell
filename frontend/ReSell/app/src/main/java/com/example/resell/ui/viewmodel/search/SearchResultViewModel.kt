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
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var currentPage = 1
    private var isLoadingMore = false
    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState: StateFlow<SearchResultUiState> = _uiState
    private val _selectedCategory = mutableStateOf<Category?>(null)
    val selectedCategory: State<Category?> = _selectedCategory
    private val _selectedProvince = mutableStateOf<Province?>(null)
    val selectedProvince: State<Province?> = _selectedProvince

    // Huyện
    private val _selectedDistrict = mutableStateOf<District?>(null)
    val selectedDistrict: State<District?> = _selectedDistrict

    // Xã
    private val _selectedWard = mutableStateOf<Ward?>(null)
    val selectedWard: State<Ward?> = _selectedWard

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        reloadPosts()
    }
    private val _categoryTree = mutableStateOf<List<CategoryNode>>(emptyList())
    val categoryTree: State<List<CategoryNode>> = _categoryTree

    init {
        viewModelScope.launch {

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

    fun applyLocationSelection(
        province: Province?,
        district: District?,
        ward: Ward?
    ) {
        _selectedProvince.value = province
        _selectedDistrict.value = district
        _selectedWard.value = ward
        reloadPosts()
    }


    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reloadPosts()
    }



    fun setSelectedPriceRange(range: Pair<Int, Int>?) {
        _uiState.update { it.copy(selectedPriceRange = range) }
        reloadPosts()
    }


    fun clearSearchQuery() {
        _uiState.update { it.copy(searchQuery = "") }
        reloadPosts()
    }


    private fun reloadPosts() {
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
                provinceID = _selectedProvince.value?.id,
                districtID = _selectedDistrict.value?.id,
                wardID = _selectedWard.value?.id

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
                provinceID = _selectedProvince.value?.id,
                districtID = _selectedDistrict.value?.id,
                wardID = _selectedWard.value?.id
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


}

data class SearchResultUiState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedPriceRange: Pair<Int, Int>? = null,
    val filteredPosts: List<PostData> = emptyList(),

)

