package com.chico.myhomebookkeeping.obj

import android.content.SharedPreferences

object RecentCategoriesPanel {
    fun isEnabled(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.RECENT_CATEGORIES_PANEL_ENABLED, true)
    }

    fun isExpanded(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.RECENT_CATEGORIES_PANEL_EXPANDED, true)
    }

    fun shouldShowTitle(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.RECENT_CATEGORIES_PANEL_SHOW_TITLE, true)
    }

    fun shouldShowLabels(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(Constants.RECENT_CATEGORIES_SHOW_LABELS, false)
    }

    fun limit(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(
            Constants.RECENT_CATEGORIES_LIMIT,
            Constants.RECENT_CATEGORIES_DEFAULT_LIMIT
        ).coerceIn(Constants.RECENT_CATEGORIES_MIN_LIMIT, Constants.RECENT_CATEGORIES_MAX_LIMIT)
    }

    fun ids(sharedPreferences: SharedPreferences): List<Int> {
        return parseIds(sharedPreferences.getString(Constants.RECENT_CATEGORIES_IDS, "").orEmpty())
            .take(limit(sharedPreferences))
    }

    fun record(sharedPreferences: SharedPreferences, categoryId: Int) {
        if (categoryId <= 0) return
        val updatedIds = moveToFront(
            currentIds = parseIds(
                sharedPreferences.getString(Constants.RECENT_CATEGORIES_IDS, "").orEmpty()
            ),
            categoryId = categoryId,
            limit = Constants.RECENT_CATEGORIES_MAX_LIMIT
        )
        sharedPreferences.edit()
            .putString(Constants.RECENT_CATEGORIES_IDS, updatedIds.joinToString(","))
            .apply()
    }

    internal fun moveToFront(currentIds: List<Int>, categoryId: Int, limit: Int): List<Int> {
        if (categoryId <= 0) return currentIds.take(limit)
        return (listOf(categoryId) + currentIds.filter { it != categoryId })
            .distinct()
            .take(limit)
    }

    private fun parseIds(rawIds: String): List<Int> {
        return rawIds.split(",")
            .mapNotNull { it.toIntOrNull() }
            .distinct()
    }
}
