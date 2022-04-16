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

    fun createQuerySortingAlphabetByAsc(typeOfFastPayments: String): SimpleSQLiteQuery {
        val orderBy = " ORDER BY name_fast_payment ASC "
        return createQuery(typeOfFastPayments,orderBy)
    }

    fun createQuerySortingAlphabetByDesc(typeOfFastPayments: String): SimpleSQLiteQuery {
        val orderBy = " ORDER BY name_fast_payment DESC "
        return createQuery(typeOfFastPayments,orderBy)
    }

    fun createQuerySortingRatingByAsc(typeOfFastPayments: String): SimpleSQLiteQuery {
        val orderBy = " ORDER BY rating ASC "
        return createQuery(typeOfFastPayments,orderBy)
    }

    fun createQuerySortingRatingByDesc(typeOfFastPayments: String): SimpleSQLiteQuery {
        val orderBy = " ORDER BY rating DESC "
        return createQuery(typeOfFastPayments,orderBy)
    }

    private fun createQuery(typeOfFastPayments: String, orderBy: String): SimpleSQLiteQuery {
        var query = mainQueryFastPayment()
        val type = getStringQueryFromType(typeOfFastPayments)
        val argsList: ArrayList<Any> = arrayListOf()
        query += type
        query += orderBy
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    private fun getStringQueryFromType(typeOfFastPayments: String): String {
        return when (typeOfFastPayments) {
            StateRecyclerFastPaymentByType.Income.name -> "AND is_income == true"
            StateRecyclerFastPaymentByType.Spending.name -> "AND is_income == false"
            StateRecyclerFastPaymentByType.All.name -> ""
            else -> ""
        }
    }

    private fun mainQueryFastPayment(): String {
        return "SELECT id, icon, name_fast_payment, rating , " +
                "cash_account_name AS cash_account_name_value, " +
                "currency_name AS currency_name_value, " +
                "category_name AS category_name_value, " +
                "is_income, amount, description " +
                "FROM fast_payments_table, cash_account_table, currency_table, category_table " +
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