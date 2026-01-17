package com.example.forklore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts_cache")
data class Post(
    @PrimaryKey val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhotoUrl: String? = null,
    val title: String = "",
    val story: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val imageUrl: String? = null,
    var localImagePath: String? = null,
    val tags: List<String> = emptyList(),
    val likesCount: Long = 0,
    val commentsCount: Long = 0,
    val createdAt: Long = 0L
)
