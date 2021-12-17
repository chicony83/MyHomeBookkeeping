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
        Icons::class
    ],
    version = 2,
    exportSchema = true,

    )
abstract class DataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
    abstract fun fastPaymentsDao(): FastPaymentsDao
    abstract fun iconsDao(): IconsDao
}

object dataBase {
    fun getDataBase(ctx: Context) =
        Room.databaseBuilder(
            ctx,
            DataBase::class.java,
            "DataBase"
        )
            .addMigrations(migration_1_2)
            .build()
}

private object migration_1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'fast_payments_table' ('id' INTEGER , 'icon' INTEGER, 'about_fast_payment' TEXT NOT NULL,'rating' INTEGER NOT NULL, 'cash_account' INTEGER NOT NULL, 'currency' INTEGER NOT NULL, 'category' INTEGER NOT NULL, 'amount' REAL,'description' TEXT, PRIMARY KEY('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icons_table' ( 'id' INTEGER , 'name' TEXT NOT NULL, 'icon_value' TEXT NOT NULL, PRIMARY KEY ('id'))")
    }
}