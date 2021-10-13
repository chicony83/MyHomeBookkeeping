package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.ReportsItem

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
        listItemsOfCashAccounts: List<ReportsItem>,
        listItemsOfCurrencies: List<ReportsItem>,
        listItemsOfCategories: List<ReportsItem>,
    ): SimpleSQLiteQuery {
        var query = mainQueryFullMoneyMoving()
        val argsList: ArrayList<Any> = arrayListOf()


        var countCategories: Int = countCategories(listItemsOfCategories)
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
//            query += " AND (category = 1 OR category = 2 OR category = 3)"

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
                    query += addCategory(i+1)
//                    argsList.add(i + 1)
//                    Message.log("add in argsList ${argsList.last()}")
                }
            }
            query += ")"
        }

//        query += " AND (category = 1 OR category = 2 )"

        Message.log(query)
        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    private fun addCategory(id: Int): String {
        return " category = $id "
    }

//    private fun getArgs(list: List<ReportsItem>): Any {
//        for (i in list.indices){
//            if (list[i].isChecked){
//
//            }
//        }
//
//    }

    private fun addCategory(): String {
        return " category = :category "

    }


    private fun countCategories(list: List<ReportsItem>): Int {
        var count = 0
        for (i in list.indices) {
            if (list[i].isChecked) count++
        }
        return count
    }

    private fun addAnd(): String {
        return " AND "
    }

}