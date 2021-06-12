package com.chico.myhomebookkeeping.ui.moneyMoving

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.constants.Constants


object MoneyMovingCreteQuery {

    private const val argsIncome: String = Constants.FOR_QUERY_INCOME
    private const val argsSpending = Constants.FOR_QUERY_SPENDING
    private const val argsNone = Constants.FOR_QUERY_NONE

    fun createQuery(
        currencyVal: Int,
        categoryVal: Int,
        cashAccountVal: Int,
        incomeSpendingSP: String
    ): SimpleSQLiteQuery {

        var queryString =
            "SELECT time_stamp,amount, " +
                    "account_name AS cash_account_name_value, " +
                    "currency_name AS currency_name_value," +
                    "category_name AS category_name_value, " +
                    "is_income " +
                    "FROM money_moving_table,cash_account_table,currency_table,category_table " +
                    "WHERE cash_account == cashAccountId " +
                    "AND currency == currencyId " +
                    "AND category == categoriesId"
        var argsList: ArrayList<Any> = arrayListOf()

        if (currencyVal > 0) {
            queryString += " AND "
            queryString += " currency = :currency "
            argsList.add(currencyVal)
        }
        if(incomeSpendingSP == argsIncome ){
            queryString += " AND "
            queryString += " is_income = 1 "
        }
        if(incomeSpendingSP == argsSpending ){
            queryString += " AND "
            queryString += " is_income = 0 "
        }
        if (categoryVal > 0) {
            queryString += " AND "
            queryString += " category = :category "
            argsList.add(categoryVal)
        }
        if (cashAccountVal > 0) {
            queryString += " AND "
            queryString += " cash_account = :cash_account "
            argsList.add(cashAccountVal)
        }

        queryString += " ORDER BY id DESC "

        Log.i("TAG","$queryString")
        val args = argsList.toArray()

        return SimpleSQLiteQuery(queryString, args)
    }


}