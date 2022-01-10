package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.db.entity.*

@Database(
    entities = [
        CashAccount::class,
        Categories::class,
        Currencies::class,
        MoneyMovement::class,
        FastPayments::class,
        Icons::class,
        ParentCategories::class
    ],
    version = 3,
    exportSchema = true,

    )
abstract class DataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
    abstract fun fastPaymentsDao(): FastPaymentsDao
    abstract fun iconsDao(): IconsDao
    abstract fun parentCategories():ParentCategories
}

object dataBase {
    fun getDataBase(ctx: Context) =
        Room.databaseBuilder(
            ctx,
            DataBase::class.java,
            "DataBase"
        )
            .addMigrations(migration_1_2)
            .addMigrations(migration_2_to_3)
            .build()
}

private object migration_1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'fast_payments_table' ('id' INTEGER , 'icon' INTEGER, 'name_fast_payment' TEXT NOT NULL,'rating' INTEGER NOT NULL, 'cash_account' INTEGER NOT NULL, 'currency' INTEGER NOT NULL, 'category' INTEGER NOT NULL, 'amount' REAL,'description' TEXT, PRIMARY KEY('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icons_table' ( 'id' INTEGER , 'name' TEXT NOT NULL, 'icon_value' TEXT NOT NULL, PRIMARY KEY ('id'))")
    }
}

private object migration_2_to_3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'parent_categories_table' ('id' INTEGER, 'name' TEXT NOT NULL, 'icon' INTEGER, PRIMARY KEY ('id'))")
        database.execSQL("ALTER TABLE 'currency_table' ADD COLUMN 'icon' INTEGER ")
        database.execSQL("ALTER TABLE 'category_table' ADD COLUMN 'icon' INTEGER ")
        database.execSQL("ALTER TABLE 'cash_account_table' ADD COLUMN 'icon' INTEGER ")
    }

}