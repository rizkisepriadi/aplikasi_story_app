package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.AuthApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResult
import com.google.gson.Gson
import retrofit2.HttpException

data class ErrorResponse(
    val error: Boolean? = null,
    val message: String? = null
)

class AuthRepository(
    private val userPreference: UserPreference,
    private val apiService: AuthApiService
) {
    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val response = apiService.register(name, email, password)
            Result.success("Registration successful: ${response.message}")
        } catch (e: HttpException) {
            val errorMessage = parseError(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val response = apiService.login(email, password)
            if (!response.error) {
                val loginResult = response.loginResult
                val userModel = UserModel(email, loginResult.token, true)
                saveSession(userModel)

                UserRepository.getInstance(userPreference, ApiConfig.getApiService(loginResult.token))
                    .updateApiService(loginResult.token)

                Result.success(loginResult)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: HttpException) {
            val errorMessage = parseError(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(e: HttpException): String {
        return try {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            errorBody.message ?: "Unknown error"
        } catch (jsonException: Exception) {
            "Error parsing error message"
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: AuthApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
