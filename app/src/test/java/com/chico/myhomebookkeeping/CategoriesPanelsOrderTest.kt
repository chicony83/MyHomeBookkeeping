package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.obj.CategoriesPanelsOrder
import com.chico.myhomebookkeeping.obj.Constants
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoriesPanelsOrderTest {
    @Test
    fun moveToBottomPlacesRecentAfterFrequent() {
        val result = CategoriesPanelsOrder.moveToBottom(
            currentOrder = listOf(
                Constants.CATEGORIES_PANEL_RECENT,
                Constants.CATEGORIES_PANEL_FREQUENT
            ),
            panelKey = Constants.CATEGORIES_PANEL_RECENT
        )

        assertEquals(
            listOf(Constants.CATEGORIES_PANEL_FREQUENT, Constants.CATEGORIES_PANEL_RECENT),
            result
        )
    }

    @Test
    fun moveToBottomPlacesFrequentAfterRecent() {
        val result = CategoriesPanelsOrder.moveToBottom(
            currentOrder = listOf(
                Constants.CATEGORIES_PANEL_FREQUENT,
                Constants.CATEGORIES_PANEL_RECENT
            ),
            panelKey = Constants.CATEGORIES_PANEL_FREQUENT
        )

        assertEquals(
            listOf(Constants.CATEGORIES_PANEL_RECENT, Constants.CATEGORIES_PANEL_FREQUENT),
            result
        )
    }
}
