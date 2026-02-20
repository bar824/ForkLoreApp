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
import com.google.firebase.auth.FirebaseAuth
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
            if (isCurrentlySaved) {
                postsRepository.unsavePostById(postId)
            } else {
                postsRepository.savePostById(postId)
            }
        }
    }

    fun toggleLike(postId: String) {
        val currentPosts = (_posts.value as? Resource.Success)?.data.orEmpty()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userName = _user.value?.displayName?.takeIf { it.isNotBlank() } ?: "Someone"
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val originalPost = currentPosts[postIndex]
        val alreadyLiked = originalPost.likedBy.containsKey(currentUserId)
        val updatedLikedBy = originalPost.likedBy.toMutableMap().apply {
            if (alreadyLiked) remove(currentUserId) else put(currentUserId, userName)
        }
        val updatedPost = originalPost.copy(
            likedBy = updatedLikedBy,
            likesCount = if (alreadyLiked) {
                (originalPost.likesCount - 1).coerceAtLeast(0)
            } else {
                originalPost.likesCount + 1
            }
        )

        val updatedList = currentPosts.toMutableList().apply { set(postIndex, updatedPost) }
        _posts.value = Resource.Success(updatedList)

        viewModelScope.launch {
            val result = postsRepository.toggleLike(postId)
            if (result is Resource.Error) {
                val revertedList = updatedList.toMutableList().apply { set(postIndex, originalPost) }
                _posts.postValue(Resource.Success(revertedList))
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
                is Resource.Error -> _posts.postValue(remote)
                is Resource.Loading -> _posts.postValue(Resource.Loading())
            }

            isLoading = false
        }
    }
}
