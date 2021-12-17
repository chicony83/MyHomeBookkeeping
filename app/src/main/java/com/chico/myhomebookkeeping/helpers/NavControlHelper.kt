package com.chico.myhomebookkeeping.helpers

import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class NavControlHelper(private val controller: NavController) {
    private val previousFragment = controller.previousBackStackEntry?.destination?.id

    private val currentFragment: Int? = controller.currentDestination?.id

    fun isCurrentFragment(fragment: Int): Boolean {
        return controller.currentDestination?.id == fragment
    }

    fun currentFragment(): Int? {
        return currentFragment
    }

    fun moveToPreviousFragment() {
        controller.previousBackStackEntry?.destination?.let {
            controller.navigate(
                it.id
            )

        }
    }

    fun isPreviousFragmentJournal(): Boolean {
        return isPreviousFragment(R.layout.fragment_money_moving)
    }

    fun isPreviousFragment(fragment: Int): Boolean {
        return previousFragment == fragment
    }

    fun previousFragment(): Int? {
        return previousFragment
    }

    fun moveToMoneyMovingFragment() {
        controller.navigate(R.id.nav_money_moving)
    }

    fun toSelectedFragment(toFragment: Int) {
        controller.navigate(toFragment)
    }

    fun moveToSelectTimePeriod() {
        controller.navigate(R.id.nav_time_period)
    }
}