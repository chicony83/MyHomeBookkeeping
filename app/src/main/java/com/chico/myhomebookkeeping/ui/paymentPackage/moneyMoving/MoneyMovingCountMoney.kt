package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyMovingCountMoney(
    listFullMoneyMoving: List<FullMoneyMoving>
) {
    private var income = 0.0
    private var spending = 0.0
    private var balance = 0.0

    init {
        var inc = 0.0
        var spe = 0.0
        var bal = 0.0

        for (i in listFullMoneyMoving.indices) {
            val amount = listFullMoneyMoving[i].amount

            if (listFullMoneyMoving[i].isIncome) {
                inc += amount
                bal += amount
            }

            if (!listFullMoneyMoving[i].isIncome) {
                spe -= amount
                bal -= amount
            }
        }
        income = roundedNumber(inc)
        spending = roundedNumber(spe)
        balance = roundedNumber(bal)
    }

    private fun roundedNumber(num: Double): Double {
        return BigDecimal(num).setScale(2, RoundingMode.HALF_EVEN).toDouble()
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
