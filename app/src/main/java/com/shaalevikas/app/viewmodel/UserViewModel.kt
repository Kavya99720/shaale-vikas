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

    private val _currentUserData = MutableStateFlow<User?>(null)
    val currentUserData: StateFlow<User?> = _currentUserData

    init {
        loadLeaderboard()
        loadSchoolProfile()
    }

    private fun loadLeaderboard() {
        repo.getLeaderboard()
            .onEach { _leaderboard.value = it }
            .launchIn(viewModelScope)
    }

    private fun loadSchoolProfile() {
        viewModelScope.launch {
            _schoolProfile.value = repo.getSchoolProfile()
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _currentUserData.value = repo.getUserById(userId)
        }
    }
}
