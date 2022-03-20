package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.icons.AddIcons
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo

class Update44To45 {
    fun update(app: Application) {
        Message.log("...updating 44 to 45...")

        val iconResourcesDb: IconResourcesDao =
            dataBase.getDataBase(app.applicationContext).iconResourcesDao()
        val iconCategoriesDb: IconCategoryDao =
            dataBase.getDataBase(app.applicationContext).iconCategoryDao()

        launchIo {
//            val categoryId =
//            val addedCategory =
            val addIcons = AddIcons(
                dbIconResources = iconResourcesDb,
                resources = app.resources,
                appPackageName = app.packageName
            )

            addIconCategoryNoCategory(iconCategoriesDb)?.let {
                IconCategoriesUseCase.getIconCategoryById(
                    db = iconCategoriesDb,
                    id = it.toInt()
                )
            }?.let {
                addIcons.addNoCategoryIconInDb(
                    iconCategory = it
                )
            }
        }


        Message.log("...updating 44 ne 45 complete...")
    }

    private suspend fun addIconCategoryNoCategory(iconCategoriesDb: IconCategoryDao): Long? {
        return launchForResult {
            AddIconCategories.addIconCategoryNoCategory(dbIconCategories = iconCategoriesDb)
        }
    }
}