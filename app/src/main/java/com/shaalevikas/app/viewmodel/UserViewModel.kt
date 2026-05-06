package com.shaalevikas.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.app.data.model.SchoolProfile
import com.shaalevikas.app.data.model.User
import com.shaalevikas.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repo = UserRepository()

    private val _leaderboard = MutableStateFlow<List<User>>(emptyList())
    val leaderboard: StateFlow<List<User>> = _leaderboard

    private val _schoolProfile = MutableStateFlow<SchoolProfile?>(null)
    val schoolProfile: StateFlow<SchoolProfile?> = _schoolProfile

    private val _isSchoolLoading = MutableStateFlow(true)
    val isSchoolLoading: StateFlow<Boolean> = _isSchoolLoading

    private val _currentUserData = MutableStateFlow<User?>(null)
    val currentUserData: StateFlow<User?> = _currentUserData

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    init {
        loadLeaderboard()
        loadSchoolProfile()
    }

    private fun loadLeaderboard() {
        repo.getLeaderboard()
            .onEach { _leaderboard.value = it }
            .launchIn(viewModelScope)
    }

    fun loadSchoolProfile() {
        viewModelScope.launch {
            _isSchoolLoading.value = true
            _schoolProfile.value = repo.getSchoolProfile()
            _isSchoolLoading.value = false
        }
    }

    fun saveSchoolProfile(profile: SchoolProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            repo.saveSchoolProfile(profile)
                .onSuccess {
                    _schoolProfile.value = profile
                    onSuccess()
                }
                .onFailure { _saveError.value = it.message }
            _isSaving.value = false
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _currentUserData.value = repo.getUserById(userId)
        }
    }

    fun clearSaveError() { _saveError.value = null }
}
