package com.shaalevikas.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.data.repository.NeedsRepository
import com.shaalevikas.app.data.repository.StorageRepository
import com.shaalevikas.app.utils.GeminiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NeedsViewModel : ViewModel() {
    private val needsRepo = NeedsRepository()
    private val storageRepo = StorageRepository()

    private val _activeNeeds = MutableStateFlow<List<Need>>(emptyList())
    val activeNeeds: StateFlow<List<Need>> = _activeNeeds

    private val _fulfilledNeeds = MutableStateFlow<List<Need>>(emptyList())
    val fulfilledNeeds: StateFlow<List<Need>> = _fulfilledNeeds

    private val _selectedNeed = MutableStateFlow<Need?>(null)
    val selectedNeed: StateFlow<Need?> = _selectedNeed

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _aiGeneratedDescription = MutableStateFlow<String?>(null)
    val aiGeneratedDescription: StateFlow<String?> = _aiGeneratedDescription

    private val _aiEstimatedCost = MutableStateFlow<Double?>(null)
    val aiEstimatedCost: StateFlow<Double?> = _aiEstimatedCost

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading

    init {
        observeActiveNeeds()
        observeFulfilledNeeds()
    }

    private fun observeActiveNeeds() {
        needsRepo.getActiveNeeds()
            .onEach { _activeNeeds.value = it }
            .launchIn(viewModelScope)
    }

    private fun observeFulfilledNeeds() {
        needsRepo.getFulfilledNeeds()
            .onEach { _fulfilledNeeds.value = it }
            .launchIn(viewModelScope)
    }

    fun loadNeed(needId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedNeed.value = needsRepo.getNeedById(needId)
            _isLoading.value = false
        }
    }

    fun saveNeed(need: Need, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            var photoUrl = need.photoUrl
            if (imageUri != null) {
                val uploadResult = storageRepo.uploadImage(imageUri, "needs")
                uploadResult.onSuccess { photoUrl = it }
                    .onFailure { _error.value = "Image upload failed: ${it.message}" }
            }
            val needToSave = need.copy(photoUrl = photoUrl)
            val result = if (need.id.isEmpty()) needsRepo.addNeed(needToSave) else needsRepo.updateNeed(needToSave)
            result.onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteNeed(needId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            needsRepo.deleteNeed(needId)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun markFulfilled(needId: String, beforeUri: Uri, afterUri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val beforeResult = storageRepo.uploadImage(beforeUri, "gallery/before")
            val afterResult = storageRepo.uploadImage(afterUri, "gallery/after")
            if (beforeResult.isSuccess && afterResult.isSuccess) {
                needsRepo.markFulfilled(needId, beforeResult.getOrThrow(), afterResult.getOrThrow())
                    .onSuccess { onSuccess() }
                    .onFailure { _error.value = it.message }
            } else {
                _error.value = "Failed to upload photos."
            }
            _isLoading.value = false
        }
    }

    fun generateAiDescription(title: String, category: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiGeneratedDescription.value = GeminiHelper.generateNeedDescription(title, category)
            _isAiLoading.value = false
        }
    }

    fun estimateAiCost(title: String, category: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiEstimatedCost.value = GeminiHelper.estimateCost(title, category)
            _isAiLoading.value = false
        }
    }

    fun clearAiDescription() { _aiGeneratedDescription.value = null }
    fun clearAiCost() { _aiEstimatedCost.value = null }
    fun clearError() { _error.value = null }
}
