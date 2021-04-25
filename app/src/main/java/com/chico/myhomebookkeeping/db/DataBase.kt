package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.db.entity.*

@Database(
    entities = [
        CashAccount::class,
        Categories::class,
        Currencies::class,
        MoneyMovement::class,
    ],
    version = 1
)
abstract class DataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
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