package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chico.myhomebookkeeping.db.dao.BankAccountDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.BankAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

@Database(
    entities = [
        BankAccount::class,
        Categories::class,
        Currencies::class,
        MoneyMovement::class
    ],
    version = 1
)
abstract class DataBase : RoomDatabase() {
    abstract fun bankAccountDao():BankAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
}

object dataBase {
    fun getDataBase(ctx: Context) =
        Room.databaseBuilder(
            ctx,
            DataBase::class.java,
            "DataBase"
        ).build()
}