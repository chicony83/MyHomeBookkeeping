package com.chico.myhomebookkeeping.ui.moneyMoving

import androidx.sqlite.db.SimpleSQLiteQuery


object MoneyMovingCreteQuery {
    fun createQuery(currencyVal: Int, categoryVal: Int, cashAccountVal: Int): SimpleSQLiteQuery {

        var queryString =
            "SELECT time_stamp,amount, " +
                    "account_name AS cash_account_name_value, " +
                    "currency_name AS currency_name_value," +
                    "category_name AS category_name_value, " +
                    "is_income " +
                    "FROM money_moving_table,cash_account_table,currency_table,category_table " +
                    "WHERE cash_account == cashAccountId " +
                    "AND currency == currencyId " +
                    "AND category== categoriesId"
        var argsList: ArrayList<Any> = arrayListOf()

        if (currencyVal > 0) {
            queryString += " AND "
            queryString += " currency = :currency "
            argsList.add(currencyVal)
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

        val args = argsList.toArray()

        return SimpleSQLiteQuery(queryString, args)
    }


}