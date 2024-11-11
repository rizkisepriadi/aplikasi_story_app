package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import com.dicoding.picodiploma.loginwithanimation.viewModel.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.viewModel.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAdapter()
        observeViewModel()
        setupAction()
        storyAction()
        languageSetup()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.listStoryEvents()
    }

    private fun observeViewModel() {
        mainViewModel.let { model ->
            model.storyEvent.observe(this) { listStory ->
                setStoryEvent(listStory)
                model.clearErrorMessage()
            }

            model.isLoading.observe(this) {
                showLoading(it)
            }

            model.errorMessage.observe(this) { errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    mainViewModel.clearErrorMessage()
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = StoryAdapter { story_id ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                if (story_id != null) {
                    putExtra("storyId", story_id.toString())
                }
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
        }

        binding.rvStories.adapter = adapter
    }

    private fun setupRecyclerView() {
        binding.apply {
            val storyEvent = LinearLayoutManager(this@MainActivity)
            rvStories.layoutManager = storyEvent
            val itemStoryEvent = DividerItemDecoration(this@MainActivity, storyEvent.orientation)
            rvStories.addItemDecoration(itemStoryEvent)
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            mainViewModel.logout()
        }

        mainViewModel.isLoggedOut.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                navigateToLogin()
            }
        }
    }

    private fun languageSetup() {
        binding.settingImageView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun storyAction() {
        binding.makeStory.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()    }

    private fun setStoryEvent(lifeStoryEvent: List<ListStoryItem?>) {
        adapter.submitList(lifeStoryEvent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
