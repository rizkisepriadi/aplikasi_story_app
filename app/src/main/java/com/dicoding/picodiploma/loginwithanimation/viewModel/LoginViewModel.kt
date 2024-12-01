package com.dicoding.picodiploma.loginwithanimation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: AuthRepository, private val context: Context) : ViewModel() {
    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isRegisterSuccessful = MutableLiveData<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean> get() = _isRegisterSuccessful

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { loginResult ->
                saveSessionAndSetLoginState(UserModel(email, loginResult.token))
            }.onFailure {
                _isLoginSuccessful.value = false
                _isLoading.value = false
                _errorMessage.value = parseErrorMessage(it)
            }
        }
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
                _isLoading.value = false
                _errorMessage.value = parseErrorMessage(it)
            }
        }
    }

    private fun saveSessionAndSetLoginState(user: UserModel) {
        viewModelScope.launch {
            try {
                repository.saveSession(user)
                UserRepository.getInstance(
                    UserPreference.getInstance(context.dataStore),
                    ApiConfig.getApiService(user.token)
                ).updateApiService(user.token)

                _isLoginSuccessful.value = true
            } catch (e: Exception) {
                _isLoginSuccessful.value = false
                _errorMessage.value = "Failed to save session: ${e.message}"
            } finally {
                _isLoading.value = false
                clearErrorMessage()
            }
        }
    }

    private fun parseErrorMessage(exception: Throwable): String {
        return if (exception is HttpException) {
            val errorJson = exception.response()?.errorBody()?.string()
            val errorResponse = errorJson?.let { ApiConfig.parseError(it) }
            errorResponse?.message ?: "Unknown error occurred"
        } else {
            exception.message ?: "An error occurred"
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
