package com.chico.myhomebookkeeping

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryWithoutParentInstrumentedTest {
    @Test
    fun categoryCanBeCreatedAndChangedWithoutParent() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("DataBase")
        val database = dataBase.getDataBase(context)
        val parentId = database.parentCategoriesDao().addNewParentCategory(
            ParentCategories("Home", null)
        ).toInt()
        val categoryId = database.categoryDao().addCategory(
            Categories("Food", false, null, parentId)
        ).toInt()

        assertEquals(parentId, database.categoryDao().getOneCategory(categoryId).parentCategoryId)
        database.categoryDao().changeLineWithoutCategory(
            categoryId,
            "Food",
            false,
            R.drawable.no_image
        )
        assertNull(database.categoryDao().getOneCategory(categoryId).parentCategoryId)

        val categoryWithoutParentId = database.categoryDao().addCategory(
            Categories("Transport", false, null, null)
        ).toInt()
        assertNull(database.categoryDao().getOneCategory(categoryWithoutParentId).parentCategoryId)
        database.close()
    }
}
