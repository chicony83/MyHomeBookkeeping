package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.obj.RecentCategoriesPanel
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentCategoriesPanelTest {
    @Test
    fun moveToFrontAddsNewCategoryToStart() {
        val result = RecentCategoriesPanel.moveToFront(
            currentIds = listOf(2, 3, 4),
            categoryId = 1,
            limit = 10
        )

        assertEquals(listOf(1, 2, 3, 4), result)
    }

    @Test
    fun moveToFrontMovesExistingCategoryWithoutDuplicate() {
        val result = RecentCategoriesPanel.moveToFront(
            currentIds = listOf(1, 2, 3, 4),
            categoryId = 3,
            limit = 10
        )

        assertEquals(listOf(3, 1, 2, 4), result)
    }

    @Test
    fun moveToFrontTrimsToLimit() {
        val result = RecentCategoriesPanel.moveToFront(
            currentIds = listOf(1, 2, 3, 4, 5),
            categoryId = 6,
            limit = 5
        )

        assertEquals(listOf(6, 1, 2, 3, 4), result)
    }
}
