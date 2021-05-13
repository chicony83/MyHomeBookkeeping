package com.chico.myhomebookkeeping.ui

import android.os.Bundle
import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class ControlHelper(private val findNavController: NavController) {

    fun checkAndMove(
        bundle: Bundle,
        argsNameForSelect: String,
        argsNameForQuery: String,
        selectedCurrencyId: Int
    ) {
        when (findNavController.previousBackStackEntry?.destination?.id) {
            R.id.nav_new_money_moving -> {
                bundle.putInt(argsNameForSelect, selectedCurrencyId)
                moveTo(bundle)
            }
            R.id.nav_money_moving_query -> {
                bundle.putInt(argsNameForQuery, selectedCurrencyId)
                moveTo(bundle)
            }
        }
    }

    private fun moveTo(bundle: Bundle) {
        findNavController.previousBackStackEntry?.destination?.let {
            findNavController.navigate(
                it.id, bundle
            )
        }
    }
}