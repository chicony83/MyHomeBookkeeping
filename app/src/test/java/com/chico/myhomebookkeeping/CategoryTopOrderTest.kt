package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.ui.categories.categories.TOP_ADD_PARENT
import com.chico.myhomebookkeeping.ui.categories.categories.TOP_PARENT_PREFIX
import com.chico.myhomebookkeeping.ui.categories.categories.TOP_WITHOUT_PARENT
import com.chico.myhomebookkeeping.ui.categories.categories.moveTopKey
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryTopOrderTest {

    @Test
    fun parentCategoryCanMoveDownPastExpandedGroupRows() {
        val parentOne = TOP_PARENT_PREFIX + 1
        val parentTwo = TOP_PARENT_PREFIX + 2
        val parentThree = TOP_PARENT_PREFIX + 3

        val result = moveTopKey(
            topOrder = listOf(parentOne, parentTwo, parentThree, TOP_WITHOUT_PARENT, TOP_ADD_PARENT),
            fromKey = parentOne,
            toKey = parentThree
        )

        assertEquals(
            listOf(parentTwo, parentThree, parentOne, TOP_WITHOUT_PARENT, TOP_ADD_PARENT),
            result
        )
    }

    @Test
    fun parentCategoryCanMoveUpBeforeExpandedGroupRows() {
        val parentOne = TOP_PARENT_PREFIX + 1
        val parentTwo = TOP_PARENT_PREFIX + 2
        val parentThree = TOP_PARENT_PREFIX + 3

        val result = moveTopKey(
            topOrder = listOf(parentOne, parentTwo, parentThree, TOP_WITHOUT_PARENT, TOP_ADD_PARENT),
            fromKey = parentThree,
            toKey = parentOne
        )

        assertEquals(
            listOf(parentThree, parentOne, parentTwo, TOP_WITHOUT_PARENT, TOP_ADD_PARENT),
            result
        )
    }
}
