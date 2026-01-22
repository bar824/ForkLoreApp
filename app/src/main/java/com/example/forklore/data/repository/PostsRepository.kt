package com.example.forklore.data.repository

import android.content.Context
import android.net.Uri
import com.example.forklore.data.local.DbProvider
import com.example.forklore.data.mockPosts
import com.example.forklore.data.model.Post
import com.example.forklore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostsRepository(context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val postsDao = DbProvider.getDb(context).postsDao()
    private val pageSize = 6 // Increased page size

    suspend fun getInitialPosts(): Resource<List<Post>> {
        return Resource.Success(mockPosts.take(pageSize))
    }

    suspend fun getPosts(page: Int): Resource<List<Post>> {
        val offset = page * pageSize
        val newPosts = mockPosts.drop(offset).take(pageSize)
        return Resource.Success(newPosts)
    }

    suspend fun getMyPosts(): Resource<List<Post>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val remotePosts = db.collection("posts")
                .whereEqualTo("ownerId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
            Resource.Success(remotePosts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getPost(postId: String): Resource<Post> {
        // Find the post in the mock list or fall back to an error
        val post = mockPosts.find { it.id == postId }
        return if (post != null) {
            Resource.Success(post)
        } else {
            Resource.Error("Post not found")
        }
    }

    suspend fun createPost(post: Post, imageUri: Uri?): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            var imageUrl: String? = null
            if (imageUri != null) {
                val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val newPost = post.copy(
                ownerId = userId,
                ownerName = auth.currentUser?.displayName ?: "Anonymous",
                ownerPhotoUrl = auth.currentUser?.photoUrl?.toString(),
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis()
            )

            val docRef = db.collection("posts").document()
            val finalPost = newPost.copy(id = docRef.id)

            docRef.set(finalPost).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun updatePost(post: Post, imageUri: Uri?): Resource<Unit> {
        return try {
            var imageUrl = post.imageUrl
            if (imageUri != null) {
                val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val updatedPost = post.copy(imageUrl = imageUrl)
            db.collection("posts").document(post.id).set(updatedPost).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun deletePost(post: Post): Resource<Unit> {
        return try {
            db.collection("posts").document(post.id).delete().await()
            post.imageUrl?.let {
                val imageRef = storage.getReferenceFromUrl(it)
                imageRef.delete().await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}