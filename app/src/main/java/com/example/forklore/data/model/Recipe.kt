package com.example.forklore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_recipes")
data class Recipe(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String
)