package me.naloaty.shoppinglist.presentation

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("showingError", "errorText")
fun bindErrorText(til: TextInputLayout, showingError: Boolean, errorText: String) {
    if (showingError)
        til.error = errorText
    else
        til.error = null
}