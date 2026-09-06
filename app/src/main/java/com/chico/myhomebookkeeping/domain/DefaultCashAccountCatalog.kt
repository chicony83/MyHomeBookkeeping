package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.enums.icon.names.CashAccountIconNames
import com.chico.myhomebookkeeping.obj.Constants

object DefaultCashAccountCatalog {
    data class Account(
        val canonicalName: String,
        val nameRu: String,
        val namePl: String,
        val iconName: CashAccountIconNames
    ) {
        val nameDe: String get() = DefaultGermanNames.cashAccounts[canonicalName] ?: canonicalName
        val nameBe: String get() = DefaultBelarusianNames.cashAccounts[canonicalName] ?: canonicalName
        val nameBeLatn: String get() = DefaultBelarusianLatinNames.cashAccounts[canonicalName] ?: canonicalName

        fun displayName(languageTag: String): String {
            return when (languageTag) {
                Constants.APP_LANGUAGE_RUSSIAN -> nameRu
                Constants.APP_LANGUAGE_POLISH -> namePl
                Constants.APP_LANGUAGE_GERMAN -> nameDe
                Constants.APP_LANGUAGE_BELARUSIAN -> nameBe
                Constants.APP_LANGUAGE_BELARUSIAN_LATIN -> nameBeLatn
                else -> canonicalName
            }
        }
    }

    val accounts = listOf(
        Account(
            canonicalName = "Card",
            nameRu = "Карточка",
            namePl = "Karta",
            iconName = CashAccountIconNames.Card
        ),
        Account(
            canonicalName = "Cash",
            nameRu = "Наличные",
            namePl = "Gotówka",
            iconName = CashAccountIconNames.Cash
        )
    )
}
