package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.icons.AddIcons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.delay

class UpdateViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val sharedPreferences:SharedPreferences = app.getSharedPreferences(spName,Context.MODE_PRIVATE)
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(sharedPreferences.edit())


    fun update() {
        update_43_to_44_after_migration_2_to_3()
    }

    private fun update_43_to_44_after_migration_2_to_3() {
        val updateKey = ConstantsOfUpdate.UPDATE_43_TO_44_AFTER_MIGRATION_2_TO_3
        if (!getSP.getBooleanDefFalse(updateKey)){
            launchIo {
                addIconsInDataBaseUpdate43To44()
            }
            setSP.saveToSP(updateKey,true)
        }
    }

    suspend fun addIconsInDataBaseUpdate43To44() {
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
    }

    private fun addIconCategories(iconCategoryDB: IconCategoryDao) {
        launchIo {
            AddIconCategories.add(iconCategoryDB)
        }
    }

}