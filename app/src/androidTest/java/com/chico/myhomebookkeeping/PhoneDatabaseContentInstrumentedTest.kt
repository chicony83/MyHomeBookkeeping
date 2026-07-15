package com.chico.myhomebookkeeping

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PhoneDatabaseContentInstrumentedTest {

    @Test
    fun seedPhoneDatabaseAndDumpEditedTables() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DATABASE_NAME)

        val database = dataBase.getDataBase(context)
        val currencyId = database.currenciesDao().addCurrency(
            Currencies("Euro", "EUR", "EUR", null, true)
        ).toInt()
        val cashId = database.cashAccountDao().addCashAccount(
            CashAccount("Cash", "", null, true)
        ).toInt()
        val cardId = database.cashAccountDao().addCashAccount(
            CashAccount("Card", "", null, false)
        ).toInt()
        val savingsId = database.cashAccountDao().addCashAccount(
            CashAccount("Savings", "", null, false)
        ).toInt()
        val salaryId = database.categoryDao().addCategory(
            Categories("Salary", true, null, null)
        ).toInt()
        val giftId = database.categoryDao().addCategory(
            Categories("Gift", true, null, null)
        ).toInt()
        val foodId = database.categoryDao().addCategory(
            Categories("Food", false, null, null)
        ).toInt()
        val transportId = database.categoryDao().addCategory(
            Categories("Transport", false, null, null)
        ).toInt()

        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(1_700_001_000_000, cashId, currencyId, salaryId, PaymentTypeIds.INCOME, 1500.0, "salary")
        )
        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(1_700_001_100_000, cardId, currencyId, giftId, PaymentTypeIds.INCOME, 200.0, "gift")
        )
        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(1_700_001_200_000, cardId, currencyId, foodId, PaymentTypeIds.SPENDING, 120.0, "food")
        )
        database.moneyMovementDao().addMovingMoney(
            MoneyMovement(1_700_001_300_000, cashId, currencyId, transportId, PaymentTypeIds.SPENDING, 30.0, "transport")
        )
        database.moneyMovementDao().addTransfer(
            MoneyMovement(1_700_001_400_000, cashId, currencyId, null, PaymentTypeIds.TRANSFER, 300.0, "cash to card", 9001L, PaymentTypeIds.TRANSFER_DIRECTION_FROM),
            MoneyMovement(1_700_001_400_000, cardId, currencyId, null, PaymentTypeIds.TRANSFER, 300.0, "cash to card", 9001L, PaymentTypeIds.TRANSFER_DIRECTION_TO)
        )
        database.moneyMovementDao().addTransfer(
            MoneyMovement(1_700_001_500_000, cardId, currencyId, null, PaymentTypeIds.TRANSFER, 50.0, "card to savings", 9002L, PaymentTypeIds.TRANSFER_DIRECTION_FROM),
            MoneyMovement(1_700_001_500_000, savingsId, currencyId, null, PaymentTypeIds.TRANSFER, 50.0, "card to savings", 9002L, PaymentTypeIds.TRANSFER_DIRECTION_TO)
        )

        val dump = StringBuilder()
        database.openHelper.readableDatabase.query("PRAGMA integrity_check").use {
            assertTrue(it.moveToFirst())
            val integrity = it.getString(0)
            assertEquals("ok", integrity)
            dump.appendLine("integrity_check=$integrity")
        }
        database.openHelper.readableDatabase.query("PRAGMA user_version").use {
            assertTrue(it.moveToFirst())
            dump.appendLine("user_version=${it.getInt(0)}")
        }
        appendQueryDump(database, dump, "payment_type_table") {
            "SELECT id, payment_type_code, payment_type_name, affects_reports, requires_category, requires_second_account FROM payment_type_table ORDER BY id"
        }
        appendQueryDump(database, dump, "money_moving_table") {
            "SELECT id, cash_account, currency, category, payment_type_id, amount, description, transfer_group_id, transfer_direction FROM money_moving_table ORDER BY id"
        }
        appendQueryDump(database, dump, "joined_check") {
            "SELECT m.id, ca.cash_account_name, c.category_name, pt.payment_type_code, m.amount, m.description, m.transfer_group_id, m.transfer_direction " +
                    "FROM money_moving_table m " +
                    "JOIN cash_account_table ca ON m.cash_account = ca.cashAccountId " +
                    "JOIN payment_type_table pt ON m.payment_type_id = pt.id " +
                    "LEFT JOIN category_table c ON m.category = c.categoriesId " +
                    "ORDER BY m.id"
        }
        appendQueryDump(database, dump, "report_visible_rows") {
            "SELECT m.id, c.category_name, pt.payment_type_code, m.amount " +
                    "FROM money_moving_table m " +
                    "JOIN category_table c ON m.category = c.categoriesId " +
                    "JOIN payment_type_table pt ON m.payment_type_id = pt.id " +
                    "WHERE m.payment_type_id IN (0, 1) ORDER BY m.id"
        }

        val rows = database.moneyMovementDao().getAllMovingMoney()
        assertEquals(8, rows.size)
        assertEquals(2, rows.count { it.paymentTypeId == PaymentTypeIds.INCOME })
        assertEquals(2, rows.count { it.paymentTypeId == PaymentTypeIds.SPENDING })
        assertEquals(4, rows.count { it.paymentTypeId == PaymentTypeIds.TRANSFER && it.category == null })

        File(context.filesDir, DUMP_FILE).writeText(dump.toString())
        database.close()
    }

    private fun appendQueryDump(
        database: com.chico.myhomebookkeeping.db.RoomDataBase,
        dump: StringBuilder,
        title: String,
        sql: () -> String
    ) {
        dump.appendLine()
        dump.appendLine("[$title]")
        database.openHelper.readableDatabase.query(sql()).use { cursor ->
            while (cursor.moveToNext()) {
                val values = (0 until cursor.columnCount).joinToString(" | ") { index ->
                    cursor.getString(index) ?: "NULL"
                }
                dump.appendLine(values)
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "DataBase"
        const val DUMP_FILE = "phone-db-check.txt"
    }
}
