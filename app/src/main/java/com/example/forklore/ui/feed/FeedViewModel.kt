package com.example.forklore.ui.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.Post
import com.example.forklore.data.model.User
import com.example.forklore.data.repository.AuthRepository
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)
    private val authRepository = AuthRepository()

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private var lastCreatedAt: Long? = null
    private var isLoading = false

    init {
        refresh()
        loadUser()
    }

    private fun loadUser() {
        authRepository.getUser { user ->
            _user.postValue(user)
        }
    }

    fun refresh() {
        lastCreatedAt = null
        loadPage(isRefresh = true)
    }

    fun loadNextPage() {
        loadPage(isRefresh = false)
    }

    fun toggleSaveStatus(postId: String) {
        viewModelScope.launch {
            val isCurrentlySaved = _user.value?.savedPosts?.contains(postId) == true
            val result = if (isCurrentlySaved) {
                postsRepository.unsavePostById(postId)
            } else {
                postsRepository.savePostById(postId)
            }
            if (result is Resource.Success) {
                // The user listener in AuthRepository will handle the update automatically
            }
        }
    }

    private fun loadPage(isRefresh: Boolean) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            if (isRefresh) {
                val cached = postsRepository.getCachedFeedPosts()
                if (cached.isNotEmpty()) {
                    _posts.postValue(Resource.Success(cached))
                } else {
                    _posts.postValue(Resource.Loading())
                }
            }

            val remote = postsRepository.getFeedPostsPage(
                beforeCreatedAt = lastCreatedAt,
                isRefresh = isRefresh
            )

            when (remote) {
                is Resource.Success -> {
                    val newItems = remote.data.orEmpty()
                    if (newItems.isNotEmpty()) {
                        lastCreatedAt = newItems.last().createdAt
                    }
                    val merged = postsRepository.getCachedFeedPosts()
                    _posts.postValue(Resource.Success(merged))
                }
                is Resource.Error -> {
                    _posts.postValue(remote)
                }
                is Resource.Loading -> {
                    _posts.postValue(Resource.Loading())
                }
            }

            isLoading = false
        }
    }
}
