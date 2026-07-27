package com.chico.myhomebookkeeping.ui.reports

import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.ui.reports.selectCategories.ReportsCategoriesItem
import com.chico.myhomebookkeeping.data.reports.ReportsCashAccountItem
import com.chico.myhomebookkeeping.data.reports.ReportsCurrenciesItem
import com.chico.myhomebookkeeping.helpers.displayName
import com.chico.myhomebookkeeping.obj.Constants

object ConvToList {

    fun cashAccountsListToReportsItemsList(
        cashAccountsList: List<CashAccount>,
        languageTag: String = Constants.APP_LANGUAGE_ENGLISH
    ): MutableList<ReportsCashAccountItem> {
        val list: MutableList<ReportsCashAccountItem> = mutableListOf()
        for (i in cashAccountsList.indices) {
            list.add(ReportsCashAccountItem(i, cashAccountsList[i].displayName(languageTag), false))
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
            .filter { it.categoryNameValue != null }
            .sortedBy { it.categoryNameValue }
            .groupBy { it.categoryNameValue.orEmpty() }
            .mapValues { it.value.sumOf { it.amount } }
    }

    fun categoriesListToSelectedCategoriesSet(categoriesList: List<Categories>): Set<Int> {
        return categoriesList.map { it.categoriesId ?: 0 }.toSet()
    }

    fun categoriesListToCategoriesItemsList(
        categoriesList: List<Categories>,
        languageTag: String = Constants.APP_LANGUAGE_ENGLISH
    ):
            List<ReportsCategoriesItem> {
        return categoriesList.map {
//            Message.log("line categories list id = ${it.categoriesId}")
            ReportsCategoriesItem(it.categoriesId ?: 0, it.displayName(languageTag), " ", it.isIncome, false)

        }
    }
}
