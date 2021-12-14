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
        BlankMoneyMovement::class
    ],
    version = 2,
    exportSchema = true,

)
abstract class DataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
    abstract fun blankMoneyMovementDao(): BlankMoneyMovementDao
    abstract fun iconsDao():IconsDao
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

object migration_1_2:Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'blanks_table' ('id' INTEGER , 'icon' INTEGER NOT NULL,'name' TEXT NOT NULL, 'cash_account' INTEGER NOT NULL, 'currency' INTEGER NOT NULL, 'category' INTEGER NOT NULL, 'description' TEXT NOT NULL, PRIMARY KEY('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icons_table' ( 'id' INTEGER , 'name' TEXT NOT NULL, 'icon_value' TEXT NOT NULL, PRIMARY KEY ('id'))")
    }
}
