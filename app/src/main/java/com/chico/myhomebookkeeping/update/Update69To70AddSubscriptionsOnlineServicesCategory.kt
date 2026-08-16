package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import com.chico.myhomebookkeeping.domain.DefaultCategoryGroup
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.enums.icon.names.CategoriesOfIconsNames
import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.IconsMaps

class Update69To70AddSubscriptionsOnlineServicesCategory {
    suspend fun update(app: Application) {
        Message.log("...updating 69 to 70: add subscriptions online services category...")

        val categoryDb: CategoryDao =
            dataBase.getDataBase(app.applicationContext).categoryDao()
        val parentCategoryDb: ParentCategoriesDao =
            dataBase.getDataBase(app.applicationContext).parentCategoriesDao()
        val iconResourcesDb: IconResourcesDao =
            dataBase.getDataBase(app.applicationContext).iconResourcesDao()
        val iconCategoryDb: IconCategoryDao =
            dataBase.getDataBase(app.applicationContext).iconCategoryDao()

        addNewIconResourcesIfNeeded(app, iconResourcesDb, iconCategoryDb)
        addCategoryGroupIfNeeded(app, categoryDb, parentCategoryDb)

        Message.log("...updating 69 to 70: add subscriptions online services category complete...")
    }

    private suspend fun addNewIconResourcesIfNeeded(
        app: Application,
        iconResourcesDb: IconResourcesDao,
        iconCategoryDb: IconCategoryDao
    ) {
        val iconCategory = IconCategoriesUseCase.getIconCategoryByName(
            db = iconCategoryDb,
            name = CategoriesOfIconsNames.Categories.name
        ) ?: return
        val iconCategoryId = iconCategory.id ?: return
        val categoryIconsMap = IconsMaps(app.resources, app.packageName).getCategoriesIconsMap()

        NEW_ICON_NAMES.forEach { iconName ->
            val existingIcon = IconResourcesUseCase.getIconByNameAndCategory(
                db = iconResourcesDb,
                name = iconName.name,
                iconCategory = iconCategoryId
            )
            if (existingIcon == null) {
                categoryIconsMap[iconName.name]?.let { iconResource ->
                    IconResourcesUseCase.addNewIconResource(
                        db = iconResourcesDb,
                        newIcon = IconsResource(
                            iconName = iconName.name,
                            iconCategory = iconCategoryId,
                            iconResources = iconResource
                        )
                    )
                }
            }
        }
    }

    private suspend fun addCategoryGroupIfNeeded(
        app: Application,
        categoryDb: CategoryDao,
        parentCategoryDb: ParentCategoriesDao
    ) {
        val group = DefaultCategoryCatalog.groups.first {
            it.parentName == PARENT_CATEGORY_NAME
        }
        val categoryIconsMap = IconsMaps(app.resources, app.packageName).getCategoriesIconsMap()
        val parentCategory = getOrAddParentCategory(parentCategoryDb, categoryIconsMap, group)
        val parentCategoryId = parentCategory.id ?: return
        val existingCategories = categoryDb.getAllCategoriesWithParentIdSortNameAsc(parentCategoryId).orEmpty()

        group.subcategories.forEachIndexed { index, categoryName ->
            val defaultNames = listOf(
                categoryName,
                group.subcategoriesRu[index],
                group.subcategoriesPl[index]
            )
            if (existingCategories.any { it.hasAnyName(defaultNames) }) return@forEachIndexed

            categoryDb.addCategory(
                Categories(
                    categoryName = categoryName,
                    isIncome = group.isIncome,
                    icon = categoryIconsMap[group.subcategoryIcons[index].name],
                    parentCategoryId = parentCategoryId,
                    categoryOrder = index,
                    categoryNameRu = group.subcategoriesRu[index],
                    categoryNamePl = group.subcategoriesPl[index]
                )
            )
        }
    }

    private suspend fun getOrAddParentCategory(
        parentCategoryDb: ParentCategoriesDao,
        categoryIconsMap: Map<String, Int>,
        group: DefaultCategoryGroup
    ): ParentCategories {
        val defaultNames = listOf(group.parentName, group.parentNameRu, group.parentNamePl)
        defaultNames.forEach { name ->
            parentCategoryDb.getParentCategoryByAnyDisplayName(name)?.let { return it }
        }

        val parentCategories = parentCategoryDb.getAllParentCategoriesSortNameAsc()
        val parentCategory = ParentCategories(
            name = group.parentName,
            icon = categoryIconsMap[group.parentIcon.name],
            parentCategoryOrder = parentCategories.maxOfOrNull { it.parentCategoryOrder }?.plus(1) ?: 0,
            nameRu = group.parentNameRu,
            namePl = group.parentNamePl
        )
        val addedId = parentCategoryDb.addNewParentCategory(parentCategory)
        parentCategory.id = addedId.toInt()
        return parentCategory
    }

    private fun Categories.hasAnyName(names: List<String>): Boolean {
        return categoryName in names || categoryNameRu in names || categoryNamePl in names
    }

    private companion object {
        const val PARENT_CATEGORY_NAME = "Subscriptions & Online Services"
        val NEW_ICON_NAMES = listOf(
            CategoryIconNames.Apps,
            CategoryIconNames.Cloud,
            CategoryIconNames.Videocam,
            CategoryIconNames.VideogameAsset
        )
    }
}
