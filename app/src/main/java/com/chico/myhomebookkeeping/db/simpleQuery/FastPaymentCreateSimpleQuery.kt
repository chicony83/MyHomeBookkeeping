package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery

object FastPaymentCreateSimpleQuery {
    fun createQueryList(): SimpleSQLiteQuery {
        val query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()

        val args:Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query,args)
    }

    private fun mainQueryFastPayment(): String {
        return "SELECT id, icon, rating," +
                "cash_account_name AS cash_account_name_value," +
                "currency_name AS currency_name_value" +
                "category_name AS category_name_value" +
                "is_income, amount, description" +
                "FROM money_moving_table, cash_account_table, currency_table, category_table " +
                "WHERE cash_account == cashAccountId " +
                "AND currency == currencyId " +
                "AND category == categoriesId"
    }
}