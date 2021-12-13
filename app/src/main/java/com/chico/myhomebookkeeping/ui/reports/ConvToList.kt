package com.chico.myhomebookkeeping.ui.reports

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.fragments.categories.ReportsCategoriesItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCashAccountItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCurrenciesItem

object ConvToList {

    fun cashAccountsListToReportsItemsList(
        cashAccountsList: List<CashAccount>
    ): MutableList<ReportsCashAccountItem> {
        val list: MutableList<ReportsCashAccountItem> = mutableListOf()
        for (i in cashAccountsList.indices) {
            list.add(ReportsCashAccountItem(i, cashAccountsList[i].accountName, false))
        }
        return list
    }

    fun currenciesListToReportsItemsList(
        currenciesList: List<Currencies>
    ): MutableList<ReportsCurrenciesItem> {
        val list: MutableList<ReportsCurrenciesItem> = mutableListOf()
        for (i in currenciesList.indices) {
            list.add(ReportsCurrenciesItem(i, currenciesList[i].currencyName, false))
        }
        return list
    }

    fun moneyMovementListToMap(list: List<FullMoneyMoving>): Map<String, Double> {
        return list
            .sortedBy { it.categoryNameValue }
            .groupBy { it.categoryNameValue }
            .mapValues { it.value.sumOf { it.amount } }
    }

    fun categoriesListToSelectedCategoriesSet(categoriesList: List<Categories>): Set<Int> {
        return categoriesList.map { it.categoriesId ?: 0 }.toSet()
    }

    fun categoriesListToCategoriesItemsList(categoriesList: List<Categories>):
            List<ReportsCategoriesItem> {
        return categoriesList.map {
            Message.log("line categories list id = ${it.categoriesId}")
            ReportsCategoriesItem(it.categoriesId ?: 0, it.categoryName, " ", it.isIncome, false)

        }
    }

//    fun categoriesListToCategoriesItemsList(categoriesList: List<Categories>):
//            List<ReportsCategoriesItem> {
//        val categoriesItemsList = mutableListOf<ReportsCategoriesItem>()
//        for (i in categoriesList.indices) {
//            categoriesItemsList.add(
//                ReportsCategoriesItem(
//                    id = categoriesList[i].categoriesId ?: 0,
//                    name = categoriesList[i].categoryName,
//                    amount = " ",
//                    isIncome = categoriesList[i].isIncome,
//                    isChecked = false
//                )
//            )
//        }
//
//        return categoriesItemsList
//    }


}
