
package com.example.forklore.data.repository

import android.content.Context
import com.example.forklore.data.local.DbProvider
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.data.remote.ApiProvider
import com.example.forklore.utils.Resource
import java.io.IOException

class ExternalRecipesRepository(context: Context) {

    private val externalRecipesDao = DbProvider.getDb(context).externalRecipesDao()
    private val mealDbApi = ApiProvider.mealDbApi

    suspend fun searchRecipes(query: String): Resource<List<ExternalRecipe>> {
        return try {
            val localRecipes = externalRecipesDao.searchRecipes(query)
            if (localRecipes.isNotEmpty()) {
                return Resource.Success(localRecipes)
            }

            val remoteRecipes = mealDbApi.searchRecipes(query).meals ?: emptyList()
            if (remoteRecipes.isNotEmpty()) {
                externalRecipesDao.insertRecipes(remoteRecipes)
            }
            Resource.Success(remoteRecipes)
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getRecipeDetails(recipeId: String): Resource<ExternalRecipe> {
        return try {
            val localRecipe = externalRecipesDao.getRecipeById(recipeId)
            if (localRecipe != null && localRecipe.instructions != null) {
                return Resource.Success(localRecipe)
            }

            val remoteRecipe = mealDbApi.lookupRecipe(recipeId).meals?.firstOrNull()
            if (remoteRecipe != null) {
                externalRecipesDao.insertRecipes(listOf(remoteRecipe))
                Resource.Success(remoteRecipe)
            } else {
                Resource.Error("Recipe not found")
            }
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
