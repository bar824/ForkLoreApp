
package com.example.forklore.data.repository

import android.net.Uri
import com.example.forklore.data.model.User
import com.example.forklore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun register(name: String, email: String, password: String): Resource<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = hashMapOf(
                "uid" to authResult.user!!.uid,
                "name" to name,
                "photoUrl" to ""
            )
            db.collection("users").document(authResult.user!!.uid).set(user).await()

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)?.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getUser(callback: (User?) -> Unit) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            callback(null)
            return
        }

        db.collection("users").document(firebaseUser.uid)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                callback(user)
            }
    }

    suspend fun updateProfile(name: String, imageUri: Uri?): Resource<Unit> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("User not logged in")
            var photoUrl: String? = user.photoUrl?.toString()

            if (imageUri != null) {
                val imageRef = storage.reference.child("avatars/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                photoUrl = imageRef.downloadUrl.await().toString()
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl?.let { Uri.parse(it) })
                .build()

            user.updateProfile(profileUpdates).await()

            db.collection("users").document(user.uid).update(
                mapOf(
                    "name" to name,
                    "photoUrl" to photoUrl
                )
            ).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
