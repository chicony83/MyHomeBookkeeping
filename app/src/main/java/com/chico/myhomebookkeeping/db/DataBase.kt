package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chico.myhomebookkeeping.db.dao.CurrencyDao
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dao.SpendingDao
import com.chico.myhomebookkeeping.db.entity.Currency
import com.chico.myhomebookkeeping.db.entity.Income
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.db.entity.Spending

@Database(
    entities = [
        Income::class,
        Spending::class,
        Currency::class,

        MoneyMovement::class
    ],
    version = 1
)
abstract class DataBase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun spendingDao(): SpendingDao
    abstract fun currencyDao(): CurrencyDao
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