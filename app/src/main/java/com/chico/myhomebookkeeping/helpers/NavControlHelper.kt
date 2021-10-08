package com.chico.myhomebookkeeping.helpers

import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class NavControlHelper(private val controller: NavController) {
    private val previousFragment = controller.previousBackStackEntry?.destination?.id

    fun moveToPreviousPage() {
        controller.previousBackStackEntry?.destination?.let {
            controller.navigate(
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
    fun moveToMoneyMovingFragment(){
        controller.navigate(R.id.nav_money_moving)
    }
    fun moveToSelectedFragment(toFragment: Int) {
        controller.navigate(toFragment)
    }

    fun moveToSelectTimePeriod() {
        controller.navigate(R.id.nav_time_period)
    }
}