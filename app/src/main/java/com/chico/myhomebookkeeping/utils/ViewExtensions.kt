package com.chico.myhomebookkeeping.utils


import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("endIconClickListener")
fun TextInputLayout.endIconClickListener(callback: () -> Unit) {
    setEndIconOnClickListener {
        callback()
        editText?.text?.clear()
    }
}


