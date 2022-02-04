package com.chico.myhomebookkeeping.icons

import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.enums.icons.CategoriesOfIconsNames

object AddIconCategories {

    private val namesIconCategory = listOf<String>(
        CategoriesOfIconsNames.CashAccounts.name,
        CategoriesOfIconsNames.Categories.name,
        CategoriesOfIconsNames.Currencies.name
    )

    suspend fun add(dbIconCategories: IconCategoryDao) {
        for (i in namesIconCategory.indices) {
            IconCategoriesUseCase.addIconCategory(
                dbIconCategories,
                iconCategory = IconCategory(namesIconCategory[i])
            )
        }
    }

}