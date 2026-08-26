package com.chico.myhomebookkeeping.obj

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.db.entity.Categories

object FrequentCategoriesPanel {
    fun isEnabled(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.FREQUENT_CATEGORIES_PANEL_ENABLED, true)
    }

    fun isExpanded(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.FREQUENT_CATEGORIES_PANEL_EXPANDED, true)
    }

    fun shouldShowTitle(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.FREQUENT_CATEGORIES_PANEL_SHOW_TITLE, true)
    }

    fun shouldShowLabels(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.FREQUENT_CATEGORIES_SHOW_LABELS, false)
    }

    fun limit(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(
            Constants.FREQUENT_CATEGORIES_LIMIT,
            Constants.FREQUENT_CATEGORIES_DEFAULT_LIMIT
        ).coerceIn(Constants.FREQUENT_CATEGORIES_MIN_LIMIT, Constants.FREQUENT_CATEGORIES_MAX_LIMIT)
    }

    fun categories(
        sourceCategories: List<Categories>,
        sharedPreferences: SharedPreferences
    ): List<Categories> {
        return categories(
            sourceCategories = sourceCategories,
            limit = limit(sharedPreferences)
        )
    }

    internal fun categories(
        sourceCategories: List<Categories>,
        limit: Int
    ): List<Categories> {
        return sourceCategories
            .filter { it.categoriesId != null && it.usageCount > 0 }
            .sortedWith(
                compareByDescending<Categories> { it.usageCount }
                    .thenBy { it.categoryName.lowercase() }
                    .thenBy { it.categoriesId ?: Int.MAX_VALUE }
            )
            .take(limit)
    }
}
