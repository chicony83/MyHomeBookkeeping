package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCategoryCatalogTest {

    @Test
    fun incomeAndProductsAreSelectedByDefault() {
        val selectedByDefault = DefaultCategoryCatalog.groups
            .filter { it.isSelectedByDefault }
            .map { it.parentName }

        assertEquals(listOf("Доходы", "Продукты"), selectedByDefault)
    }

    @Test
    fun onlyIncomeGroupIsIncome() {
        val incomeGroups = DefaultCategoryCatalog.groups
            .filter { it.isIncome }
            .map { it.parentName }

        assertEquals(listOf("Доходы"), incomeGroups)
    }

    @Test
    fun otherWithoutCategoryGroupIsNotInCatalog() {
        assertFalse(DefaultCategoryCatalog.groups.any { it.parentName == "Другое" })
        assertFalse(
            DefaultCategoryCatalog.groups.any {
                it.parentName == "Другое" && "Без категории" in it.subcategories
            }
        )
        assertTrue(DefaultCategoryCatalog.groups.all { it.subcategories.isNotEmpty() })
    }
}
