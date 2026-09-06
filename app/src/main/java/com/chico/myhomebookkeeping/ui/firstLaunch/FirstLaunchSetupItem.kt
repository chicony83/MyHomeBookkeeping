package com.chico.myhomebookkeeping.ui.firstLaunch

import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames

data class FirstLaunchSetupItem(
    val img: Int,
    val name: String,
    val canonicalName: String = name,
    val nameRu: String? = null,
    val namePl: String? = null,
    val nameDe: String? = null,
    val nameBe: String? = null,
    val nameBeLatn: String? = null
)

data class FirstLaunchCategoryGroupItem(
    val parentName: String,
    val parentNameRu: String?,
    val parentNamePl: String?,
    val parentNameDe: String?,
    val parentNameBe: String?,
    val parentNameBeLatn: String?,
    val parentIcon: CategoryIconNames,
    val isIncome: Boolean,
    val subcategories: List<String>,
    val subcategoriesRu: List<String>,
    val subcategoriesPl: List<String>,
    val subcategoriesDe: List<String>,
    val subcategoriesBe: List<String>,
    val subcategoriesBeLatn: List<String>,
    val subcategoryIcons: List<CategoryIconNames>
)
