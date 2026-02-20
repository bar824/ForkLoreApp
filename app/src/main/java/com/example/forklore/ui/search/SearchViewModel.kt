package com.example.forklore.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.Post
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(application)

    private val _searchResults = MutableLiveData<List<Post>>()
    val searchResults: LiveData<List<Post>> = _searchResults

    fun search(query: String) {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            when (val resource = postsRepository.searchPosts(normalized)) {
                is Resource.Success -> {
                    _searchResults.postValue(resource.data!!)
                }
                is Resource.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }
}
