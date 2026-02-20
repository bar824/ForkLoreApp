
package com.example.forklore.ui.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.ImageCacheManager
import com.example.forklore.data.local.AppDatabase
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostLikeState
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class PostDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)
    private val imageCacheManager = ImageCacheManager(application, AppDatabase.getDatabase(application).postsDao())

    private val _post = MutableLiveData<Resource<Post>>()
    val post: LiveData<Resource<Post>> = _post

    private val _likeState = MutableLiveData<Resource<PostLikeState>>()
    val likeState: LiveData<Resource<PostLikeState>> = _likeState

    private val _likeActionStatus = MutableLiveData<Resource<Unit>>()
    val likeActionStatus: LiveData<Resource<Unit>> = _likeActionStatus

    private var likesListener: ListenerRegistration? = null

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

    fun observeLikes(postId: String) {
        likesListener?.remove()
        likesListener = postsRepository.listenToPostLikes(postId) { resource ->
            _likeState.postValue(resource)
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            _likeActionStatus.value = Resource.Loading()
            when (val result = postsRepository.toggleLike(postId)) {
                is Resource.Success -> _likeActionStatus.value = Resource.Success(Unit)
                is Resource.Error -> _likeActionStatus.value = Resource.Error(result.message ?: "Failed to update like")
                is Resource.Loading -> _likeActionStatus.value = Resource.Loading()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        likesListener?.remove()
    }
}
