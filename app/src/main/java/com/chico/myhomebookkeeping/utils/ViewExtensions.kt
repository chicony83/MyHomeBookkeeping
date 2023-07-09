package com.chico.myhomebookkeeping.utils


import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.content_main.*


@BindingAdapter("endIconClickListener")
fun TextInputLayout.endIconClickListener(callback: () -> Unit) {
    setEndIconOnClickListener {
        callback()
        editText?.text?.clear()
    }
}

fun Fragment.hideBottomNavigation(){
    requireActivity().bottom_navigation.visibility = View.INVISIBLE
}


