package com.chico.myhomebookkeeping.domain

object DefaultBelarusianLatinNames {
    val cashAccounts = DefaultBelarusianNames.cashAccounts.mapValues { (_, name) ->
        BelarusianLatin.transliterate(name)
    }
    val parentCategories = DefaultBelarusianNames.parentCategories.mapValues { (_, name) ->
        BelarusianLatin.transliterate(name)
    }
    val categories = DefaultBelarusianNames.categories.mapValues { (_, name) ->
        BelarusianLatin.transliterate(name)
    }

    fun parentCategoryName(canonicalName: String): String =
        parentCategories[canonicalName] ?: canonicalName

    fun categoryName(canonicalName: String): String = categories[canonicalName] ?: canonicalName
}
