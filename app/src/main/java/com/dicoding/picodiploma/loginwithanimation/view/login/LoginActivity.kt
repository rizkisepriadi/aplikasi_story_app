package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.viewModel.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeViewModel()
    }

    private fun setupView() {
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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoginSuccessful.observe(this) { isSuccessful ->
            if (isSuccessful) {
                showSuccessDialog()
            }
        }
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                showErrorDialog(it)
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun showSuccessDialog() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
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
