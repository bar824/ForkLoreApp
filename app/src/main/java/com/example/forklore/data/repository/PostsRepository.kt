package com.example.forklore.data.repository

import android.content.Context
import android.net.Uri
import com.example.forklore.data.ImageCacheManager
import com.example.forklore.data.local.DbProvider
import com.example.forklore.data.model.Post
import com.example.forklore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostsRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val postsDao = DbProvider.getDb(context).postsDao()
    private val imageCacheManager = ImageCacheManager(context, postsDao)

    private val pageSize = 10

    suspend fun getCachedFeedPosts(): List<Post> = withContext(Dispatchers.IO) {
        postsDao.getPosts()
    }

    /**
     * Feed = כל הפוסטים של כולם (דרישת "שיתוף").
     * Paging פשוט לפי createdAt: מביאים פוסטים חדשים יותר קודם,
     * ואם beforeCreatedAt לא null => מביאים פוסטים ישנים יותר.
     */
    suspend fun getFeedPostsPage(
        beforeCreatedAt: Long?,
        isRefresh: Boolean
    ): Resource<List<Post>> {
        return try {
            var query = db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (beforeCreatedAt != null) {
                query = query.whereLessThan("createdAt", beforeCreatedAt)
            }

            val remotePosts = query
                .limit(pageSize.toLong())
                .get()
                .await()
                .toObjects(Post::class.java)

            withContext(Dispatchers.IO) {
                if (isRefresh) {
                    postsDao.deleteAllPosts()
                }
                postsDao.insertPosts(remotePosts)
            }

            // Cache תמונות מקומית (Room שומר localImagePath)
            remotePosts.forEach { post ->
                val url = post.imageUrl
                if (post.localImagePath == null && !url.isNullOrBlank()) {
                    imageCacheManager.cacheImage(post.id, url)
                }
            }

            Resource.Success(remotePosts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load feed posts")
        }
    }

    suspend fun getMyPosts(): Resource<List<Post>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val remotePosts = db.collection("posts")
                .whereEqualTo("ownerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)

            Resource.Success(remotePosts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load my posts")
        }
    }

    fun listenForMyPostsCount(callback: (Resource<Int>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(Resource.Error("User not logged in"))
        db.collection("posts")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(Resource.Error(error.message ?: "Failed to listen for post count"))
                    return@addSnapshotListener
                }
                callback(Resource.Success(snapshot?.size() ?: 0))
            }
    }

    fun listenForSavedPostsCount(callback: (Resource<Int>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(Resource.Error("User not logged in"))
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(Resource.Error(error.message ?: "Failed to listen for saved post count"))
                    return@addSnapshotListener
                }
                val savedPostIds = snapshot?.get("savedPosts") as? List<*> ?: emptyList<Any>()
                callback(Resource.Success(savedPostIds.size))
            }
    }

    suspend fun getSavedPosts(): Resource<List<Post>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val userDoc = db.collection("users").document(userId).get().await()
            val savedPostIds = userDoc.get("savedPosts") as? List<String> ?: emptyList()

            if (savedPostIds.isEmpty()) {
                return Resource.Success(emptyList())
            }

            val savedPosts = db.collection("posts")
                .whereIn("id", savedPostIds)
                .get()
                .await()
                .toObjects(Post::class.java)

            Resource.Success(savedPosts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load saved posts")
        }
    }


    suspend fun searchPosts(query: String): Resource<List<Post>> {
        return try {
            val posts = db.collection("posts")
                .whereArrayContains("searchableKeywords", query.lowercase())
                .get()
                .await()
                .toObjects(Post::class.java)

            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed")
        }
    }

    suspend fun savePostById(postId: String): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val userRef = db.collection("users").document(userId)
            userRef.update("savedPosts", FieldValue.arrayUnion(postId)).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save post")
        }
    }

    suspend fun unsavePostById(postId: String): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val userRef = db.collection("users").document(userId)
            userRef.update("savedPosts", FieldValue.arrayRemove(postId)).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unsave post")
        }
    }

    suspend fun getPost(postId: String): Resource<Post> {
        return try {
            val cached = withContext(Dispatchers.IO) { postsDao.getPostById(postId) }
            if (cached != null) return Resource.Success(cached)

            val snap = db.collection("posts").document(postId).get().await()
            val remote = snap.toObject(Post::class.java)
                ?: return Resource.Error("Post not found")

            val finalPost = if (remote.id.isBlank()) remote.copy(id = postId) else remote

            withContext(Dispatchers.IO) {
                postsDao.insertPost(finalPost)
            }

            finalPost.imageUrl?.let { url ->
                if (finalPost.localImagePath == null) {
                    imageCacheManager.cacheImage(finalPost.id, url)
                }
            }

            Resource.Success(finalPost)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load post")
        }
    }

    private fun generateKeywords(title: String, tags: List<String>): List<String> {
        val keywords = mutableListOf<String>()
        keywords.addAll(title.lowercase().split(" "))
        keywords.addAll(tags.map { it.lowercase() })
        return keywords.distinct()
    }

    suspend fun addPost(post: Post, imageUri: Uri?): Resource<Unit> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("User not logged in")

            var imageUrl: String? = null
            if (imageUri != null) {
                val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val docRef = db.collection("posts").document()

            val keywords = generateKeywords(post.title, post.tags)

            val newPost = post.copy(
                id = docRef.id,
                ownerId = user.uid,
                ownerName = user.displayName ?: "Anonymous",
                ownerPhotoUrl = user.photoUrl?.toString(),
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis(),
                searchableKeywords = keywords
            )

            docRef.set(newPost).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add post")
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

            val keywords = generateKeywords(post.title, post.tags)

            val updatedPost = post.copy(imageUrl = imageUrl, searchableKeywords = keywords)
            db.collection("posts").document(post.id).set(updatedPost).await()

            // גם מעדכנים cache לוקלי
            withContext(Dispatchers.IO) {
                postsDao.insertPost(updatedPost)
            }

            updatedPost.imageUrl?.let { url ->
                if (updatedPost.localImagePath == null) {
                    imageCacheManager.cacheImage(updatedPost.id, url)
                }
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update post")
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
            Resource.Error(e.message ?: "Failed to delete post")
        }
    }
}
