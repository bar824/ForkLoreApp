package com.example.forklore.ui.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    private var currentPage = 1
    private var isFetching = false
    private var hasMorePosts = true

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            val resource = postsRepository.getInitialPosts()
            _posts.value = resource
        }
    }

    fun loadNextPage() {
        if (isFetching || !hasMorePosts) return
        viewModelScope.launch {
            isFetching = true
            val resource = postsRepository.getPosts(currentPage)
            if (resource is Resource.Success) {
                val newPosts = resource.data
                if (newPosts.isNullOrEmpty()) {
                    hasMorePosts = false
                } else {
                    val currentPosts = (_posts.value as? Resource.Success)?.data ?: emptyList()
                    _posts.value = Resource.Success(currentPosts + newPosts)
                    currentPage++
                }
            }
            isFetching = false
        }
    }
}
