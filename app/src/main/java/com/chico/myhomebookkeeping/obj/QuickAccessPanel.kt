package com.chico.myhomebookkeeping.obj

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.chico.myhomebookkeeping.R

object QuickAccessPanel {
    const val MIN_ITEMS = 3
    const val MAX_ITEMS = 5

    private const val SEPARATOR = ","

    val availableItems = listOf(
        Item(
            key = "fast_payments",
            destinationId = R.id.nav_fast_payments_fragment,
            titleRes = R.string.bottom_navigation_fast_payments,
            iconRes = R.drawable.blanks
        ),
        Item(
            key = "free_payment",
            destinationId = R.id.nav_new_money_moving,
            titleRes = R.string.bottom_navigation_free_fast_payment,
            iconRes = R.drawable.ic_add_box
        ),
        Item(
            key = "transfer",
            destinationId = R.id.nav_new_transfer,
            titleRes = R.string.bottom_navigation_transfer,
            iconRes = R.drawable.ic_attach_money
        ),
        Item(
            key = "categories",
            destinationId = R.id.nav_categories,
            titleRes = R.string.bottom_navigation_categories,
            iconRes = R.drawable.ic_menu_category
        ),
        Item(
            key = "currencies",
            destinationId = R.id.nav_currencies,
            titleRes = R.string.bottom_navigation_currencies,
            iconRes = R.drawable.ic_attach_money
        ),
        Item(
            key = "cash_accounts",
            destinationId = R.id.nav_cash_account,
            titleRes = R.string.bottom_navigation_cash_accounts,
            iconRes = R.drawable.ic_credit_card
        ),
        Item(
            key = "journal",
            destinationId = R.id.nav_money_moving,
            titleRes = R.string.bottom_navigation_journal,
            iconRes = R.drawable.journal
        ),
        Item(
            key = "reports",
            destinationId = R.id.nav_reports,
            titleRes = R.string.bottom_navigation_reports,
            iconRes = R.drawable.ic_menu_reports
        ),
        Item(
            key = "settings",
            destinationId = R.id.nav_setting,
            titleRes = R.string.bottom_navigation_settings,
            iconRes = R.drawable.ic_settings
        )
    )

    private val defaultKeys = listOf(
        "fast_payments",
        "categories",
        "journal",
        "settings"
    )

    fun getItems(sharedPreferences: SharedPreferences): List<Item> {
        val keys = getKeys(sharedPreferences)
        return keys.mapNotNull(::findByKey)
    }

    fun getKeys(sharedPreferences: SharedPreferences): List<String> {
        val savedValue = sharedPreferences.getString(Constants.QUICK_ACCESS_PANEL_ITEMS, null)
        val keys = savedValue
            ?.split(SEPARATOR)
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: defaultKeys

        return normalizeKeys(keys)
    }

    fun saveKeys(sharedPreferences: SharedPreferences, keys: List<String>) {
        sharedPreferences.edit()
            .putString(Constants.QUICK_ACCESS_PANEL_ITEMS, normalizeKeys(keys).joinToString(SEPARATOR))
            .apply()
    }

    fun findByKey(key: String): Item? {
        return availableItems.firstOrNull { it.key == key }
    }

    fun findByDestinationId(@IdRes destinationId: Int): Item? {
        return availableItems.firstOrNull { it.destinationId == destinationId }
    }

    private fun normalizeKeys(keys: List<String>): List<String> {
        return keys
            .distinct()
            .filter { key -> availableItems.any { it.key == key } }
            .take(MAX_ITEMS)
            .let { normalized ->
                if (normalized.size >= MIN_ITEMS) normalized else defaultKeys
            }
    }

    data class Item(
        val key: String,
        @IdRes val destinationId: Int,
        @StringRes val titleRes: Int,
        @DrawableRes val iconRes: Int
    )
}
