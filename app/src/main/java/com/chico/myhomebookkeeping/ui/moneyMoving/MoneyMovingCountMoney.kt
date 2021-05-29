package com.chico.myhomebookkeeping.ui.moneyMoving

import android.util.Log
import com.chico.myhomebookkeeping.db.FullMoneyMoving

object MoneyMovingCountMoney {
    fun count(fullMoneyMoving: List<FullMoneyMoving>): Int {
        var amount = 0
        for (i in fullMoneyMoving.indices) {
            if (fullMoneyMoving[i].isIncome) amount += fullMoneyMoving[i].amount.toInt()
            else amount -= fullMoneyMoving[i].amount.toInt()
        }
        return amount
    }

}
