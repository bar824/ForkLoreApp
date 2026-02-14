package com.example.forklore.data.model

data class User(
    val displayName: String? = null,
    val bio: String? = null,
    val photoUrl: String? = null,
    val email: String? = null,
    val savedPosts: List<String> = emptyList()
)
