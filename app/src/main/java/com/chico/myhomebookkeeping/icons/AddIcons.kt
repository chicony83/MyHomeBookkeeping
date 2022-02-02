package com.chico.myhomebookkeeping.icons

import android.annotation.SuppressLint
import android.content.res.Resources
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.utils.launchIo

class AddIcons(
    private val dbIconResources: IconResourcesDao,
    resources: Resources,
    opPackageName: String
) {
    private val iconsMaps = IconsMaps(resources, opPackageName)
    private val categoryIconsMap = iconsMaps.getCategoriesIconsMap()
    private val cashAccountIconsMap = iconsMaps.getCashAccountIconsList()

    private var numOfAddedIcon = 0
    fun getNumOfAddedIcons() = numOfAddedIcon

    fun addCategoriesIconsInDB(iconCategory: IconCategory) {
//        Message.log("---Add categories icons---")
        addIconsRecourseList(
            iconsMap = categoryIconsMap,
            iconCategory = iconCategory
        )
    }

    fun addCashAccountsIconsInDB(iconCategory: IconCategory) {
//        Message.log("---Add Cash accounts icons---")
        addIconsRecourseList(
            iconsMap = cashAccountIconsMap,
            iconCategory = iconCategory
        )
    }

    @SuppressLint("NewApi")
    private fun addIconsRecourseList(
        iconsMap: Map<String, Int>,
        iconCategory: IconCategory
    ) {
        iconsMap.forEach { (name, iconRes) ->
            addIconResource(
                name = name,
                iconCategory = iconCategory.id,
                iconResource = iconRes
            )
            numOfAddedIcon++
            Message.log("added icon name $name number $numOfAddedIcon")
        }

//        for (i in iconsMap.iterator()) {
//            addIconResource(
//                name = iconsMap[i]
//                iconCategory.id, iconsMap[i])
//        }
    }

    private fun addIconResource(name: String, iconCategory: Int?, iconResource: Int) {
//        Message.log("---Add new icon resource---")
        launchIo {
            IconResourcesUseCase.addNewIconResource(
                dbIconResources,
                IconsResource(
                    iconName = name,
                    iconCategory = iconCategory ?: 0,
                    iconResources = iconResource
                )
            )
        }
    }


}