package com.example.resell.ui.viewmodel.components

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Category
import com.example.resell.model.CategoryNode
import com.example.resell.repository.CategoryRepository
import com.example.resell.model.buildCategoryTree

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoryPickerViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoryTree = mutableStateOf<List<CategoryNode>>(emptyList())
    val categoryTree: State<List<CategoryNode>> = _categoryTree

    private val _currentLevelNodes = mutableStateOf<List<CategoryNode>>(emptyList())
    val currentLevelNodes: State<List<CategoryNode>> = _currentLevelNodes

    private val _path = mutableStateListOf<CategoryNode>()
    val path: List<CategoryNode> = _path

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            categoryRepository.getAllCategory().fold(
                { Log.e("CategoryPicker", it.message ?: "Lỗi tải danh mục") },
                {
                    val tree = buildCategoryTree(it)
                    _categoryTree.value = tree
                    _currentLevelNodes.value = tree
                }
            )
            _isLoading.value = false
        }
    }

    fun onCategorySelected(node: CategoryNode) {
        if (node.children.isEmpty()) {
            // Nếu là lá thì gọi xử lý bên ngoài luôn
        } else {
            _path.add(node)
            _currentLevelNodes.value = node.children
        }
    }

    fun onBack() {
        if (_path.isNotEmpty()) {
            _path.removeAt(_path.lastIndex)
            _currentLevelNodes.value = if (_path.isEmpty()) {
                categoryTree.value
            } else {
                _path.last().children
            }
        }
    }

    fun getCurrentTitle(): String {
        return when {
            _path.isEmpty() -> "Chọn danh mục"
            else -> _path.last().category.name
        }
    }

    fun reset() {
        _path.clear()
        _currentLevelNodes.value = categoryTree.value
    }

    fun getCurrentSelected(): Category? {
        return _path.lastOrNull()?.category
    }
    fun getFullCategoryPath(category: Category?): String {
        if(category == null) return ""
        val path = mutableListOf<String>()

        fun dfs(current: CategoryNode, ancestors: List<String>): Boolean {
            val newAncestors = ancestors + current.category.name
            if (current.category.id == category.id) {
                path.addAll(newAncestors)
                return true
            }
            for (child in current.children) {
                if (dfs(child, newAncestors)) return true
            }
            return false
        }

        for (node in  _categoryTree.value) {
            if (dfs(node, emptyList())) break
        }

        return path.joinToString(" - ")
    }

}
