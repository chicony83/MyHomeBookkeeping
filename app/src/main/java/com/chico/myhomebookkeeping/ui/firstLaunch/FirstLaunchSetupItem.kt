package com.chico.myhomebookkeeping.ui.firstLaunch

import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames

data class FirstLaunchSetupItem(
    val img: Int,
    val name: String,
    val canonicalName: String = name,
    val nameRu: String? = null,
    val namePl: String? = null
)

data class FirstLaunchCategoryGroupItem(
    val parentName: String,
    val parentNameRu: String?,
    val parentNamePl: String?,
    val parentIcon: CategoryIconNames,
    val isIncome: Boolean,
    val subcategories: List<String>,
    val subcategoriesRu: List<String>,
    val subcategoriesPl: List<String>,
    val subcategoryIcons: List<CategoryIconNames>
)
