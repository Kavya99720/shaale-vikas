package com.shaalevikas.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shaalevikas.app.data.model.Pledge
import com.shaalevikas.app.data.repository.PledgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PledgeViewModel : ViewModel() {
    private val repo = PledgeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _pledges = MutableStateFlow<List<Pledge>>(emptyList())
    val pledges: StateFlow<List<Pledge>> = _pledges

    private val _userPledges = MutableStateFlow<List<Pledge>>(emptyList())
    val userPledges: StateFlow<List<Pledge>> = _userPledges

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun loadPledgesForNeed(needId: String) {
        repo.getPledgesForNeed(needId)
            .onEach { _pledges.value = it }
            .launchIn(viewModelScope)
    }

    fun loadUserPledges() {
        val uid = auth.currentUser?.uid ?: return
        repo.getPledgesForUser(uid)
            .onEach { _userPledges.value = it }
            .launchIn(viewModelScope)
    }

    fun submitPledge(needId: String, name: String, phone: String, amount: Double) {
        val uid = auth.currentUser?.uid ?: run { _error.value = "You must be logged in."; return }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val pledge = Pledge(needId = needId, userId = uid, name = name, phone = phone, amount = amount)
            repo.submitPledge(pledge)
                .onSuccess { _success.value = true }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _success.value = false }
}
