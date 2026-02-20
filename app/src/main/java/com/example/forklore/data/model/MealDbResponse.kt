
package com.example.forklore.data.model

import com.squareup.moshi.Json

data class MealDbResponse(
    @Json(name = "meals") val meals: List<ExternalRecipe>?
)
