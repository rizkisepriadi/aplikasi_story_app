package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import com.dicoding.picodiploma.loginwithanimation.getImageUri
import com.dicoding.picodiploma.loginwithanimation.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.viewModel.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.viewModel.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                Log.d("Image URI", "showImage: $uri")
                binding.rvImage.setImageURI(uri)
            }
        }

        binding.let {
            it.buttonGallery.setOnClickListener{
                startGallery()
            }
            it.buttonCamera.setOnClickListener{
                startCamera()
            }
            it.buttonAdd.setOnClickListener{
                uploadImage()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mainViewModel.saveUri(uri)
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun showImage() {
        mainViewModel.currentImageUri.observe(this) {
            binding.rvImage.setImageURI(it)
        }
    }

    private fun startCamera() {
        mainViewModel.saveUri(getImageUri(this))
        val uri = mainViewModel.currentImageUri.value
        if (uri != null) {
            launcherIntentCamera.launch(uri)
        } else {
            showToast("Failed to create URI for camera")
        }
    }

    private fun uploadImage() {
        val uri = mainViewModel.currentImageUri.value
        if (uri != null) {
            val imageFile = uriToFile(uri, this)
            val description = binding.edAddDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            mainViewModel.uploadStory(multipartBody, requestBody)

            // Observasi apakah upload berhasil, lalu pindah ke MainActivity
            mainViewModel.isUploadSuccessful.observe(this) { isSuccessful ->
                if (isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        } else {
            showToast(getString(R.string.empty_image_warning))
        }
    }



    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            mainViewModel.saveUri(null)
        }
    }

    private fun showLoading(isLoading: Boolean) {
//        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

