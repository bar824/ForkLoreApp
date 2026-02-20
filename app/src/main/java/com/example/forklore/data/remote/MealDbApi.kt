
package com.example.forklore.data.remote

import com.example.forklore.data.model.MealDbResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {

    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipes(@Query("s") query: String): MealDbResponse

    @GET("api/json/v1/1/lookup.php")
    suspend fun lookupRecipe(@Query("i") id: String): MealDbResponse
}
