
package com.example.forklore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forklore.data.model.ExternalRecipe

@Dao
interface ExternalRecipesDao {

    @Query("SELECT * FROM external_recipes_cache WHERE name LIKE '%' || :query || '%' ")
    suspend fun searchRecipes(query: String): List<ExternalRecipe>

    @Query("SELECT * FROM external_recipes_cache WHERE id = :id")
    suspend fun getRecipeById(id: String): ExternalRecipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<ExternalRecipe>)
}
