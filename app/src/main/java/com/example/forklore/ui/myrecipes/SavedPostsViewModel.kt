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

class SavedPostsViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    init {
        loadSavedPosts()
    }

    fun loadSavedPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            _posts.value = postsRepository.getSavedPosts()
        }
    }

    fun unsavePost(postId: String) {
        viewModelScope.launch {
            postsRepository.unsavePostById(postId)
            loadSavedPosts()
        }
    }
}
