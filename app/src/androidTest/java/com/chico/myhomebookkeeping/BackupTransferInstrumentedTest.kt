package com.chico.myhomebookkeeping

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.backup.DatabaseBackupManager
import com.chico.myhomebookkeeping.backup.DatabaseRestoreManager
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.IconCategory
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
import com.chico.myhomebookkeeping.icons.IconResourceSynchronizer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackupTransferInstrumentedTest {
    private val password = "transfer-test-password".toCharArray()

    @Test
    fun seedSixOperationsAndCreateBackup() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)
        val database = dataBase.getDataBase(context)

        val currencyId = database.currenciesDao().addCurrency(
            Currencies("United States dollar", "$", "USD", null, true)
        ).toInt()
        val accountId = database.cashAccountDao().addCashAccount(
            CashAccount("Тестовый счёт", "", OLD_RESOURCE_ID, true)
        ).toInt()
        val incomeCategoryId = database.categoryDao().addCategory(
            Categories("Заработок", true, OLD_RESOURCE_ID, null)
        ).toInt()
        val expenseCategoryId = database.categoryDao().addCategory(
            Categories("Расходы", false, OLD_RESOURCE_ID, null)
        ).toInt()
        val iconCategoryId = database.iconCategoryDao().addNewIconCategory(
            IconCategory("Categories")
        ).toInt()
        database.iconResourcesDao().addNewIcon(
            IconsResource(CategoryIconNames.Car.name, iconCategoryId, OLD_RESOURCE_ID)
        )

        val operations = listOf(
            MoneyMovement(1_700_000_000_000, accountId, currencyId, incomeCategoryId, 100_000.0, "Заработок"),
            MoneyMovement(1_700_000_100_000, accountId, currencyId, expenseCategoryId, 1_200.0, "Продукты"),
            MoneyMovement(1_700_000_200_000, accountId, currencyId, expenseCategoryId, 2_500.0, "Коммунальные услуги"),
            MoneyMovement(1_700_000_300_000, accountId, currencyId, expenseCategoryId, 650.0, "Транспорт"),
            MoneyMovement(1_700_000_400_000, accountId, currencyId, expenseCategoryId, 3_100.0, "Лекарства"),
            MoneyMovement(1_700_000_500_000, accountId, currencyId, expenseCategoryId, 900.0, "Связь")
        )
        operations.forEach { database.moneyMovementDao().addMovingMoney(it) }
        database.close()

        IconResourceSynchronizer.synchronize(context)
        val synchronized = dataBase.getDataBase(context)
        assertEquals(
            R.drawable.category_car,
            synchronized.categoryDao().getOneCategory(incomeCategoryId).icon
        )
        assertEquals(
            R.drawable.category_car,
            synchronized.cashAccountDao().getOneCashAccount(accountId).icon
        )
        assertEquals(6, synchronized.moneyMovementDao().getAllMovingMoney().size)
        synchronized.close()

        val backup = File(context.filesDir, BACKUP_FILE)
        backup.delete()
        DatabaseBackupManager.createEncryptedBackup(context, Uri.fromFile(backup), password.copyOf())
        assertTrue(backup.isFile)
        assertTrue(backup.length() > 100)
    }

    @Test
    fun restoreAfterCleanInstallAndVerifySixOperations() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertFalse(context.getDatabasePath(DATABASE_NAME).exists())
        val backup = File(context.filesDir, BACKUP_FILE)
        assertTrue("Backup must be copied back after reinstall", backup.isFile)

        val wrongPasswordFailed = runCatching {
            DatabaseRestoreManager.stageRestore(
                context,
                Uri.fromFile(backup),
                "definitely-wrong".toCharArray()
            )
        }.isFailure
        assertTrue("Wrong password must be rejected", wrongPasswordFailed)

        DatabaseRestoreManager.stageRestore(context, Uri.fromFile(backup), password.copyOf())
        assertTrue(DatabaseRestoreManager.applyPendingRestore(context))
        IconResourceSynchronizer.synchronize(context)

        val database = dataBase.getDataBase(context)
        val operations = database.moneyMovementDao().getAllMovingMoney().sortedBy { it.timeStamp }
        val categories = database.categoryDao().getAllCategoriesSortIdASC()
            .associateBy { it.categoriesId }
        assertEquals(6, operations.size)
        assertEquals(1, operations.count { categories[it.category]?.isIncome == true })
        assertEquals(5, operations.count { categories[it.category]?.isIncome == false })
        assertEquals(108_350.0, operations.sumOf { it.amount }, 0.001)
        assertEquals(
            listOf("Заработок", "Продукты", "Коммунальные услуги", "Транспорт", "Лекарства", "Связь"),
            operations.map { it.description }
        )
        assertTrue(categories.values.all { it.icon == R.drawable.category_car })
        database.close()
    }

    companion object {
        const val DATABASE_NAME = "DataBase"
        const val BACKUP_FILE = "transfer-test.mhbk"
        const val OLD_RESOURCE_ID = 123_456_789
    }
}
