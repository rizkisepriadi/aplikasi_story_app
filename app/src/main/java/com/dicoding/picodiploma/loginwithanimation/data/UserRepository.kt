package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResult
import com.dicoding.picodiploma.loginwithanimation.data.response.Story
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            apiService.register(name, email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val response = apiService.login(email, password)
            if (!response.error) {
                Result.success(response.loginResult)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStories(): Result<List<ListStoryItem>> {
        return try {
            val response = apiService.getStories()
            if (response.isSuccessful) {
                val storyList = response.body()?.listStory ?: emptyList()
                Result.success(storyList)
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailStory(id: String): Result<Story> {
        return try {
            val response = apiService.getDetailStory(id)
            if (response.isSuccessful) {
                val detailResponse = response.body()
                val story = detailResponse?.story
                if (story != null) {
                    Result.success(story)
                } else {
                    Result.failure(Exception("Failed to get Story: Story data is null"))
                }
            } else {
                Result.failure(Exception("Failed to get Story: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody
    ): Result<FileUploadResponse> {
        return try {
            val response = apiService.uploadStory(multipartBody, requestBody)

            if (!response.error) {
                Log.d("UploadStory", "Upload success: ${response.message}")
                Result.success(response)
            } else {
                Log.e("UploadStory", "Upload failed: ${response.message}")
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("UploadStory", "Exception occurred: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

}