package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCategoriesItem

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

    fun createSampleQueryForReports(
        startTimePeriodLong: Long,
        endTimePeriodLong: Long,
//        listItemsOfCashAccounts: List<ReportsCashAccountItem>,
        listItemsOfCategories: List<ReportsCategoriesItem>,
    ): SimpleSQLiteQuery {
        var query = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Any> = arrayListOf()

        val countCategories: Int = countCategories(listItemsOfCategories)
        Message.log("count categories = $countCategories")
        if (countCategories == 1) {
            for (i in listItemsOfCategories.indices) {
                if (listItemsOfCategories[i].isChecked) {
//                    Message.log(" add one category")
                    query += addAnd()
                    query += addCategory()
                    argsList.add(i + 1)
                }
            }
        }
        if (countCategories > 1) {
            var counter = 0
            Message.log("many categories")
            query += addAnd()
            query += "("
            for (i in listItemsOfCategories.indices) {
                if (listItemsOfCategories[i].isChecked) {
                    counter++
                    if (counter > 1) {
                        query += "OR"
                    }
                    query += addCategory(i + 1)
                }
            }
            query += ")"
        }

        query = checkTimePeriod(startTimePeriodLong, endTimePeriodLong, query)

        Message.log(query)
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
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

    private fun countCategories(list: List<ReportsCategoriesItem>): Int {
        var count = 0
        for (i in list.indices) {
            if (list[i].isChecked) count++
        }
        return count
    }

    private fun addAnd(): String {
        return " AND "
    }

    fun createSampleQueryForReports(
        startTimePeriodLong: Long,
        endTimePeriodLong: Long,
        setItemsOfCategories: Set<Int>
    ): SimpleSQLiteQuery {
        var query = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Int> = arrayListOf()
        val listSelectedCategories = setItemsOfCategories.toList()
        val countCategories = listSelectedCategories.size

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
//                argsList.add(listSelectedCategories[i])
            }
            query += " ) "
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