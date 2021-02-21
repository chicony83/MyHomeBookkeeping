package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.entity.Income

@Database(entities = [Income::class], version = 1)
abstract class IncomeCategoryDB : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
}

object incomeCategoryDB {
    fun getCategoryDB(ctx: Context) =
        Room.databaseBuilder(
            ctx,
            IncomeCategoryDB::class.java,
            "IncomeMoneyCategoryDB"
        ).build()
}