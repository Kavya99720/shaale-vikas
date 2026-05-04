package com.shaalevikas.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shaalevikas.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(repo.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        if (repo.currentUser != null) loadUserRole()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            val user = repo.getCurrentUserData()
            _userRole.value = user?.role
        }
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repo.login(email, password)
            result.onSuccess {
                _currentUser.value = it
                val userData = repo.getCurrentUserData()
                _userRole.value = userData?.role
                onSuccess(userData?.role ?: "ALUMNI")
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repo.register(name, email, password)
            result.onSuccess {
                _currentUser.value = it
                _userRole.value = "ALUMNI"
                onSuccess()
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repo.sendPasswordReset(email)
            result.onSuccess {
                _successMessage.value = "Password reset email sent! Check your inbox."
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        repo.signOut()
        _currentUser.value = null
        _userRole.value = null
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
