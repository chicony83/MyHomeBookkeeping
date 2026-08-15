package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportsCategorySelectionTest {

    @Test
    fun reportCategorySelectionAddsGroupForCategoriesWithoutParent() {
        val parentCategory = ParentCategories("Home", null).apply { id = 10 }
        val rent = category(id = 1, name = "Rent", isIncome = false, parentCategoryId = 10)
        val bonus = category(id = 2, name = "Bonus", isIncome = true, parentCategoryId = null)
        val groceries = category(id = 3, name = "Groceries", isIncome = false, parentCategoryId = null)

        val items = ConvToList.parentCategoriesListToReportsItemsList(
            parentCategoriesList = listOf(parentCategory),
            categoriesList = listOf(rent, bonus, groceries),
            noParentCategoryName = "No parent category"
        )

        assertEquals(2, items.size)
        assertEquals("Home", items[0].name)
        assertEquals(setOf(1), items[0].categoryIds)

        val withoutParentItem = items[1]
        assertEquals("No parent category", withoutParentItem.name)
        assertEquals(setOf(2, 3), withoutParentItem.categoryIds)
        assertEquals(setOf(2), withoutParentItem.incomeCategoryIds)
        assertEquals(setOf(3), withoutParentItem.spendingCategoryIds)
        assertFalse(withoutParentItem.isChecked)
    }

    @Test
    fun reportCategorySelectionSkipsGroupWhenNoCategoriesAreWithoutParent() {
        val parentCategory = ParentCategories("Home", null).apply { id = 10 }
        val rent = category(id = 1, name = "Rent", isIncome = false, parentCategoryId = 10)

        val items = ConvToList.parentCategoriesListToReportsItemsList(
            parentCategoriesList = listOf(parentCategory),
            categoriesList = listOf(rent),
            noParentCategoryName = "No parent category"
        )

        assertEquals(1, items.size)
        assertTrue(items.none { it.name == "No parent category" })
    }

    private fun category(
        id: Int,
        name: String,
        isIncome: Boolean,
        parentCategoryId: Int?
    ): Categories {
        return Categories(
            categoryName = name,
            isIncome = isIncome,
            icon = null,
            parentCategoryId = parentCategoryId
        ).apply {
            categoriesId = id
        }
    }
}
