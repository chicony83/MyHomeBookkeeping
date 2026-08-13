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
import com.chico.myhomebookkeeping.ui.reports.main.ReportCategoryItem

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

    fun moneyMovementListToReportCategoryItems(list: List<FullMoneyMoving>): List<ReportCategoryItem> {
        val groupedByCategory = list
            .filter { it.categoryIdValue != null && it.categoryNameValue != null }
            .groupBy { it.categoryIdValue ?: 0 }

        val totalAmount = groupedByCategory.values.sumOf { categoryItems ->
            categoryItems.sumOf { it.amount }
        }

        if (totalAmount <= 0.0) return emptyList()

        return groupedByCategory.map { (categoryId, categoryItems) ->
            val amount = categoryItems.sumOf { it.amount }
            ReportCategoryItem(
                categoryId = categoryId,
                displayName = categoryItems.first().categoryDisplayName.orEmpty(),
                iconRes = categoryItems.first().categoryIconValue
                    ?: categoryItems.first().parentCategoryIconValue,
                amount = amount,
                percentage = amount / totalAmount * 100,
                color = stableCategoryColor(categoryId)
            )
        }.sortedByDescending { it.amount }
    }

    private fun stableCategoryColor(categoryId: Int): Int {
        val palette = listOf(
            0xFF176B73.toInt(),
            0xFFB64A42.toInt(),
            0xFF5B7CFA.toInt(),
            0xFF7C5C2E.toInt(),
            0xFF348A59.toInt(),
            0xFFC56A2D.toInt(),
            0xFF7B5EA7.toInt(),
            0xFF4F7D8A.toInt(),
            0xFFD69A2D.toInt(),
            0xFF8C5A5A.toInt()
        )
        return palette[Math.floorMod(categoryId, palette.size)]
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
            ReportsCategoriesItem(it.categoriesId ?: 0, it.displayName(languageTag), it.icon, false)

        }
    }
}
