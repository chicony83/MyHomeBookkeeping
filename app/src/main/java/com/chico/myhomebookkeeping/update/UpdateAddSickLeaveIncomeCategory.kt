package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.domain.DefaultPolishNames
import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.IconsMaps
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants

class UpdateAddSickLeaveIncomeCategory {
    suspend fun update(app: Application) {
        Message.log("...updating sick leave income category...")

        val categoryDb: CategoryDao =
            dataBase.getDataBase(app.applicationContext).categoryDao()
        val parentCategoryDb: ParentCategoriesDao =
            dataBase.getDataBase(app.applicationContext).parentCategoriesDao()

        addCategoryIfNeeded(app, categoryDb, parentCategoryDb)

        Message.log("...updating sick leave income category complete...")
    }

    private suspend fun addCategoryIfNeeded(
        app: Application,
        categoryDb: CategoryDao,
        parentCategoryDb: ParentCategoriesDao
    ) {
        val existingCategory = categoryDb.getOneCategoryByAnyDefaultName(CATEGORY_NAMES)
        if (existingCategory != null) return

        val parentCategoryId = getIncomeParentCategoryId(app, parentCategoryDb) ?: return
        val categoryIcon = IconsMaps(app.resources, app.packageName)
            .getCategoriesIconsMap()[CategoryIconNames.Medical.name]

        categoryDb.addCategory(
            Categories(
                categoryName = CATEGORY_NAME,
                isIncome = true,
                icon = categoryIcon,
                parentCategoryId = parentCategoryId,
                categoryOrder = Int.MAX_VALUE,
                categoryNameRu = CATEGORY_NAME_RU,
                categoryNamePl = CATEGORY_NAME_PL
            )
        )
    }

    private suspend fun getIncomeParentCategoryId(
        app: Application,
        parentCategoryDb: ParentCategoriesDao
    ): Int? {
        val incomeGroup = INCOME_PARENT_NAMES[AppLanguage.getSelectedTag(app.applicationContext)]
            ?: INCOME_PARENT_NAMES.getValue(Constants.APP_LANGUAGE_ENGLISH)
        return parentCategoryDb.getParentCategoryByAnyDisplayName(incomeGroup)?.id
            ?: INCOME_PARENT_NAMES.values.firstNotNullOfOrNull {
                parentCategoryDb.getParentCategoryByAnyDisplayName(it)?.id
            }
    }

    private companion object {
        const val CATEGORY_NAME = "Sick leave payments"
        const val CATEGORY_NAME_RU = "Выплата по больничным листам"
        val CATEGORY_NAME_PL = DefaultPolishNames.categoryName(CATEGORY_NAME)
        val INCOME_PARENT_NAMES = mapOf(
            Constants.APP_LANGUAGE_ENGLISH to "Income",
            Constants.APP_LANGUAGE_RUSSIAN to "Доходы",
            Constants.APP_LANGUAGE_POLISH to "Przychody"
        )
        val CATEGORY_NAMES = listOf(CATEGORY_NAME, CATEGORY_NAME_RU, CATEGORY_NAME_PL)
    }
}
