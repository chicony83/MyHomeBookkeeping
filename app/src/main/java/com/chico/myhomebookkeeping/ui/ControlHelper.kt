package com.chico.myhomebookkeeping.ui

import android.os.Bundle
import android.widget.Button
import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class ControlHelper(private val findNavController: NavController) {
    private val previousFragment = findNavController.previousBackStackEntry?.destination?.id
    private val navNewMoneyMoving = R.id.nav_new_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query

    private val uiHelper = UiHelper()

    fun checkAndMove(
        bundle: Bundle,
        argsNameForSelect: String,
        argsNameForQuery: String,
        selectedCurrencyId: Int
    ) {
        when (previousFragment) {
            navNewMoneyMoving -> {
                bundle.putInt(argsNameForSelect, selectedCurrencyId)
                moveTo(bundle)
            }
            navMoneyMovingQuery -> {
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

    fun isPreviousFragment(showHideButton: Button) {
        if (previousFragment == navMoneyMovingQuery) uiHelper.hideUiElement(showHideButton)
    }
}