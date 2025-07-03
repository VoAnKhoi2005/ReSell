package com.example.resell.ui.viewmodel.market

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Post
import com.example.resell.model.PostData
import com.example.resell.repository.FavoriteRepository
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.ui.navigation.NavigationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: PostRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _followPosts = MutableStateFlow<List<PostData>>(emptyList())
    val followPosts: StateFlow<List<PostData>> = _followPosts

    private val _explorePosts = MutableStateFlow<List<PostData>>(emptyList())
    val explorePosts: StateFlow<List<PostData>> = _explorePosts
    private val _recommendedPosts = MutableStateFlow<List<PostData>>(emptyList())
    val recommendedPosts: StateFlow<List<PostData>> = _recommendedPosts

    private var followPage = 1
    private var explorePage = 1

    private var recommendedPage =1

    private var isLoadingRecommended = false
    private var isMoreRecommended = false
    private var isLoadingFollow = false
    private var isLoadingExplore = false
    private var isMoreFollow = false
    private var isMoreExplore = false
    init {
        getFollowPosts()
        getExplorePosts()
        getRecommendedPosts()
    }
    fun getRecommendedPosts() {
        if (isLoadingRecommended) return
        isLoadingRecommended = true
        viewModelScope.launch {
            val result = repository.getRecommendationPosts(page = recommendedPage, limit = 10)
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                        success->
                    isMoreRecommended = success.hasMore
                    recommendedPage++
                    _recommendedPosts.value = success.data?:emptyList()
                }
            )
            isLoadingRecommended = false
        }
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
    fun getMoreRecommended(){
        if(!isMoreRecommended) return
        if(isLoadingRecommended) return
        isLoadingRecommended=true
        viewModelScope.launch {
            val result = repository.getRecommendationPosts(page=recommendedPage, limit = 10)
            result.fold(
                {
                    Log.e("Get post",it.message?:"")
                },{
                        success->
                    isMoreRecommended = success.hasMore
                    recommendedPage++
                    _recommendedPosts.value = _recommendedPosts.value + (success.data ?: emptyList())
                }
            )
            isLoadingRecommended = false
        }
    }
    fun toggleFollow(userId: String,isFollow: Boolean) {
        viewModelScope.launch {
            // Gọi API
            val success = if (isFollow) {
                userRepository.unfollowUser(userId)
            } else {
                userRepository.followUser(userId)
            }
            success.fold(
                {
                    Log.e("Update Follow",it.message?:"Error")
                },{
                    updateFollowStatus(userId, !isFollow)
                }
            )
        }
    }
    fun toggleFavorite(postId: String,isFavorite: Boolean){
        viewModelScope.launch {
            val result = if(isFavorite) {
                favoriteRepository.unlikePost(postId)
            } else{
                favoriteRepository.likePost(postId)
            }
            result.fold(
                {
                    Log.e("Like post","${it.message}")
                },
                {
                    _followPosts.update { posts ->
                        posts.map {
                            if (it.id == postId) it.copy(isFavorite = !isFavorite) else it
                        }
                    }

                    _explorePosts.update { posts ->
                        posts.map {
                            if (it.id == postId) it.copy(isFavorite = !isFavorite) else it
                        }
                    }
                }
            )
        }
    }
    fun openConversation(postId: String){
        viewModelScope.launch {
            val result = messageRepository.getConversationByPostAndUserID(postId)
            result.fold (
                ifLeft = {error ->
                    Log.e("Market", "Lỗi lấy cuộc trò chuyện: ${error.message}")
                },
                ifRight = { conversation ->
                    Log.d("Conversation: ","${conversation}")
                    if (conversation.isExist) {
                        NavigationController.navController.navigate("chat/${conversation.conversation!!.id}")
                    }
                    else {
                        Log.d("Market","Chưa có cuộc trò chuyện")
                        val post = repository.getPostByID(postId)
                        post.fold({
                            Log.e("Market", "Lỗi lấy post: ${it.message}")
                        },{
                            ReactiveStore<Post>().set(it)
                            NavigationController.navController.navigate("chat/")
                        })

                    }
                }
            )
        }
    }
    private fun updateFollowStatus(userId: String, isFollowing: Boolean) {
        _followPosts.update { posts ->
            posts.map {
                if (it.userId == userId) it.copy(isFollowing = isFollowing) else it
            }
        }

        _explorePosts.update { posts ->
            posts.map {
                if (it.userId == userId) it.copy(isFollowing = isFollowing) else it
            }
        }
        _recommendedPosts.update { posts ->
            posts.map {
                if (it.userId == userId) it.copy(isFollowing = isFollowing) else it
            }
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
