package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.viewModel.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("storyId")

        if (!eventId.isNullOrEmpty()) {
            mainViewModel.detailStoryEvents(eventId)
        } else {
            Toast.makeText(this, "Invalid story ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        mainViewModel.detailEvent.observe(this) { story ->
            if (story != null) {
                binding.tvItemName.text = story.name
                binding.tvItemDescription.text = story.description
                Glide.with(this).load(story.photoUrl).into(binding.ivItemPhoto)
            } else {
                Toast.makeText(this, "Failed to load story details", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                mainViewModel.clearErrorMessage()
            }
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
