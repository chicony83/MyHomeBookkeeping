package com.chico.myhomebookkeeping.ui.reports

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCashAccountItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCategoriesItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCurrenciesItem

object ConvToList {

    fun categoriesListToReportsItemsList(
        categoriesList: List<Categories>
    ): MutableList<ReportsCategoriesItem> {
        val list: MutableList<ReportsCategoriesItem> = mutableListOf()
        for (i in categoriesList.indices) {
            list.add(ReportsCategoriesItem(i, categoriesList[i].categoryName, true))
            addToListMessage(list, i)
        }
        return list
    }

    fun cashAccountsListToReportsItemsList(
        cashAccountsList: List<CashAccount>
    ): MutableList<ReportsCashAccountItem> {
        val list: MutableList<ReportsCashAccountItem> = mutableListOf()
        for (i in cashAccountsList.indices) {
            list.add(ReportsCashAccountItem(i, cashAccountsList[i].accountName, false))
//            addToListMessage(list, i)
        }
        return list
    }

    fun currenciesListToReportsItemsList(
        currenciesList: List<Currencies>
    ): MutableList<ReportsCurrenciesItem> {
        val list: MutableList<ReportsCurrenciesItem> = mutableListOf()
        for (i in currenciesList.indices) {
            list.add(ReportsCurrenciesItem(i, currenciesList[i].currencyName, false))
//            addToListMessage(list, i)
        }
        return list
    }

    private fun addToListMessage(
        list: MutableList<ReportsCategoriesItem>,
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