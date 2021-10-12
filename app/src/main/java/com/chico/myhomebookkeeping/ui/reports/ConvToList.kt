package com.chico.myhomebookkeeping.ui.reports

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message

object ConvToList {

    fun categoriesListToReportsItemsList(
        categoriesList: List<Categories>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in categoriesList.indices) {
            list.add(ReportsItem(i, categoriesList[i].categoryName, false))
            addToListMessage(list, i)
        }
        return list
    }

    fun cashAccountsListToReportsItemsList(
        cashAccountsList: List<CashAccount>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in cashAccountsList.indices) {
            list.add(ReportsItem(i, cashAccountsList[i].accountName, false))
            addToListMessage(list, i)
        }
        return list
    }


    fun currenciesListToReportsItemsList(
        currenciesList: List<Currencies>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in currenciesList.indices) {
            list.add(ReportsItem(i, currenciesList[i].currencyName, false))
            addToListMessage(list, i)
        }
        return list
    }

    private fun addToListMessage(
        list: MutableList<ReportsItem>,
        i: Int
    ) {
        Message.log("add in List ${list[i].id}, name ${list[i].name}")
    }

    fun moneyMovementListToMap(list: List<FullMoneyMoving>): Map<String, Double> {
        return list
            .sortedBy { it.categoryNameValue }
            .groupBy { it.categoryNameValue }
            .mapValues { it.value.sumOf { it.amount } }
    }
}