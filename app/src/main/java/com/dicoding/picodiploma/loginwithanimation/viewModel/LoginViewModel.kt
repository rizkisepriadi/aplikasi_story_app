package com.dicoding.picodiploma.loginwithanimation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)

            result.onSuccess { loginResult ->
                saveSession(UserModel(email, loginResult.token))
                _isLoginSuccessful.value = true
                _isLoading.value = false
                clearErrorMessage()
            }.onFailure {
                _isLoginSuccessful.value = false
                _errorMessage.value = it.message
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}