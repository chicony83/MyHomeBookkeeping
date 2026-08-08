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
        fun displayName(languageTag: String): String {
            return when (languageTag) {
                Constants.APP_LANGUAGE_RUSSIAN -> nameRu
                Constants.APP_LANGUAGE_POLISH -> namePl
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
