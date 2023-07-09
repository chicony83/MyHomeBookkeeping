package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.enums.StateRecyclerFastPaymentByType
import com.chico.myhomebookkeeping.helpers.Message

object FastPaymentCreateSimpleQuery {
    fun createQueryList(): SimpleSQLiteQuery {
        val query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()

        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingAlphabetByAsc(): SimpleSQLiteQuery {
        var query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY name_fast_payment ASC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingAlphabetByDesc(): SimpleSQLiteQuery {
        var query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY name_fast_payment DESC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingRatingByAsc(): SimpleSQLiteQuery {
        var query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY rating ASC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingRatingByDesc(): SimpleSQLiteQuery {
        var query = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY rating DESC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }
    private fun mainQueryFastPayment(): String {
        return "SELECT id, icon, name_fast_payment, rating , childCategories ," +
                "cash_account_name AS cash_account_name_value, " +
                "currency_name AS currency_name_value, " +
                "name_fast_payment AS category_name_value, " +
                "is_income, amount, description " +
                "FROM fast_payments_table, cash_account_table, currency_table, parent_categories_table " +
                "WHERE cash_account == cashAccountId " +
                "AND currency == currencyId " +
                "AND category == categoriesId "
    }

    fun createQueryOneFullFastPayment(id: Long): SimpleSQLiteQuery {
        var queryString = mainQueryFastPayment()
        val argsList: ArrayList<Any> = arrayListOf()

        if (id > 0) {
            queryString += addAnd()
            queryString += " id = :id "
            argsList.add(id)
        }

        val args = argsList.toArray()
        Message.log("query = $queryString")
        return SimpleSQLiteQuery(queryString, args)
    }

    private fun addAnd(): String {
        return " AND "
    }
}