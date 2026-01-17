
package com.example.forklore.ui.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.ImageCacheManager
import com.example.forklore.data.local.AppDatabase
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)
    private val imageCacheManager = ImageCacheManager(application, AppDatabase.getDatabase(application).postsDao())

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    private var lastVisible: DocumentSnapshot? = null
    private var isFetching = false

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            val resource = postsRepository.getInitialPosts()
            handleResource(resource)
        }
    }

    fun loadNextPage() {
        if (isFetching) return
        viewModelScope.launch {
            isFetching = true
            val resource = postsRepository.getPosts(lastVisible)
            handleResource(resource)
            isFetching = false
        }
    }

    private fun handleResource(resource: Resource<List<Post>>) {
        if (resource is Resource.Success) {
            lastVisible = resource.lastVisible
            resource.data?.forEach { post ->
                if (post.localImagePath == null && post.imageUrl != null) {
                    viewModelScope.launch {
                        imageCacheManager.cacheImage(post.id, post.imageUrl)
                    }
                }
            }
        }
        _posts.value = resource
    }
}
