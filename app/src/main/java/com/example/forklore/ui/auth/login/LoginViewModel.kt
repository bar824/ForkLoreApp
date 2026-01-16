package com.example.forklore.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.repository.AuthRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginStatus = MutableLiveData<Resource<Unit>>()
    val loginStatus: LiveData<Resource<Unit>> = _loginStatus

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = Resource.Loading()
            _loginStatus.value = authRepository.login(email, password)
        }
    }
}