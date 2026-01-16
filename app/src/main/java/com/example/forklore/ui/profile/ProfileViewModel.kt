
package com.example.forklore.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.repository.AuthRepository
import com.example.forklore.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _updateStatus = MutableLiveData<Resource<Unit>>()
    val updateStatus: LiveData<Resource<Unit>> = _updateStatus

    fun getUser() {
        _user.value = authRepository.getCurrentUser()
    }

    fun logout() {
        authRepository.logout()
    }

    fun updateProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = authRepository.updateProfile(name, imageUri)
        }
    }
}
