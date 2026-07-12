package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.enums.icon.names.CategoriesOfIconsNames
import com.chico.myhomebookkeeping.enums.icon.names.NoCategoryNames
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.utils.launchForResult

class Update44To45 {
    suspend fun update(app: Application) {
        Message.log("...updating 44 to 45...")

        val iconResourcesDb: IconResourcesDao =
            dataBase.getDataBase(app.applicationContext).iconResourcesDao()
        val iconCategoriesDb: IconCategoryDao =
            dataBase.getDataBase(app.applicationContext).iconCategoryDao()

        val noCategoryIconCategory = getOrAddNoCategoryIconCategory(iconCategoriesDb)
        val noCategoryIconCategoryId = noCategoryIconCategory.id ?: return
        val hasNoImageIcon =
            IconResourcesUseCase.getIconByNameAndCategory(
                db = iconResourcesDb,
                name = NoCategoryNames.NoImage.name,
                iconCategory = noCategoryIconCategoryId
            ) != null

        if (!hasNoImageIcon) {
            IconResourcesUseCase.addNewIconResource(
                db = iconResourcesDb,
                newIcon = IconsResource(
                    iconName = NoCategoryNames.NoImage.name,
                    iconCategory = noCategoryIconCategoryId,
                    iconResources = R.drawable.no_image
                )
            )
        }

        Message.log("...updating 44 ne 45 complete...")
    }

    private suspend fun getOrAddNoCategoryIconCategory(iconCategoriesDb: IconCategoryDao): IconCategory {
        val iconCategoryName = CategoriesOfIconsNames.IconHasNoCategory.name
        IconCategoriesUseCase.getIconCategoryByName(
            db = iconCategoriesDb,
            name = iconCategoryName
        )?.let {
            return it
        }

        val addedId = addIconCategoryNoCategory(iconCategoriesDb)
        return IconCategoriesUseCase.getIconCategoryById(
            db = iconCategoriesDb,
            id = addedId.toInt()
        )
    }

    private suspend fun addIconCategoryNoCategory(iconCategoriesDb: IconCategoryDao): Long {
        return launchForResult {
            AddIconCategories.addIconCategoryNoCategory(dbIconCategories = iconCategoriesDb)
        } ?: 0
    }
}
