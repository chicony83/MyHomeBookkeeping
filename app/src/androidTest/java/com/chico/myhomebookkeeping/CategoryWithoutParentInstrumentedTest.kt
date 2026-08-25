package com.chico.myhomebookkeeping

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
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

    @Test
    fun categoryUsageCountCanIncrementAndDoesNotGoBelowZero() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("DataBase")
        val database = dataBase.getDataBase(context)
        val categoryId = database.categoryDao().addCategory(
            Categories("Food", false, null, null)
        ).toInt()

        database.categoryDao().incrementUsageCount(categoryId)
        assertEquals(1, database.categoryDao().getOneCategory(categoryId).usageCount)

        database.categoryDao().decrementUsageCount(categoryId)
        database.categoryDao().decrementUsageCount(categoryId)
        assertEquals(0, database.categoryDao().getOneCategory(categoryId).usageCount)

        database.close()
    }

    @Test
    fun categoryUsageCountCanBeRebuiltFromRegularPaymentsOnly() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("DataBase")
        val database = dataBase.getDataBase(context)
        val currencyId = database.currenciesDao().addCurrency(
            Currencies("Euro", "EUR", "EUR", null, true)
        ).toInt()
        val cashAccountId = database.cashAccountDao().addCashAccount(
            CashAccount("Cash", "", null, true)
        ).toInt()
        val foodCategoryId = database.categoryDao().addCategory(
            Categories("Food", false, null, null)
        ).toInt()
        val transferFeeCategoryId = database.categoryDao().addCategory(
            Categories("Transfer fee", false, null, null)
        ).toInt()

        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(
                1_700_000_000_000,
                cashAccountId,
                currencyId,
                foodCategoryId,
                PaymentTypeIds.SPENDING,
                10.0,
                "food"
            )
        )
        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(
                1_700_000_100_000,
                cashAccountId,
                currencyId,
                transferFeeCategoryId,
                PaymentTypeIds.SPENDING,
                1.0,
                "transfer fee"
            )
        )

        database.categoryDao().rebuildUsageCount()
        database.categoryDao().clearTransferFeeUsageCount()

        assertEquals(1, database.categoryDao().getOneCategory(foodCategoryId).usageCount)
        assertEquals(0, database.categoryDao().getOneCategory(transferFeeCategoryId).usageCount)

        database.close()
    }
}
