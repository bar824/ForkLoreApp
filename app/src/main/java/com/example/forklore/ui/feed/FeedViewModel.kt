
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

    fun getPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            _posts.value = postsRepository.getPosts()
        }
    }
}
