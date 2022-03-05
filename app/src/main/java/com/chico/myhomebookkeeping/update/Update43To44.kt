package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.icons.AddIcons
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.delay

class Update43To44 {
    suspend fun update(app: Application) {
        Message.log("...updating 43 to 44...")

        val iconResourcesDb: IconResourcesDao =
            dataBase.getDataBase(app.applicationContext).iconResourcesDao()
        val iconCategoryDB: IconCategoryDao =
            dataBase.getDataBase(app.applicationContext).iconCategoryDao()
        val numOfIconsCategories = IconCategoriesUseCase.getAllIconCategories(iconCategoryDB)
        val numOfIcons = IconResourcesUseCase.getIconsList(iconResourcesDb)
        val addIcons = AddIcons(
            dbIconResources = iconResourcesDb,
            resources = app.resources,
            opPackageName = app.packageName
        )

        if (numOfIconsCategories.isEmpty()) {
            addIconCategories(iconCategoryDB)
        }
        if (numOfIcons.isEmpty()) {
            var iconCategoriesList = listOf<IconCategory>()
            while (iconCategoriesList.size < 3) {
                delay(100)
                iconCategoriesList = IconCategoriesUseCase.getAllIconCategories(iconCategoryDB)
            }
            addIcons.addIconResources(iconCategoriesList = iconCategoriesList)
        }

        Message.log("...updating 43 ne 44 complete...")
    }

    private fun addIconCategories(iconCategoryDB: IconCategoryDao) {
        launchIo {
            AddIconCategories.add(iconCategoryDB)
        }
    }

}