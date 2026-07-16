package com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving

data class QuickPaymentSettings(
    val isCurrencyScrollEnabled: Boolean,
    val isCashAccountScrollEnabled: Boolean,
    val isCalculatorButtonVisible: Boolean,
    val amountInputMode: String,
    val amountWholeDigits: Int,
    val amountFractionDigits: Int
)
