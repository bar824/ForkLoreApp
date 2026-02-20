package com.example.forklore.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.model.User
import com.example.forklore.data.repository.PostsRepository
import com.example.forklore.utils.Event
import com.example.forklore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- UI State for Edit Screen ---
sealed class EditProfileUiState {
    object Idle : EditProfileUiState()
    object Loading : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

/**
 * This is a SHARED ViewModel for both ProfileFragment and EditProfileFragment.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ProfileViewModel"

    // --- LiveData for Profile Screen ---
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _recipeCount = MutableLiveData<Int>()
    val recipeCount: LiveData<Int> = _recipeCount

    private val _savedRecipeCount = MutableLiveData<Int>()
    val savedRecipeCount: LiveData<Int> = _savedRecipeCount

    // --- LiveData for Edit Profile Screen ---
    private val _editProfileUiState = MutableLiveData<EditProfileUiState>(EditProfileUiState.Idle)
    val editProfileUiState: LiveData<EditProfileUiState> = _editProfileUiState

    private val _navigateToProfile = MutableLiveData<Event<Unit>>()
    val navigateToProfile: LiveData<Event<Unit>> = _navigateToProfile

    private val _isFormChanged = MutableLiveData(false)

    private val _isSaveEnabled = MediatorLiveData<Boolean>()
    val isSaveEnabled: LiveData<Boolean> = _isSaveEnabled

    // --- Firebase Instances ---
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val postsRepository = PostsRepository(application)

    private var originalUser: User? = null
    private var nameHasChanged = false
    private var bioHasChanged = false
    private var imageHasChanged = false

    init {
        loadUserProfile()
        listenForRecipeCountChanges()
        listenForSavedRecipeCountChanges()

        _isSaveEnabled.addSource(_isFormChanged) { updateSaveButtonState() }
        _isSaveEnabled.addSource(_editProfileUiState) { updateSaveButtonState() }
    }

    private fun updateSaveButtonState() {
        val formHasChanges = _isFormChanged.value ?: false
        val isLoading = _editProfileUiState.value is EditProfileUiState.Loading
        _isSaveEnabled.value = formHasChanges && !isLoading
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val freshFirebaseUser = auth.currentUser ?: return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val bio = snapshot.getString("bio") ?: ""
                    val savedPosts = snapshot.get("savedPosts") as? List<String> ?: emptyList()
                    val updatedUser = User(freshFirebaseUser.displayName, bio, freshFirebaseUser.photoUrl?.toString(), freshFirebaseUser.email, savedPosts)
                    originalUser = updatedUser // This line causes the "one change" bug, but it ensures data is fresh.
                    _user.postValue(updatedUser)
                } else {
                     val updatedUser = User(freshFirebaseUser.displayName, "", freshFirebaseUser.photoUrl?.toString(), freshFirebaseUser.email)
                     originalUser = updatedUser
                    _user.postValue(updatedUser)
                }
            }
    }

    private fun listenForRecipeCountChanges() {
        postsRepository.listenForMyPostsCount { resource ->
            if (resource is Resource.Success) {
                _recipeCount.postValue(resource.data!!)
            }
        }
    }

    private fun listenForSavedRecipeCountChanges() {
        postsRepository.listenForSavedPostsCount { resource ->
            if (resource is Resource.Success) {
                _savedRecipeCount.postValue(resource.data!!)
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    // --- LOGIC FOR EDIT PROFILE SCREEN ---
    fun onDisplayNameChanged( newName: String) {
        nameHasChanged = newName != originalUser?.displayName
        updateFormChangedStatus()
    }

    fun onBioChanged(newBio: String) {
        bioHasChanged = newBio != originalUser?.bio
        updateFormChangedStatus()
    }

    fun onImageChanged() {
        imageHasChanged = true
        updateFormChangedStatus()
    }

    private fun updateFormChangedStatus() {
        _isFormChanged.value = nameHasChanged || bioHasChanged || imageHasChanged
    }

    fun saveProfile(displayName: String, bio: String, imageUri: Uri?) {
        if (displayName.isBlank()) {
            _editProfileUiState.value = EditProfileUiState.Error("Display name cannot be empty")
            return
        }

        _editProfileUiState.value = EditProfileUiState.Loading

        viewModelScope.launch {
            try {
                val newPhotoUrl = if (imageUri != null) uploadImage(imageUri) else originalUser?.photoUrl

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(newPhotoUrl?.let { Uri.parse(it) })
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)?.await()

                db.collection("users").document(auth.currentUser!!.uid).set(mapOf("bio" to bio)).await()

                _editProfileUiState.postValue(EditProfileUiState.Idle)
                _navigateToProfile.postValue(Event(Unit))

            } catch (e: Exception) {
                Log.e(TAG, "saveProfile: FAILED.", e)
                _editProfileUiState.postValue(EditProfileUiState.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String {
        val storageRef = storage.reference.child("profile_images/${auth.currentUser!!.uid}")
        val uploadTask = storageRef.putFile(uri).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }
}
