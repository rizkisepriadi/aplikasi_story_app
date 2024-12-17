package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.data.database.StoryRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.Story
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private var apiService: ApiService,
    private val database: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPaging() = Pager(
        config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false
        ),
        remoteMediator = StoryRemoteMediator(database, apiService),
        pagingSourceFactory = {
            database.storyDao().getAllStory()
        }
    ).liveData

    suspend fun getStories(): Result<List<ListStoryItem>> {
        return try {
            val response = apiService.getStories1()
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

    suspend fun getStoriesWithLocation(location: Int): Result<List<ListStoryItem>> {
        return try {
            val response = apiService.getStoriesWithLocation(location)
            if (response.isSuccessful) {
                val storyList =
                    response.body()?.listStory.orEmpty() // Pastikan selalu mengembalikan list kosong jika null
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
        requestBody: RequestBody,
        currentLatitude: Double?,
        currentLongitude: Double?
    ): Result<FileUploadResponse> {
        return try {
            val response = apiService.uploadStory(
                multipartBody, requestBody, currentLatitude, currentLongitude
            )
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

    fun updateApiService(token: String) {
        apiService = ApiConfig.getApiService(token)
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
            apiService: ApiService,
            database: StoryDatabase? = null
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, database!!)
            }.also { instance = it }
    }
}
