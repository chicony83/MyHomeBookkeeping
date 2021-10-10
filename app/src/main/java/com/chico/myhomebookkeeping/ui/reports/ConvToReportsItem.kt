package com.chico.myhomebookkeeping.ui.reports

import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message

object ConvToReportsItem {

    fun categoriesListToItems(
        categoriesList: List<Categories>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in categoriesList.indices) {
            list.add(ReportsItem(i, categoriesList[i].categoryName))
            addToListMessage(list, i)
        }
        return list
    }

    fun cashAccountsListToItems(
        cashAccountsList: List<CashAccount>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in cashAccountsList.indices) {
            list.add(ReportsItem(i, cashAccountsList[i].accountName))
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

    fun currenciesListToItems(
        currenciesList: List<Currencies>
    ): MutableList<ReportsItem> {
        val list: MutableList<ReportsItem> = mutableListOf()
        for (i in currenciesList.indices) {
            list.add(ReportsItem(i, currenciesList[i].currencyName))
            addToListMessage(list, i)
        }
        return list
    }

}