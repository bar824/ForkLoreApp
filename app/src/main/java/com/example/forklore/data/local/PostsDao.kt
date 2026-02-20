package com.example.forklore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forklore.data.model.Post

@Dao
interface PostsDao {

    @Query("SELECT * FROM posts_cache ORDER BY createdAt DESC")
    suspend fun getPosts(): List<Post>

    @Query("SELECT * FROM posts_cache WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("DELETE FROM posts_cache")
    suspend fun deleteAllPosts()

    @Query("UPDATE posts_cache SET localImagePath = :localImagePath WHERE id = :postId")
    suspend fun updateLocalImagePath(postId: String, localImagePath: String)
}
