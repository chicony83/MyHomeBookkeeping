package com.chico.myhomebookkeeping

import android.app.Application
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.enums.icon.names.CategoriesOfIconsNames
import com.chico.myhomebookkeeping.enums.icon.names.NoCategoryNames
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.update.Update44To45
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DuplicateSeedOnUpdateInstrumentedTest {
    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }
    private val preferences by lazy {
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    }

    @Test
    fun existingDatabaseIsNotTreatedAsFirstLaunchAfterUpdate() = runBlocking {
        context.deleteDatabase("DataBase")
        preferences.edit().clear().commit()

        val database = dataBase.getDataBase(context)
        database.cashAccountDao().addCashAccount(
            CashAccount("Card", "", R.drawable.no_image, true)
        )
        database.categoryDao().addCategory(
            Categories("Food", false, R.drawable.no_image, null)
        )
        database.currenciesDao().addCurrency(
            Currencies("Euro", "EUR", "EUR", null, true)
        )

        val cashAccountsBefore = database.cashAccountDao().getCashAccountsCount()
        val categoriesBefore = database.categoryDao().getCategoriesCount()
        val currenciesBefore = database.currenciesDao().getCurrenciesCount()

        val viewModel = MainActivityViewModel(context.applicationContext as Application)

        assertFalse(viewModel.checkIsFirstLaunch())
        assertEquals(cashAccountsBefore, database.cashAccountDao().getCashAccountsCount())
        assertEquals(categoriesBefore, database.categoryDao().getCategoriesCount())
        assertEquals(currenciesBefore, database.currenciesDao().getCurrenciesCount())
        database.close()
    }

    @Test
    fun update44To45DoesNotDuplicateIconDictionariesWhenRunAgain() = runBlocking {
        context.deleteDatabase("DataBase")

        val application = context.applicationContext as Application
        val update = Update44To45()
        update.update(application)

        val databaseAfterFirstRun = dataBase.getDataBase(context)
        val iconCategoriesAfterFirstRun = databaseAfterFirstRun.iconCategoryDao()
            .getAllIconCategories()
            .count { it.iconCategoryName == CategoriesOfIconsNames.IconHasNoCategory.name }
        val noImageIconsAfterFirstRun = databaseAfterFirstRun.iconResourcesDao()
            .getListIcons()
            .count { it.iconName == NoCategoryNames.NoImage.name }
        databaseAfterFirstRun.close()

        update.update(application)

        val databaseAfterSecondRun = dataBase.getDataBase(context)
        assertEquals(
            iconCategoriesAfterFirstRun,
            databaseAfterSecondRun.iconCategoryDao()
                .getAllIconCategories()
                .count { it.iconCategoryName == CategoriesOfIconsNames.IconHasNoCategory.name }
        )
        assertEquals(
            noImageIconsAfterFirstRun,
            databaseAfterSecondRun.iconResourcesDao()
                .getListIcons()
                .count { it.iconName == NoCategoryNames.NoImage.name }
        )
        databaseAfterSecondRun.close()
    }
}
