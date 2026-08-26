package com.chico.myhomebookkeeping.obj

import android.content.SharedPreferences

object CategoriesPanelsOrder {
    private val defaultOrder = listOf(
        Constants.CATEGORIES_PANEL_RECENT,
        Constants.CATEGORIES_PANEL_FREQUENT
    )

    fun get(sharedPreferences: SharedPreferences): List<String> {
        val savedOrder = sharedPreferences
            .getString(Constants.CATEGORIES_PANELS_ORDER, "")
            .orEmpty()
            .split(",")
            .filter { it in defaultOrder }
            .distinct()
        return savedOrder + defaultOrder.filter { it !in savedOrder }
    }

    fun moveToBottom(sharedPreferences: SharedPreferences, panelKey: String) {
        if (panelKey !in defaultOrder) return
        val updatedOrder = get(sharedPreferences)
            .filter { it != panelKey } + panelKey
        sharedPreferences.edit()
            .putString(Constants.CATEGORIES_PANELS_ORDER, updatedOrder.joinToString(","))
            .apply()
    }

    internal fun moveToBottom(currentOrder: List<String>, panelKey: String): List<String> {
        if (panelKey !in defaultOrder) return normalize(currentOrder)
        return normalize(currentOrder).filter { it != panelKey } + panelKey
    }

    private fun normalize(order: List<String>): List<String> {
        val supportedOrder = order.filter { it in defaultOrder }.distinct()
        return supportedOrder + defaultOrder.filter { it !in supportedOrder }
    }
}
