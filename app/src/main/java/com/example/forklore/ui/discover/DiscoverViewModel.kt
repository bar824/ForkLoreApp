package com.example.forklore.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.data.repository.ExternalRecipesRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val externalRecipesRepository = ExternalRecipesRepository(application)

    private val _recipes = MutableLiveData<Resource<List<ExternalRecipe>>>()
    val recipes: LiveData<Resource<List<ExternalRecipe>>> = _recipes

    private var searchJob: Job? = null

    init {
        searchRecipes("")
    }

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searchRecipes(query)
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipes.value = Resource.Loading()
            val baseQuery = query.trim().ifBlank { "chicken" }
            val result = externalRecipesRepository.searchRecipes(baseQuery)
            _recipes.value = when (result) {
                is Resource.Success -> Resource.Success(filterByNameAndTags(result.data.orEmpty(), query))
                is Resource.Error -> result
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    private fun filterByNameAndTags(recipes: List<ExternalRecipe>, query: String): List<ExternalRecipe> {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return recipes

        return recipes.filter { recipe ->
            recipe.name.lowercase().contains(normalized) || recipe.ingredientsText().contains(normalized)
        }
    }

    private fun ExternalRecipe.ingredientsText(): String {
        return listOf(
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
            ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
            ingredient11, ingredient12, ingredient13, ingredient14, ingredient15,
            ingredient16, ingredient17, ingredient18, ingredient19, ingredient20
        ).filterNotNull().joinToString(" ").lowercase()
    }
}
