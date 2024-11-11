package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.viewModel.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this) as ViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            mainViewModel.register(name, email, password)
        }
    }

    private fun observeViewModel() {
        mainViewModel.isRegisterSuccessful.observe(this) { isSuccessful ->
            if (isSuccessful) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.errorMessage.observe(this) { error ->
            error?.let {
                showErrorDialog(it  )
                mainViewModel.clearErrorMessage()
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }
}