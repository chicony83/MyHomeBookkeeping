package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.helpers.Message

object ReportsCreateSimpleQuery {
    private fun mainQueryFullMoneyMoving(): String {
        return "SELECT id,time_stamp, " +
                "cash_account_name AS cash_account_name_value, " +
                "currency_name AS currency_name_value," +
                "category_name AS category_name_value, " +
                "amount, is_income, description " +
                "FROM money_moving_table,cash_account_table,currency_table,category_table " +
                "WHERE cash_account == cashAccountId " +
                "AND currency == currencyId " +
                "AND category == categoriesId"
    }

    private fun checkTimePeriod(
        startTimePeriodLong: Long,
        endTimePeriodLong: Long,
        query: String
    ): String {
        var query1 = query
        if ((startTimePeriodLong > 0) and (endTimePeriodLong > 0)) {
            query1 += "AND time_stamp BETWEEN $startTimePeriodLong AND $endTimePeriodLong"
        }
        if ((startTimePeriodLong > 0) xor (endTimePeriodLong > 0)) {
            if (startTimePeriodLong > 0) {
                query1 += "AND time_stamp > $startTimePeriodLong"
            }
            if (endTimePeriodLong > 0) {
                query1 += "AND time_stamp < $endTimePeriodLong"
            }
        }
        return query1
    }

    private fun addCategory(id: Int): String {
        return " category = $id "
    }

    private fun addCategory(): String {
        return " category = :category "

    }

    private fun addAnd(): String {
        return " AND "
    }

    fun createSampleQueryForReports(
        startTimePeriodLong: Long,
        endTimePeriodLong: Long,
        setItemsOfCategories: Set<Int>,
        numbersOfAllCategories: Int
    ): SimpleSQLiteQuery {
        var query = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Int> = arrayListOf()
        val listSelectedCategories = setItemsOfCategories.toList()
        val countCategories = listSelectedCategories.size

        if (setItemsOfCategories.size != numbersOfAllCategories) {
            if (countCategories == 1) {
                query += addAnd()
                query += addCategory()
                for (i in listSelectedCategories.indices) {
                    argsList.add(listSelectedCategories[i])
                }
            }
            if (countCategories > 1) {
                var counter = 0
                query += addAnd()
                query += " ( "
                for (i in listSelectedCategories.indices) {
                    counter++
                    if (counter > 1) {
                        query += addOr()
                    }
                    query += addCategory(listSelectedCategories[i])
                }
                query += " ) "
            }
        }

        query = checkTimePeriod(startTimePeriodLong, endTimePeriodLong, query)

        Message.log("ARGS ${argsList.joinToString()}")
        Message.log(query)

        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    private fun addOr(): String {
        return " OR "
    }

}