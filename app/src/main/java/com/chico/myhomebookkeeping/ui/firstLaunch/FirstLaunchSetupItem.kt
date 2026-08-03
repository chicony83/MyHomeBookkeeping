package com.chico.myhomebookkeeping.ui.firstLaunch

data class FirstLaunchSetupItem(
    val img: Int,
    val name: String
)

data class FirstLaunchCategoryGroupItem(
    val parentName: String,
    val parentNameRu: String?,
    val parentNamePl: String?,
    val isIncome: Boolean,
    val subcategories: List<String>,
    val subcategoriesRu: List<String>,
    val subcategoriesPl: List<String>
)
