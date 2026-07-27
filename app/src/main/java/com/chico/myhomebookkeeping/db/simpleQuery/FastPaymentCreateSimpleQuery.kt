package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.enums.StateRecyclerFastPaymentByType
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants

object FastPaymentCreateSimpleQuery {
    fun createQueryList(languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        val query = mainQueryFastPayment(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()

        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingAlphabetByAsc(languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        var query = mainQueryFastPayment(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY name_fast_payment_value ASC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingAlphabetByDesc(languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        var query = mainQueryFastPayment(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY name_fast_payment_value DESC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingRatingByAsc(languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        var query = mainQueryFastPayment(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY rating ASC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    fun createQuerySortingRatingByDesc(languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        var query = mainQueryFastPayment(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()
        query += " ORDER BY rating DESC "
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }
    private fun mainQueryFastPayment(languageTag: String): String {
        val fastPaymentName = localizedColumn("name_fast_payment", "name_fast_payment_ru", languageTag)
        val cashAccountName = localizedColumn("cash_account_name", "cash_account_name_ru", languageTag)
        val categoryName = localizedColumn("category_name", "category_name_ru", languageTag)
        return "SELECT id, icon, $fastPaymentName AS name_fast_payment_value, rating , " +
                "$cashAccountName AS cash_account_name_value, " +
                "currency_name AS currency_name_value, " +
                "$categoryName AS category_name_value, " +
                "is_income, amount, description " +
                "FROM fast_payments_table, cash_account_table, currency_table, category_table " +
                "WHERE cash_account == cashAccountId " +
                "AND currency == currencyId " +
                "AND category == categoriesId "
    }

    fun createQueryOneFullFastPayment(id: Long, languageTag: String = Constants.APP_LANGUAGE_ENGLISH): SimpleSQLiteQuery {
        var queryString = mainQueryFastPayment(languageTag)
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

    private fun localizedColumn(baseColumn: String, ruColumn: String, languageTag: String): String {
        return if (languageTag == Constants.APP_LANGUAGE_RUSSIAN) {
            "COALESCE($ruColumn, $baseColumn)"
        } else {
            baseColumn
        }
    }
}
