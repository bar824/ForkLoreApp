package com.example.forklore.ui.external

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.data.repository.ExternalRecipesRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class ExternalDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val externalRecipesRepository = ExternalRecipesRepository(application)

    private val _recipe = MutableLiveData<Resource<ExternalRecipe>>()
    val recipe: LiveData<Resource<ExternalRecipe>> = _recipe

    fun getRecipeDetails(recipeId: String) {
        viewModelScope.launch {
            _recipe.value = Resource.Loading()
            _recipe.value = externalRecipesRepository.getRecipeDetails(recipeId)
        }
    }

}
