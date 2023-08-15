package com.chico.myhomebookkeeping.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.db.typeconverter.DbConverters

@Database(
    entities = [
        CashAccount::class,
        Categories::class,
        Currencies::class,
        MoneyMovement::class,
        FastPayments::class,
//        Icons::class,
        ParentCategory::class,
        UserParentCategory::class,
        IconsResource::class,
        IconCategory::class,
        ChildCategory::class
    ],
    version = 6,
    exportSchema = true,
)
@TypeConverters(DbConverters::class)
abstract class RoomDataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
    abstract fun fastPaymentsDao(): FastPaymentsDao

    //    abstract fun iconsDao(): IconsDao
    abstract fun parentCategoriesDao(): ParentCategoriesDao
    abstract fun childCategoriesDao(): ChildCategoriesDao
    abstract fun userParentCategoriesDao(): UserParentCategoriesDao
    abstract fun iconResourcesDao(): IconResourcesDao
    abstract fun iconCategoryDao(): IconCategoryDao
}

object dataBase {
    fun getDataBase(ctx: Context) =
        Room.databaseBuilder(
            ctx,
            RoomDataBase::class.java,
            "DataBase"
        )
            .addMigrations(migration_1_to_2)
            .addMigrations(migration_2_to_3)
            .addMigrations(migration_3_to_4)
            .addMigrations(migration_4_to_5)
            .fallbackToDestructiveMigration()
            .build()
}

private object migration_1_to_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'fast_payments_table' ('id' INTEGER , 'icon' INTEGER, 'name_fast_payment' TEXT NOT NULL,'rating' INTEGER NOT NULL, 'cash_account' INTEGER NOT NULL, 'currency' INTEGER NOT NULL, 'category' INTEGER NOT NULL, 'amount' REAL,'description' TEXT, PRIMARY KEY('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icons_table' ( 'id' INTEGER , 'name' TEXT NOT NULL, 'icon_value' TEXT NOT NULL, PRIMARY KEY ('id'))")
    }
}

private object migration_2_to_3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icon_category_table' ('id' INTEGER, 'name' TEXT NOT NULL, PRIMARY KEY('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'parent_categories_table' ('id' INTEGER, 'name' TEXT NOT NULL, 'icon_parent_category' INTEGER, PRIMARY KEY ('id'))")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'icon_resource_table' ('id' INTEGER, 'icon_name'TEXT NOT NULL, 'icon_category' INTEGER, 'icon_resource' INTEGER NOT NULL, PRIMARY KEY ('id') ) ")
        database.execSQL("ALTER TABLE 'currency_table' ADD COLUMN 'icon_currency' INTEGER ")
        database.execSQL("ALTER TABLE 'category_table' ADD COLUMN 'icon_category' INTEGER ")
        database.execSQL("ALTER TABLE 'cash_account_table' ADD COLUMN 'icon_cash_account' INTEGER ")
        database.execSQL("DROP TABLE 'icons_table'")
    }
}

private object migration_3_to_4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'currency_table' ADD COLUMN 'is_currency_default' INTEGER ")
        database.execSQL("ALTER TABLE 'cash_account_table' ADD COLUMN 'is_cash_account_default' INTEGER ")
    }
}

private object migration_4_to_5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'currency_table' ADD COLUMN 'currency_name_short' TEXT")
        database.execSQL("ALTER TABLE 'currency_table' ADD COLUMN 'iso_4217' TEXT")
    }
}
