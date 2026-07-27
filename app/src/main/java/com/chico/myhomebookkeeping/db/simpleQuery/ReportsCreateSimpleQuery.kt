package com.chico.myhomebookkeeping.db.simpleQuery

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants

object ReportsCreateSimpleQuery {
    private fun mainQueryFullMoneyMoving(languageTag: String): String {
        val cashAccountName = localizedColumn("cash_account_name", "cash_account_name_ru", languageTag)
        val categoryName = localizedColumn("category_name", "category_name_ru", languageTag)
        val parentCategoryName = localizedColumn("parent_category_name", "parent_category_name_ru", languageTag)
        return "SELECT money_moving_table.id,time_stamp, " +
                "$cashAccountName AS cash_account_name_value, " +
                "currency_name AS currency_name_value," +
                "$categoryName AS category_name_value, " +
                "$parentCategoryName AS parent_category_name_value, " +
                "amount, money_moving_table.payment_type_id = 0 AS is_income, " +
                "money_moving_table.payment_type_id, payment_type_name, " +
                "transfer_group_id, transfer_direction, description " +
                "FROM money_moving_table " +
                "INNER JOIN cash_account_table ON cash_account == cashAccountId " +
                "INNER JOIN currency_table ON currency == currencyId " +
                "INNER JOIN payment_type_table ON money_moving_table.payment_type_id == payment_type_table.id " +
                "INNER JOIN category_table ON category == categoriesId " +
                "LEFT JOIN parent_categories_table ON category_table.parent_category_id == parent_categories_table.id " +
                "WHERE money_moving_table.payment_type_id IN (0, 1)"
    }

    private fun checkTimePeriod(
        startTimePeriodLong: Long,
        endTimePeriodLong: Long,
        query: String,
        argsList: ArrayList<Any>
    ): String {
        var query1 = query
        if ((startTimePeriodLong > 0) and (endTimePeriodLong > 0)) {
            query1 += " AND time_stamp BETWEEN :startTime AND :endTime "
            argsList.add(startTimePeriodLong)
            argsList.add(endTimePeriodLong)
        }
        if ((startTimePeriodLong > 0) xor (endTimePeriodLong > 0)) {
            if (startTimePeriodLong > 0) {
                query1 += " AND time_stamp > :startTime "
                argsList.add(startTimePeriodLong)
            }
            if (endTimePeriodLong > 0) {
                query1 += " AND time_stamp < :endTime"
                argsList.add(endTimePeriodLong)
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
        numbersOfAllCategories: Int,
        languageTag: String = Constants.APP_LANGUAGE_ENGLISH
    ): SimpleSQLiteQuery {
        var query = mainQueryFullMoneyMoving(languageTag)
        val argsList: ArrayList<Any> = arrayListOf()
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

        query = checkTimePeriod(startTimePeriodLong, endTimePeriodLong, query, argsList)

        Message.log(" ARGS ${argsList.joinToString()}")
        Message.log(query)

        val args: Array<Any> = argsList.toArray()
        return SimpleSQLiteQuery(query, args)
    }

    private fun addOr(): String {
        return " OR "
    }

    private fun localizedColumn(baseColumn: String, ruColumn: String, languageTag: String): String {
        return if (languageTag == Constants.APP_LANGUAGE_RUSSIAN) {
            "COALESCE($ruColumn, $baseColumn)"
        } else {
            baseColumn
        }
    }

}
