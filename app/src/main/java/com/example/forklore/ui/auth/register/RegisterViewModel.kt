package com.example.forklore.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forklore.data.repository.AuthRepository
import com.example.forklore.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _registerStatus = MutableLiveData<Resource<Unit>>()
    val registerStatus: LiveData<Resource<Unit>> = _registerStatus

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerStatus.value = Resource.Loading()
            _registerStatus.value = authRepository.register(name, email, password)
        }
    }
}