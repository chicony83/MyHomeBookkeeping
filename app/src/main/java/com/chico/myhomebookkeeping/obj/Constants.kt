package com.chico.myhomebookkeeping.obj

object Constants {

    const val ARGS_NEW_PAYMENT_DATE_TIME_KEY = "dateTimeForCreate"
    const val ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY = "cashAccountForCreate"
    const val ARGS_NEW_PAYMENT_CURRENCY_KEY = "currencyForCreate"
    const val ARGS_NEW_PAYMENT_CATEGORY_KEY = "categoryForCreate"
    const val ARGS_NEW_PAYMENT_AMOUNT_KEY = "amountForCreate"
    const val ARGS_NEW_PAYMENT_DESCRIPTION_KEY = "descriptionForCreate"

    const val ARGS_CHANGE_PAYMENT_ID = "id_money_moving_for_change"
    const val ARGS_CHANGE_PAYMENT_DATE_TIME_KEY = "dateTimeForChange"
    const val ARGS_CHANGE_PAYMENT_CASH_ACCOUNT_KEY = "cashAccountForChange"
    const val ARGS_CHANGE_PAYMENT_CURRENCY_KEY = "currencyForChange"
    const val ARGS_CHANGE_PAYMENT_CATEGORY_KEY = "categoryForChange"
    const val ARGS_CHANGE_PAYMENT_AMOUNT_KEY = "amountForChange"
    const val ARGS_CHANGE_PAYMENT_DESCRIPTION_KEY = "descriptionForChange"

    const val ARGS_QUERY_PAYMENT_START_TIME_PERIOD = "queryStartTimePeriod"
    const val ARGS_QUERY_PAYMENT_END_TIME_PERIOD = "queryEndTimePeriod"
    const val ARGS_QUERY_PAYMENT_CASH_ACCOUNT_KEY = "queryCashAccount"
    const val ARGS_QUERY_PAYMENT_CURRENCY_KEY = "queryCurrency"
    const val ARGS_QUERY_PAYMENT_CATEGORY_KEY = "queryCategory"
    const val ARGS_QUERY_PAYMENT_CATEGORIES_INCOME_SPENDING_KEY = "queryCategoryIncomeSpending"
    const val ARGS_QUERY_PAYMENT_INCOME = "income"
    const val ARGS_QUERY_PAYMENT_SPENDING = "spending"

    const val ARGS_REPORTS_START_TIME_PERIOD = "startTimePeriodForReports"
    const val ARGS_REPORTS_END_TIME_PERIOD = "endTimePeriodForReports"
    const val ARGS_REPORTS_CASH_ACCOUNT_KEY = "cashAccountForReports"
    const val ARGS_REPORTS_CURRENCY_KEY = "currencyForReports"
    const val ARGS_REPORTS_CATEGORY_KEY = "categoryForReports"
    const val ARGS_REPORTS_CATEGORIES_INCOME_SPENDING_KEY = "categoryIncomeSpendingForReports"
    const val ARGS_REPORTS_INCOME = "incomeForReports"
    const val ARGS_REPORTS_SPENDING = "spendingForReports"
    const val ARGS_REPORTS_AMOUNT_KEY = "amountForReports"

    const val ARGS_NEW_FAST_PAYMENTS_NAME = "nameNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_RATING = "ratingNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_CASH_ACCOUNT = "cashAccountNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_CURRENCY = "currencyNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_CATEGORY = "categoryNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_AMOUNT = "amountNewFastPayment"
    const val ARGS_NEW_FAST_PAYMENT_DESCRIPTION = "descriptionNewFastPayment"

    const val ARGS_CHANGE_FAST_PAYMENT_ID = "idFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_NAME = "nameFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_RATING = "ratingFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_CASH_ACCOUNT = "cashAccountFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_CURRENCY = "currencyFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_CATEGORY = "categoryFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_AMOUNT = "amountFastPaymentForChange"
    const val ARGS_CHANGE_FAST_PAYMENT_DESCRIPTION = "descriptionFastPaymentForChange"

    const val FOR_REPORTS_SELECTED_CATEGORIES_LIST_KEY = "reportsSelectedCategoriesList"

    const val FOR_QUERY_NONE = ""
    const val IS_FIRST_LAUNCH = "isFirstLaunch"
    const val IS_FIRST_LAUNCH_FAST_PAYMENTS_ADD_FREE_FAST_PAYMENTS = "isFirstLaunchFastPaymentAddFreeFastPayment"

    const val IS_NIGHT_MODE_ON = "isNightModeOn"
    const val SP_NAME = "SPNewMoneyMoving"

    const val MIN_LENGTH_NAME = 2

    const val SP_PASS = "pass"
    const val SP_PASS_PROMPT = "pass_prompt"

    const val TEXT_EMPTY = ""
    const val TEXT_NONE = ""
    const val MINUS_ONE_VAL_INT: Int = -1
    const val MINUS_ONE_VAL_LONG: Long = -1L

    const val LOCAL_ASSETS_URL = "file:///android_asset/"

    const val REPORT_TYPE = "report_type"

    const val SORTING_CATEGORIES = "sorting_categories"
    const val SORTING_FAST_PAYMENTS = "sorting_fast_payments"

    const val WHATS_NEW_IN_LAST_VERSION = "whats_new_in_last_version"
}