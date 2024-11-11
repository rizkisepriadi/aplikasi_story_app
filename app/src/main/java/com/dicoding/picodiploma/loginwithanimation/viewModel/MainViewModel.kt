package com.dicoding.picodiploma.loginwithanimation.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.Story
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isRegisterSuccessful = MutableLiveData<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean> get() = _isRegisterSuccessful

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyEvent = MutableLiveData<List<ListStoryItem>>()
    val storyEvent: LiveData<List<ListStoryItem>> = _storyEvent

    private val _detailEvent = MutableLiveData<Story?>()
    val detailEvent: LiveData<Story?> = _detailEvent

    private val _isUploadSuccessful = MutableLiveData<Boolean>()
    val isUploadSuccessful: LiveData<Boolean> get() = _isUploadSuccessful

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut


    init {
        listStoryEvents()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.register(name, email, password)

            result.onSuccess {
                _isRegisterSuccessful.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _isRegisterSuccessful.value = false
                _errorMessage.value = it.message
            }
        }
    }

    fun listStoryEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getStories()
            result.onSuccess {
                _storyEvent.value = it
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun detailStoryEvents(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getDetailStory(id)
            result.onSuccess {
                _detailEvent.value = it
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun uploadStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.uploadStory(multipartBody, requestBody)
            result.onSuccess {
                _isUploadSuccessful.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun saveUri(uri: Uri?) {
        viewModelScope.launch {
            _currentImageUri.value = uri
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedOut.value = true
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
