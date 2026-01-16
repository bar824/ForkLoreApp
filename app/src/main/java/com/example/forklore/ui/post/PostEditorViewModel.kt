
package com.example.forklore.ui.post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class PostEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)

    private val _post = MutableLiveData<Resource<Post>>()
    val post: LiveData<Resource<Post>> = _post

    private val _saveStatus = MutableLiveData<Resource<Unit>>()
    val saveStatus: LiveData<Resource<Unit>> = _saveStatus

    fun getPost(postId: String) {
        viewModelScope.launch {
            _post.value = Resource.Loading()
            _post.value = postsRepository.getPost(postId)
        }
    }

    fun savePost(post: Post, imageUri: Uri?) {
        viewModelScope.launch {
            _saveStatus.value = Resource.Loading()
            val result = if (post.id.isEmpty()) {
                postsRepository.createPost(post, imageUri)
            } else {
                postsRepository.updatePost(post, imageUri)
            }
            _saveStatus.value = result
        }
    }
}
