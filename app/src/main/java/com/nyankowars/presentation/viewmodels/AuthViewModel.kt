package com.nyankowars.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyankowars.domain.models.Resource
import com.nyankowars.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val loginState: StateFlow<Resource<Unit>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val registerState: StateFlow<Resource<Unit>> = _registerState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = authRepository.login(email, password)
            _loginState.value = result
            if (result is Resource.Success) {
                _isLoggedIn.value = true
            }
        }
    }
    
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val result = authRepository.register(username, email, password)
            _registerState.value = result
            if (result is Resource.Success) {
                _isLoggedIn.value = true
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }
    
    fun clearStates() {
        _loginState.value = Resource.Empty
        _registerState.value = Resource.Empty
    }
}