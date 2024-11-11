package com.dicoding.picodiploma.loginwithanimation.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EmailInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak digunakan
            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                lengthBefore: Int,
                lengthAfter: Int
            ) {
                val isValid = isValidEmail(text.toString())
                error = if (!isValid) "Email tidak valid" else null
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak digunakan
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }
}