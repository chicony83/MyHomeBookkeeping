package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.obj.FrequentCategoriesPanel
import org.junit.Assert.assertEquals
import org.junit.Test

class FrequentCategoriesPanelTest {
    @Test
    fun categoriesSortByUsageCountDescending() {
        val result = FrequentCategoriesPanel.categories(
            sourceCategories = listOf(
                category(id = 1, name = "Low", usageCount = 2),
                category(id = 2, name = "High", usageCount = 9),
                category(id = 3, name = "Middle", usageCount = 4)
            ),
            limit = 10
        )

        assertEquals(listOf(2, 3, 1), result.map { it.categoriesId })
    }

    @Test
    fun categoriesIgnoreUnusedItems() {
        val result = FrequentCategoriesPanel.categories(
            sourceCategories = listOf(
                category(id = 1, name = "Unused", usageCount = 0),
                category(id = 2, name = "Used", usageCount = 1)
            ),
            limit = 10
        )

        assertEquals(listOf(2), result.map { it.categoriesId })
    }

    @Test
    fun categoriesTrimToLimit() {
        val result = FrequentCategoriesPanel.categories(
            sourceCategories = listOf(
                category(id = 1, name = "One", usageCount = 5),
                category(id = 2, name = "Two", usageCount = 4),
                category(id = 3, name = "Three", usageCount = 3)
            ),
            limit = 2
        )

        assertEquals(listOf(1, 2), result.map { it.categoriesId })
    }

    private fun category(id: Int, name: String, usageCount: Int): Categories {
        return Categories(
            categoryName = name,
            isIncome = false,
            icon = null,
            parentCategoryId = null,
            usageCount = usageCount
        ).apply {
            categoriesId = id
        }
    }
}
