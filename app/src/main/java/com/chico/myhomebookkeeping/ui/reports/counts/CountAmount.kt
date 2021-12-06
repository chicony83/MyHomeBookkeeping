package com.chico.myhomebookkeeping.ui.reports.counts

import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCategoriesItem

object CountAmount {
    fun countAmountInCategories(listOfFullMoneyMovements: List<FullMoneyMoving>?)
            : MutableSet<ReportsCategoriesItem> {
        var categoriesItemsSetOf = mutableSetOf<ReportsCategoriesItem>()

        Message.log("size of categories items list = ${categoriesItemsSetOf.size}")
//            listOfFullMoneyMovements.let {
//
//            }
        return categoriesItemsSetOf
    }
}