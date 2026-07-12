package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.helpers.ParentCategoryHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParentCategoryHelperTest {
    @Test
    fun emptyListMeansNoParentCategory() {
        assertNull(ParentCategoryHelper.getIdSelectedParentCategory("", emptyList()))
    }

    @Test
    fun unknownNameMeansNoParentCategory() {
        assertNull(
            ParentCategoryHelper.getIdSelectedParentCategory(
                "No parent category",
                listOf(parentCategory(7, "Home"))
            )
        )
    }

    @Test
    fun existingParentCategoryReturnsItsId() {
        assertEquals(
            7,
            ParentCategoryHelper.getIdSelectedParentCategory(
                "Home",
                listOf(parentCategory(7, "Home"))
            )
        )
    }

    private fun parentCategory(id: Int, name: String) = ParentCategories(name, null).apply {
        this.id = id
    }
}
