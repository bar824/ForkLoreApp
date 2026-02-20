
package com.example.forklore.ui.post

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
import kotlinx.coroutines.launch

class PostDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)
    private val imageCacheManager = ImageCacheManager(application, AppDatabase.getDatabase(application).postsDao())

    private val _post = MutableLiveData<Resource<Post>>()
    val post: LiveData<Resource<Post>> = _post

    fun getPost(postId: String) {
        viewModelScope.launch {
            _post.value = Resource.Loading()
            val resource = postsRepository.getPost(postId)
            if (resource is Resource.Success) {
                resource.data?.let {
                    if (it.localImagePath == null && it.imageUrl != null) {
                        imageCacheManager.cacheImage(it.id, it.imageUrl)
                    }
                }
            }
            _post.value = resource
        }
    }
}
