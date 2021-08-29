package com.chico.myhomebookkeeping.ui.moneyMoving

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.constants.Constants


object MoneyMovingCreteQuery {

    private const val argsIncome: String = Constants.FOR_QUERY_INCOME
    private const val argsSpending = Constants.FOR_QUERY_SPENDING
    private const val argsNone = Constants.FOR_QUERY_NONE

    fun createQueryOneLine(id: Long): SimpleSQLiteQuery {
        var queryString = ""
        var argsList: ArrayList<Any> = arrayListOf()
        queryString += addMainQuery()

        if (id > 0) {
            queryString += addAnd()
            queryString += " id = :id"
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

        var queryString = ""
        queryString += addMainQuery()
        var argsList: ArrayList<Any> = arrayListOf()

        if ((startTimePeriodLongSP > 0) and (endTimePeriodLongSP > 0)) {
            queryString += addAnd()
            queryString += "time_stamp BETWEEN :startTime AND :endTime"
            argsList.add(startTimePeriodLongSP)
            argsList.add(endTimePeriodLongSP)
        }
        if ((startTimePeriodLongSP > 0) xor (endTimePeriodLongSP > 0)) {
            if (startTimePeriodLongSP > 0) {
                queryString += addAnd()
                queryString += "time_stamp > :time_stamp"
                argsList.add(startTimePeriodLongSP)
            }
            if (endTimePeriodLongSP > 0) {
                queryString += addAnd()
                queryString += "time_stamp < :time_stamp"
                argsList.add(endTimePeriodLongSP)
            }
        }
        if (currencyVal > 0) {
            queryString += addAnd()
            queryString += " currency = :currency "
            argsList.add(currencyVal)
        }
        if (incomeSpendingSP == argsIncome) {
            queryString += addAnd()
            queryString += " is_income = 1 "
        }
        if (incomeSpendingSP == argsSpending) {
            queryString += addAnd()
            queryString += " is_income = 0 "
        }
        if (categoryVal > 0) {
            queryString += addAnd()
            queryString += " category = :category "
            argsList.add(categoryVal)
        }
        if (cashAccountVal > 0) {
            queryString += addAnd()
            queryString += " cash_account = :cash_account "
            argsList.add(cashAccountVal)
        }

        queryString += " ORDER BY time_stamp DESC "

        Log.i("TAG", "$queryString")
        val args = argsList.toArray()

        return SimpleSQLiteQuery(queryString, args)
    }

    private fun addMainQuery(): String {
        return "SELECT id,time_stamp,amount, " +
                "cash_account_name AS cash_account_name_value, " +
                "currency_name AS currency_name_value," +
                "category_name AS category_name_value, " +
                "is_income, description " +
                "FROM money_moving_table,cash_account_table,currency_table,category_table " +
                "WHERE cash_account == cashAccountId " +
                "AND currency == currencyId " +
                "AND category == categoriesId"
    }

    private fun addAnd(): Any {
        return " AND "
    }


}