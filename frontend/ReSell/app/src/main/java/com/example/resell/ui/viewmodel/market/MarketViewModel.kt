package com.example.resell.ui.viewmodel.market

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.PostData
import com.example.resell.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _followPosts = MutableStateFlow<List<PostData>>(emptyList())
    val followPosts: StateFlow<List<PostData>> = _followPosts

    private val _explorePosts = MutableStateFlow<List<PostData>>(emptyList())
    val explorePosts: StateFlow<List<PostData>> = _explorePosts

    private var followPage = 1
    private var explorePage = 1

    private var isLoadingFollow = false
    private var isLoadingExplore = false
    private var isMoreFollow = false
    private var isMoreExplore = false
    init {
        getFollowPosts()
        getExplorePosts()
    }

    fun getFollowPosts() {
        if (isLoadingFollow) return
        isLoadingFollow = true
        viewModelScope.launch {
            val result = repository.getFollowedPosts(page = followPage, limit = 10, status = "approved")
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                    success->
                    isMoreFollow = success.hasMore
                    followPage++
                    _followPosts.value = success.data?:emptyList()
                }
            )
            isLoadingFollow = false
        }
    }

    fun getExplorePosts() {
        if (isLoadingExplore) return
        isLoadingExplore = true
        viewModelScope.launch {
            val result = repository.getPosts(page = explorePage,limit=10, status = "approved")
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                        success->
                    isMoreExplore = success.hasMore
                    explorePage++
                    _explorePosts.value = success.data?:emptyList()
                }
            )
            isLoadingExplore = false
        }
    }
    fun getMoreFollow(){
        if(!isMoreFollow) return
        if (isLoadingFollow) return
        isLoadingFollow = true
        viewModelScope.launch {
            val result = repository.getFollowedPosts(page = followPage, limit = 10, status = "approved")
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                        success->
                    isMoreFollow = success.hasMore
                    followPage++
                    _followPosts.value = _followPosts.value + (success.data ?: emptyList())
                }
            )
            isLoadingFollow = false
        }
    }
    fun getMoreExplore(){
        if(!isMoreExplore) return
        isLoadingExplore = true
        viewModelScope.launch {
            val result = repository.getPosts(page = explorePage,limit=10, status = "approved")
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                        success->
                    isMoreExplore = success.hasMore
                    explorePage++
                    _explorePosts.value =  _explorePosts.value+(success.data?:emptyList())
                }
            )
            isLoadingExplore = false
        }
    }
}
