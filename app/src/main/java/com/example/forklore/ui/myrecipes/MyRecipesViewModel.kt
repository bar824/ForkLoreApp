
package com.example.forklore.ui.myrecipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class MyRecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)

    private val _myPosts = MutableLiveData<Resource<List<Post>>>()
    val myPosts: LiveData<Resource<List<Post>>> = _myPosts

    private val _deleteStatus = MutableLiveData<Resource<Unit>>()
    val deleteStatus: LiveData<Resource<Unit>> = _deleteStatus

    fun getMyPosts() {
        viewModelScope.launch {
            _myPosts.value = Resource.Loading()
            _myPosts.value = postsRepository.getMyPosts()
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            _deleteStatus.value = Resource.Loading()
            val result = postsRepository.deletePost(post)
            if (result is Resource.Success) {
                // Refresh the list after deletion
                getMyPosts()
            }
            _deleteStatus.value = result
        }
    }
}
