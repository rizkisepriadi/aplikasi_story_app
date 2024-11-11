package com.dicoding.picodiploma.loginwithanimation.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (text.toString().length < 8) {
            setError("Password tidak boleh kurang dari 8 karakter", null)
        } else {
            error = null
        }
    }
}