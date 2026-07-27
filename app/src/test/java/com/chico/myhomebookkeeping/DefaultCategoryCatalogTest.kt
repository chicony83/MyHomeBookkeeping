package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCategoryCatalogTest {

    @Test
    fun allGroupsAreSelectedByDefault() {
        assertTrue(DefaultCategoryCatalog.groups.all { it.isSelectedByDefault })
    }

    @Test
    fun incomeAndProductsAreRequired() {
        val requiredByDefault = DefaultCategoryCatalog.groups
            .filter { it.isRequired }

        assertEquals(DefaultCategoryCatalog.groups.take(2), requiredByDefault)
    }

    @Test
    fun onlyIncomeGroupIsIncome() {
        val incomeGroups = DefaultCategoryCatalog.groups
            .filter { it.isIncome }
            .map { it.parentName }

        assertEquals(listOf("Income"), incomeGroups)
    }

    @Test
    fun otherWithoutCategoryGroupIsNotInCatalog() {
        assertFalse(DefaultCategoryCatalog.groups.any { it.parentName == "Other" })
        assertFalse(
            DefaultCategoryCatalog.groups.any {
                it.parentName == "Other" && "No category" in it.subcategories
            }
        )
        assertTrue(DefaultCategoryCatalog.groups.all { it.subcategories.isNotEmpty() })
    }

    @Test
    fun russianCatalogIsAvailableForRussianLanguage() {
        val groups = DefaultCategoryCatalog.groupsForLanguage("ru")

        assertEquals("Доходы", groups.first().parentName)
        assertTrue(groups.first().subcategories.contains("Другое"))
    }
}
