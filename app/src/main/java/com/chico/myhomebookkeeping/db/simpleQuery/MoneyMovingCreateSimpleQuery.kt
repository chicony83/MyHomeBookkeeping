package com.chico.myhomebookkeeping.db.simpleQuery

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.obj.Constants


object MoneyMovingCreateSimpleQuery {

    private const val argsIncome: String = Constants.ARGS_QUERY_PAYMENT_INCOME
    private const val argsSpending = Constants.ARGS_QUERY_PAYMENT_SPENDING
    private const val argsNone = Constants.FOR_QUERY_NONE

    fun createQueryOneLine(id: Long): SimpleSQLiteQuery {
        var queryString = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Any> = arrayListOf()

        if (id > 0) {
            queryString += addAnd()
            queryString += " money_moving_table.id = :id"
            argsList.add(id)
        }
        val args = argsList.toArray()
        Log.i("TAG", queryString)
        return SimpleSQLiteQuery(queryString, args)
    }

    fun createQueryList(
        currencyVal: Int,
        categoryVal: Int,
        cashAccountVal: Int,
        incomeSpendingSP: String,
        startTimePeriodLongSP: Long,
        endTimePeriodLongSP: Long
    ): SimpleSQLiteQuery {

        var queryString = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Any> = arrayListOf()

        queryString =
            addSelectedTimePeriod(startTimePeriodLongSP, endTimePeriodLongSP, queryString, argsList)
        queryString =
            addNotEndedTimePeriod(startTimePeriodLongSP, endTimePeriodLongSP, queryString, argsList)
        queryString = addCurrency(currencyVal, queryString, argsList)
        queryString = addIsIncomeCategory(incomeSpendingSP, queryString)
        queryString = addIsSpendingCategory(incomeSpendingSP, queryString)
        queryString = addCategory(categoryVal, queryString, argsList)
        queryString = addCashAccount(cashAccountVal, queryString, argsList)

        queryString = addSortingByTimeStampDesc(queryString)

        Log.i("TAG", queryString)
        val args: Array<Any> = argsList.toArray()

        return SimpleSQLiteQuery(queryString, args)
    }

    fun createQueryListForReports(
        currencyVal: Int,
        cashAccountVal: Int,
        incomeSpendingStringSP: String,
        startTimePeriodLongSP: Long,
        endTimePeriodLongSP: Long
    ): SimpleSQLiteQuery {

        var queryString = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Any> = arrayListOf()

        if (
            (currencyVal > 0)
            or (cashAccountVal > 0)
            or (startTimePeriodLongSP > 0)
            or (endTimePeriodLongSP > 0)
            or (incomeSpendingStringSP.isNotEmpty())
        ) {
            queryString =
                addSelectedTimePeriod(
                    startTimePeriodLongSP,
                    endTimePeriodLongSP,
                    queryString,
                    argsList
                )
            queryString =
                addNotEndedTimePeriod(
                    startTimePeriodLongSP,
                    endTimePeriodLongSP,
                    queryString,
                    argsList
                )
            queryString = addCurrency(currencyVal, queryString, argsList)
            queryString = addIsIncomeCategory(incomeSpendingStringSP, queryString)
            queryString = addIsSpendingCategory(incomeSpendingStringSP, queryString)
            queryString = addCashAccount(cashAccountVal, queryString, argsList)
        }

        Log.i("TAG", queryString)

        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(queryString, args)

    }

    private fun addNotEndedTimePeriod(
        startTimePeriodLongSP: Long,
        endTimePeriodLongSP: Long,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if ((startTimePeriodLongSP > 0) xor (endTimePeriodLongSP > 0)) {
            queryString1 = addStartTimePeriod(startTimePeriodLongSP, queryString1, argsList)
            queryString1 = addEndTimePeriod(endTimePeriodLongSP, queryString1, argsList)
        }
        return queryString1
    }

    private fun addSortingByTimeStampDesc(queryString: String): String {
        var queryString1 = queryString
        queryString1 += " ORDER BY time_stamp DESC "
        return queryString1
    }

    private fun addCashAccount(
        cashAccountVal: Int,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if (cashAccountVal > 0) {
            queryString1 += addAnd()
            queryString1 += " cash_account = :cash_account "
            argsList.add(cashAccountVal)
        }
        return queryString1
    }

    private fun addCategory(
        categoryVal: Int,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if (categoryVal > 0) {
            queryString1 += addAnd()
            queryString1 += " category = :category "
            argsList.add(categoryVal)
        }
        return queryString1
    }

    private fun addIsSpendingCategory(
        incomeSpendingSP: String,
        queryString: String
    ): String {
        var queryString1 = queryString
        if (incomeSpendingSP == argsSpending) {
            queryString1 += addAnd()
            queryString1 += " money_moving_table.payment_type_id = 1 "
        }
        return queryString1
    }

    private fun addIsIncomeCategory(
        incomeSpendingSP: String,
        queryString: String
    ): String {
        var queryString1 = queryString
        if (incomeSpendingSP == argsIncome) {
            queryString1 += addAnd()
            queryString1 += " money_moving_table.payment_type_id = 0 "
        }
        return queryString1
    }

    private fun addCurrency(
        currencyVal: Int,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if (currencyVal > 0) {
            queryString1 += addAnd()
            queryString1 += " currency = :currency "
            argsList.add(currencyVal)
        }
        return queryString1
    }

    private fun addEndTimePeriod(
        endTimePeriodLongSP: Long,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if (endTimePeriodLongSP > 0) {
            queryString1 += addAnd()
            queryString1 += "time_stamp < :time_stamp"
            argsList.add(endTimePeriodLongSP)
        }
        return queryString1
    }

    private fun addStartTimePeriod(
        startTimePeriodLongSP: Long,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if (startTimePeriodLongSP > 0) {
            queryString1 += addAnd()
            queryString1 += "time_stamp > :time_stamp"
            argsList.add(startTimePeriodLongSP)
        }
        return queryString1
    }

    private fun addSelectedTimePeriod(
        startTimePeriodLongSP: Long,
        endTimePeriodLongSP: Long,
        queryString: String,
        argsList: ArrayList<Any>
    ): String {
        var queryString1 = queryString
        if ((startTimePeriodLongSP > 0) and (endTimePeriodLongSP > 0)) {
            queryString1 += addAnd()
            queryString1 += "time_stamp BETWEEN :startTime AND :endTime"
            argsList.add(startTimePeriodLongSP)
            argsList.add(endTimePeriodLongSP)
        }
        return queryString1
    }

    private fun mainQueryFullMoneyMoving(): String {
        return "SELECT money_moving_table.id,time_stamp, " +
                "cash_account_name AS cash_account_name_value, " +
                "currency_name AS currency_name_value," +
                "category_name AS category_name_value, " +
                "amount, money_moving_table.payment_type_id = 0 AS is_income, " +
                "money_moving_table.payment_type_id, payment_type_name, " +
                "transfer_group_id, transfer_direction, description " +
                "FROM money_moving_table " +
                "INNER JOIN cash_account_table ON cash_account == cashAccountId " +
                "INNER JOIN currency_table ON currency == currencyId " +
                "INNER JOIN payment_type_table ON money_moving_table.payment_type_id == payment_type_table.id " +
                "LEFT JOIN category_table ON category == categoriesId " +
                "WHERE 1 = 1"
    }

    private fun addMainQueryMoneyMoving(): String {
        return "SELECT id,time_stamp, cash_account, currency, category, payment_type_id, amount, description, transfer_group_id, transfer_direction " +
                "FROM money_moving_table"
    }

    private fun addAnd(): String {
        return " AND "
    }

//    private fun addWhere(): String {
//        return " WHERE "
//    }

}
