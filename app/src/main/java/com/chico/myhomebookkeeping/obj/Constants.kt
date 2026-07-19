package com.chico.myhomebookkeeping.obj

object Constants {

    const val ARGS_NEW_PAYMENT_DATE_TIME_KEY = "dateTimeForCreate"
    const val ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY = "cashAccountForCreate"
    const val ARGS_NEW_PAYMENT_TRANSFER_CASH_ACCOUNT_KEY = "transferCashAccountForCreate"
    const val ARGS_NEW_PAYMENT_IS_TRANSFER_KEY = "isTransferForCreate"
    const val ARGS_NEW_PAYMENT_CASH_ACCOUNT_SELECT_MODE_KEY = "cashAccountSelectModeForCreate"
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

    const val ARGS_NEW_ENTRY_OF_MONEY_MOVING_IN_DB_IS_ADDED = "newEntryOfMoneyMovingInDbIsAdded"

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
    const val CATEGORIES_TOP_ORDER = "categories_top_order"
    const val SORTING_FAST_PAYMENTS = "sorting_fast_payments"
    const val ARGS_GET_FAST_PAYMENTS_BY_TYPE = "get_fast_payments_by_type"

    const val QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL = "quick_payment_currency_selection_scroll"
    const val QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL = "quick_payment_cash_account_selection_scroll"
    const val QUICK_PAYMENT_SHOW_CALCULATOR = "quick_payment_show_calculator"
    const val QUICK_PAYMENT_AMOUNT_INPUT_MODE = "quick_payment_amount_input_mode"
    const val QUICK_PAYMENT_AMOUNT_INPUT_DIGITS = "digits"
    const val QUICK_PAYMENT_AMOUNT_INPUT_SCROLL = "scroll"
    const val QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS = "quick_payment_amount_whole_digits"
    const val QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS = "quick_payment_amount_fraction_digits"
    const val QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS = 4
    const val QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS = 2

    const val CASH_ACCOUNT_SELECT_MODE_SOURCE = "source"
    const val CASH_ACCOUNT_SELECT_MODE_DESTINATION = "destination"

    const val START_FRAGMENT = "startFragment"
    const val START_FRAGMENT_FAST_PAYMENTS = "fast_payments"
    const val START_FRAGMENT_CATEGORIES = "categories"
    const val START_FRAGMENT_JOURNAL = "journal"

}
