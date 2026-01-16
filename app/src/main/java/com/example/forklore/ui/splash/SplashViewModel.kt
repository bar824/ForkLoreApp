
package com.example.forklore.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forklore.data.repository.AuthRepository

class SplashViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun checkUserStatus() {
        _isLoggedIn.value = authRepository.getCurrentUser() != null
    }
}
