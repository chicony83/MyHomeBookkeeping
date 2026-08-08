package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.domain.DefaultCashAccountCatalog
import com.chico.myhomebookkeeping.enums.icon.names.CashAccountIconNames
import com.chico.myhomebookkeeping.obj.Constants
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultCashAccountCatalogTest {

    @Test
    fun defaultAccountsKeepCanonicalEnglishNames() {
        assertEquals(
            listOf("Card", "Cash"),
            DefaultCashAccountCatalog.accounts.map { it.canonicalName }
        )
    }

    @Test
    fun defaultAccountsHaveLocalizedDisplayNames() {
        val card = DefaultCashAccountCatalog.accounts.first { it.iconName == CashAccountIconNames.Card }
        val cash = DefaultCashAccountCatalog.accounts.first { it.iconName == CashAccountIconNames.Cash }

        assertEquals("Card", card.displayName(Constants.APP_LANGUAGE_ENGLISH))
        assertEquals("Карточка", card.displayName(Constants.APP_LANGUAGE_RUSSIAN))
        assertEquals("Karta", card.displayName(Constants.APP_LANGUAGE_POLISH))

        assertEquals("Cash", cash.displayName(Constants.APP_LANGUAGE_ENGLISH))
        assertEquals("Наличные", cash.displayName(Constants.APP_LANGUAGE_RUSSIAN))
        assertEquals("Gotówka", cash.displayName(Constants.APP_LANGUAGE_POLISH))
    }
}
