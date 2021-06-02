package com.chico.myhomebookkeeping.helpers

import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class ControlHelper(private val findNavController: NavController) {
    private val previousFragment = findNavController.previousBackStackEntry?.destination?.id

    fun moveToPreviousPage() {
        findNavController.previousBackStackEntry?.destination?.let {
            findNavController.navigate(
                it.id
            )
        }
    }

    fun isPreviousFragment(fragment: Int): Boolean {
        return previousFragment == fragment
    }
    fun previousFragment(): Int? {
        return previousFragment
    }
}