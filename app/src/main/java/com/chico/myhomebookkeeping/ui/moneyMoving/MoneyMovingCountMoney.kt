package com.chico.myhomebookkeeping.ui.moneyMoving

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import kotlinx.coroutines.Deferred

class MoneyMovingCountMoney(
    private val listFullMoneyMoving: List<FullMoneyMoving>
) {
    var income = 0.0
    var spending = 0.0
    var balance = 0.0

    init {
        for (i in listFullMoneyMoving.indices) {
            val amount = listFullMoneyMoving[i].amount

            if (listFullMoneyMoving[i].isIncome) {
                income += amount
                balance += amount
            }

            if (!listFullMoneyMoving[i].isIncome) {
                spending -= amount
                balance -= amount
            }
        }


    }

    fun getIncome(): String {
        return income.toString()
    }

    fun getSpending(): String {
        return spending.toString()
    }

    fun getBalance(): String {
        return balance.toString()
    }
}
