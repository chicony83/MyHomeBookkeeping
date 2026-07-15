package com.chico.myhomebookkeeping

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.db.simpleQuery.MoneyMovingCreateSimpleQuery
import com.chico.myhomebookkeeping.db.simpleQuery.ReportsCreateSimpleQuery
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving.MoneyMovingCountMoney
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentTypeMigrationInstrumentedTest {

    @Test
    fun migrationFromVersion7KeepsOperationsAndAssignsPaymentTypes() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)

        createVersion7Database(context.getDatabasePath(DATABASE_NAME).absolutePath)

        val database = dataBase.getDataBase(context)
        val operations = database.moneyMovementDao().getAllMovingMoney().sortedBy { it.id }

        assertEquals(2, operations.size)
        assertEquals(PaymentTypeIds.INCOME, operations[0].paymentTypeId)
        assertEquals(PaymentTypeIds.SPENDING, operations[1].paymentTypeId)
        assertEquals(INCOME_CATEGORY_ID, operations[0].category)
        assertEquals(SPENDING_CATEGORY_ID, operations[1].category)

        val paymentTypes = database.openHelper.readableDatabase.query(
            "SELECT id, payment_type_code FROM payment_type_table ORDER BY id"
        )
        paymentTypes.use {
            assertEquals(3, it.count)
            assertTrue(it.moveToFirst())
            assertEquals(PaymentTypeIds.INCOME, it.getInt(0))
            assertEquals("income", it.getString(1))
            assertTrue(it.moveToNext())
            assertEquals(PaymentTypeIds.SPENDING, it.getInt(0))
            assertEquals("spending", it.getString(1))
            assertTrue(it.moveToNext())
            assertEquals(PaymentTypeIds.TRANSFER, it.getInt(0))
            assertEquals("transfer", it.getString(1))
        }

        database.close()
    }

    @Test
    fun regularPaymentWithoutCategoryIsRejectedAfterMigration() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)

        createVersion7Database(context.getDatabasePath(DATABASE_NAME).absolutePath)

        val database = dataBase.getDataBase(context)
        val failed = runCatching {
            database.openHelper.writableDatabase.execSQL(
                "INSERT INTO money_moving_table " +
                        "(time_stamp, cash_account, currency, category, payment_type_id, amount, description) " +
                        "VALUES (1700000200000, $ACCOUNT_ID, $CURRENCY_ID, NULL, ${PaymentTypeIds.SPENDING}, 50.0, 'broken')"
            )
        }.exceptionOrNull()

        assertTrue(failed is SQLiteConstraintException)
        database.close()
    }

    @Test
    fun transferRowsCanUseNullCategoryAndAreLinkedByGroup() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)

        val database = dataBase.getDataBase(context)
        val currencyId = database.currenciesDao().addCurrency(
            Currencies("United States dollar", "USD", "USD", null, true)
        ).toInt()
        val sourceAccountId = database.cashAccountDao().addCashAccount(
            CashAccount("Cash", "", null, true)
        ).toInt()
        val destinationAccountId = database.cashAccountDao().addCashAccount(
            CashAccount("Card", "", null, false)
        ).toInt()
        val categoryId = database.categoryDao().addCategory(
            Categories("Food", false, null, null)
        ).toInt()
        val transferGroupId = 999L

        database.moneyMovementDao().addTransfer(
            MoneyMovement(
                1_700_000_000_000,
                sourceAccountId,
                currencyId,
                null,
                PaymentTypeIds.TRANSFER,
                100.0,
                "transfer",
                transferGroupId,
                PaymentTypeIds.TRANSFER_DIRECTION_FROM
            ),
            MoneyMovement(
                1_700_000_000_000,
                destinationAccountId,
                currencyId,
                null,
                PaymentTypeIds.TRANSFER,
                100.0,
                "transfer",
                transferGroupId,
                PaymentTypeIds.TRANSFER_DIRECTION_TO
            )
        )

        val operations = database.moneyMovementDao().getAllMovingMoney()
        assertEquals(2, operations.size)
        assertTrue(operations.all { it.category == null })
        assertTrue(operations.all { it.paymentTypeId == PaymentTypeIds.TRANSFER })
        assertTrue(operations.all { it.transferGroupId == transferGroupId })

        val invalidTransfer = runCatching {
            database.moneyMovementDao().addMovingMoney(
                MoneyMovement(
                    1_700_000_100_000,
                    sourceAccountId,
                    currencyId,
                    categoryId,
                    PaymentTypeIds.TRANSFER,
                    10.0,
                    "broken transfer",
                    1000L,
                    PaymentTypeIds.TRANSFER_DIRECTION_FROM
                )
            )
        }.exceptionOrNull()
        assertTrue(invalidTransfer is SQLiteConstraintException)

        database.close()
    }

    @Test
    fun freshDatabaseStoresPaymentsTransfersAndKeepsQueriesConsistent() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)

        val database = dataBase.getDataBase(context)
        val currencyId = database.currenciesDao().addCurrency(
            Currencies("Euro", "EUR", "EUR", null, true)
        ).toInt()
        val walletId = database.cashAccountDao().addCashAccount(
            CashAccount("Wallet", "", null, true)
        ).toInt()
        val cardId = database.cashAccountDao().addCashAccount(
            CashAccount("Card", "", null, false)
        ).toInt()
        val salaryCategoryId = database.categoryDao().addCategory(
            Categories("Salary", true, null, null)
        ).toInt()
        val foodCategoryId = database.categoryDao().addCategory(
            Categories("Food", false, null, null)
        ).toInt()

        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(
                1_700_000_000_000,
                walletId,
                currencyId,
                salaryCategoryId,
                PaymentTypeIds.INCOME,
                1_000.0,
                "salary"
            )
        )
        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(
                1_700_000_100_000,
                walletId,
                currencyId,
                foodCategoryId,
                PaymentTypeIds.SPENDING,
                200.0,
                "food"
            )
        )
        database.moneyMovementDao().addTransfer(
            MoneyMovement(
                1_700_000_200_000,
                walletId,
                currencyId,
                null,
                PaymentTypeIds.TRANSFER,
                100.0,
                "cash to card",
                700L,
                PaymentTypeIds.TRANSFER_DIRECTION_FROM
            ),
            MoneyMovement(
                1_700_000_200_000,
                cardId,
                currencyId,
                null,
                PaymentTypeIds.TRANSFER,
                100.0,
                "cash to card",
                700L,
                PaymentTypeIds.TRANSFER_DIRECTION_TO
            )
        )

        database.openHelper.readableDatabase.query("PRAGMA integrity_check").use {
            assertTrue(it.moveToFirst())
            assertEquals("ok", it.getString(0))
        }

        val journal = database.moneyMovementDao().getSelectedFullMoneyMoving(
            MoneyMovingCreateSimpleQuery.createQueryList(
                currencyVal = -1,
                categoryVal = -1,
                cashAccountVal = -1,
                incomeSpendingSP = "",
                startTimePeriodLongSP = -1,
                endTimePeriodLongSP = -1
            )
        )
        assertEquals(4, journal.size)
        assertEquals(2, journal.count { it.paymentTypeId == PaymentTypeIds.TRANSFER })
        assertTrue(journal.filter { it.paymentTypeId == PaymentTypeIds.TRANSFER }
            .all { it.categoryNameValue == null && it.transferGroupId == 700L })
        assertEquals("1000.0", MoneyMovingCountMoney(journal).getIncome())
        assertEquals("-200.0", MoneyMovingCountMoney(journal).getSpending())
        assertEquals("800.0", MoneyMovingCountMoney(journal).getBalance())

        val walletJournal = database.moneyMovementDao().getSelectedFullMoneyMoving(
            MoneyMovingCreateSimpleQuery.createQueryList(
                currencyVal = -1,
                categoryVal = -1,
                cashAccountVal = walletId,
                incomeSpendingSP = "",
                startTimePeriodLongSP = -1,
                endTimePeriodLongSP = -1
            )
        )
        assertEquals("700.0", MoneyMovingCountMoney(walletJournal).getBalance())

        val cardJournal = database.moneyMovementDao().getSelectedFullMoneyMoving(
            MoneyMovingCreateSimpleQuery.createQueryList(
                currencyVal = -1,
                categoryVal = -1,
                cashAccountVal = cardId,
                incomeSpendingSP = "",
                startTimePeriodLongSP = -1,
                endTimePeriodLongSP = -1
            )
        )
        assertEquals("100.0", MoneyMovingCountMoney(cardJournal).getBalance())

        val reportsRows = database.moneyMovementDao().getSelectedMoneyMoving(
            ReportsCreateSimpleQuery.createSampleQueryForReports(
                startTimePeriodLong = -1,
                endTimePeriodLong = -1,
                setItemsOfCategories = setOf(salaryCategoryId, foodCategoryId),
                numbersOfAllCategories = 2
            )
        )
        assertEquals(2, reportsRows.size)
        assertTrue(reportsRows.none { it.paymentTypeId == PaymentTypeIds.TRANSFER })

        database.close()
    }

    private fun createVersion7Database(path: String) {
        val db = SQLiteDatabase.openOrCreateDatabase(path, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS `cash_account_table` (`cash_account_name` TEXT NOT NULL, `cash_account_number` TEXT NOT NULL, `icon_cash_account` INTEGER, `is_cash_account_default` INTEGER, `cashAccountId` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `category_table` (`category_name` TEXT NOT NULL, `is_income` INTEGER NOT NULL, `icon_category` INTEGER, `parent_category_id` INTEGER, `category_order` INTEGER NOT NULL, `categoriesId` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `currency_table` (`currency_name` TEXT NOT NULL, `currency_name_short` TEXT, `iso_4217` TEXT, `icon_currency` INTEGER, `is_currency_default` INTEGER, `currencyId` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `money_moving_table` (`time_stamp` INTEGER NOT NULL, `cash_account` INTEGER NOT NULL, `currency` INTEGER NOT NULL, `category` INTEGER NOT NULL, `amount` REAL NOT NULL, `description` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `fast_payments_table` (`icon` INTEGER, `name_fast_payment` TEXT NOT NULL, `rating` INTEGER NOT NULL, `cash_account` INTEGER NOT NULL, `currency` INTEGER NOT NULL, `category` INTEGER NOT NULL, `amount` REAL, `description` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `parent_categories_table` (`parent_category_name` TEXT NOT NULL, `name_icon_parent_category` INTEGER, `parent_category_order` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `icon_resource_table` (`icon_name` TEXT NOT NULL, `icon_category` INTEGER, `icon_resource` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `icon_category_table` (`name` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
        db.execSQL("INSERT INTO cash_account_table (cashAccountId, cash_account_name, cash_account_number, icon_cash_account, is_cash_account_default) VALUES ($ACCOUNT_ID, 'Card', '', NULL, 1)")
        db.execSQL("INSERT INTO currency_table (currencyId, currency_name, currency_name_short, iso_4217, icon_currency, is_currency_default) VALUES ($CURRENCY_ID, 'United States dollar', 'USD', 'USD', NULL, 1)")
        db.execSQL("INSERT INTO category_table (categoriesId, category_name, is_income, icon_category, parent_category_id, category_order) VALUES ($INCOME_CATEGORY_ID, 'Salary', 1, NULL, NULL, 1)")
        db.execSQL("INSERT INTO category_table (categoriesId, category_name, is_income, icon_category, parent_category_id, category_order) VALUES ($SPENDING_CATEGORY_ID, 'Food', 0, NULL, NULL, 2)")
        db.execSQL("INSERT INTO money_moving_table (id, time_stamp, cash_account, currency, category, amount, description) VALUES (1, 1700000000000, $ACCOUNT_ID, $CURRENCY_ID, $INCOME_CATEGORY_ID, 100.0, 'salary')")
        db.execSQL("INSERT INTO money_moving_table (id, time_stamp, cash_account, currency, category, amount, description) VALUES (2, 1700000100000, $ACCOUNT_ID, $CURRENCY_ID, $SPENDING_CATEGORY_ID, 25.0, 'food')")
        db.version = 7
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "DataBase"
        private const val ACCOUNT_ID = 1
        private const val CURRENCY_ID = 1
        private const val INCOME_CATEGORY_ID = 1
        private const val SPENDING_CATEGORY_ID = 2
    }
}
