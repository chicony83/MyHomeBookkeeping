package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyMovingCountMoney(
    listFullMoneyMoving: List<FullMoneyMoving>
) {
    data class CurrencyBalance(
        val currencyPrefix: String,
        val income: String,
        val spending: String,
        val balance: String
    )

    private var income = 0.0
    private var spending = 0.0
    private var balance = 0.0
    private val balancesByCurrency: List<CurrencyBalance>

    init {
        var inc = 0.0
        var spe = 0.0
        var bal = 0.0
        val currencyTotals = linkedMapOf<String, Totals>()

        for (i in listFullMoneyMoving.indices) {
            val moneyMoving = listFullMoneyMoving[i]
            val amount = moneyMoving.amount
            val currencyPrefix = moneyMoving.currencyIsoValue
                ?.takeIf { it.isNotBlank() }
                ?: moneyMoving.currencyNameValue
            val totals = currencyTotals.getOrPut(currencyPrefix) { Totals() }

            when (moneyMoving.paymentTypeId) {
                PaymentTypeIds.INCOME -> {
                    inc += amount
                    bal += amount
                    totals.income += amount
                    totals.balance += amount
                }

                PaymentTypeIds.SPENDING -> {
                    spe -= amount
                    bal -= amount
                    totals.spending -= amount
                    totals.balance -= amount
                }

                PaymentTypeIds.TRANSFER -> {
                    if (moneyMoving.transferDirection == PaymentTypeIds.TRANSFER_DIRECTION_FROM) {
                        bal -= amount
                        totals.balance -= amount
                    }
                    if (moneyMoving.transferDirection == PaymentTypeIds.TRANSFER_DIRECTION_TO) {
                        bal += amount
                        totals.balance += amount
                    }
                }
            }
        }
        income = roundedNumber(inc)
        spending = roundedNumber(spe)
        balance = roundedNumber(bal)
        balancesByCurrency = currencyTotals.map { (currencyPrefix, totals) ->
            CurrencyBalance(
                currencyPrefix,
                roundedNumber(totals.income).toString(),
                roundedNumber(totals.spending).toString(),
                roundedNumber(totals.balance).toString()
            )
        }
    }

    private class Totals {
        var income = 0.0
        var spending = 0.0
        var balance = 0.0
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

    fun getBalancesByCurrency(): List<CurrencyBalance> {
        return balancesByCurrency
    }
}
