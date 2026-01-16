package com.example.forklore.data.repository

import android.content.Context
import com.example.forklore.data.local.DbProvider
import com.example.forklore.data.model.Recipe

class MyRecipesRepository(context: Context) {

    private val myRecipesDao = DbProvider.getDb(context).myRecipesDao()

    suspend fun saveRecipe(recipe: Recipe) {
        myRecipesDao.insertRecipe(recipe)
    }
}