
package com.example.forklore.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.data.repository.ExternalRecipesRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val externalRecipesRepository = ExternalRecipesRepository(application)

    private val _recipes = MutableLiveData<Resource<List<ExternalRecipe>>>()
    val recipes: LiveData<Resource<List<ExternalRecipe>>> = _recipes

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipes.value = Resource.Loading()
            _recipes.value = externalRecipesRepository.searchRecipes(query)
        }
    }
}
