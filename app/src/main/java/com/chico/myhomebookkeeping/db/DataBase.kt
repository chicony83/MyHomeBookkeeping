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
//        Icons::class,
        ParentCategories::class,
        IconsResource::class,
        IconCategory::class,
        PaymentType::class
    ],
    version = 8,
    exportSchema = true,
)
abstract class RoomDataBase : RoomDatabase() {
    abstract fun cashAccountDao(): CashAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun moneyMovementDao(): MoneyMovementDao
    abstract fun fastPaymentsDao(): FastPaymentsDao

    //    abstract fun iconsDao(): IconsDao
    abstract fun parentCategoriesDao(): ParentCategoriesDao
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
            .addMigrations(migration_5_to_6)
            .addMigrations(migration_6_to_7)
            .addMigrations(migration_7_to_8)
            .addCallback(seedPaymentTypesOnCreate)
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
private object migration_5_to_6:Migration(5,6){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE 'parent_categories_table'")
        database.execSQL("CREATE TABLE IF NOT EXISTS 'parent_categories_table' ('id' INTEGER, 'name' TEXT NOT NULL, 'name_icon_parent_category' INTEGER, PRIMARY KEY ('id'))")
        database.execSQL("ALTER TABLE 'category_table' ADD COLUMN 'parent_category_id' INTEGER")
    }
}

private object migration_6_to_7 : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'category_table' ADD COLUMN 'category_order' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("UPDATE category_table SET category_order = categoriesId")
        database.execSQL("ALTER TABLE 'parent_categories_table' ADD COLUMN 'parent_category_order' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("UPDATE parent_categories_table SET parent_category_order = id")
    }
}

private object migration_7_to_8 : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        createPaymentTypeTable(database)
        seedPaymentTypes(database)

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `money_moving_table_new` (" +
                    "`time_stamp` INTEGER NOT NULL, " +
                    "`cash_account` INTEGER NOT NULL, " +
                    "`currency` INTEGER NOT NULL, " +
                    "`category` INTEGER, " +
                    "`payment_type_id` INTEGER NOT NULL, " +
                    "`amount` REAL NOT NULL, " +
                    "`description` TEXT NOT NULL, " +
                    "`transfer_group_id` INTEGER, " +
                    "`transfer_direction` INTEGER, " +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "CHECK ((" +
                    "`payment_type_id` IN (0, 1) " +
                    "AND `category` IS NOT NULL " +
                    "AND `transfer_group_id` IS NULL " +
                    "AND `transfer_direction` IS NULL" +
                    ") OR (" +
                    "`payment_type_id` = 2 " +
                    "AND `category` IS NULL " +
                    "AND `transfer_group_id` IS NOT NULL " +
                    "AND `transfer_direction` IN (0, 1)" +
                    ")))"
        )
        database.execSQL(
            "INSERT INTO `money_moving_table_new` (" +
                    "`time_stamp`, `cash_account`, `currency`, `category`, " +
                    "`payment_type_id`, `amount`, `description`, `id`" +
                    ") " +
                    "SELECT money_moving_table.`time_stamp`, " +
                    "money_moving_table.`cash_account`, " +
                    "money_moving_table.`currency`, " +
                    "money_moving_table.`category`, " +
                    "CASE WHEN category_table.`is_income` = 1 THEN 0 ELSE 1 END, " +
                    "money_moving_table.`amount`, " +
                    "money_moving_table.`description`, " +
                    "money_moving_table.`id` " +
                    "FROM `money_moving_table` " +
                    "LEFT JOIN `category_table` " +
                    "ON money_moving_table.`category` = category_table.`categoriesId`"
        )
        database.execSQL("DROP TABLE `money_moving_table`")
        database.execSQL("ALTER TABLE `money_moving_table_new` RENAME TO `money_moving_table`")
        createMoneyMovingValidationTriggers(database)
    }
}

private object seedPaymentTypesOnCreate : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedPaymentTypes(db)
        createMoneyMovingValidationTriggers(db)
    }
}

private fun createPaymentTypeTable(database: SupportSQLiteDatabase) {
    database.execSQL(
        "CREATE TABLE IF NOT EXISTS `payment_type_table` (" +
                "`id` INTEGER NOT NULL, " +
                "`payment_type_code` TEXT NOT NULL, " +
                "`payment_type_name` TEXT NOT NULL, " +
                "`affects_reports` INTEGER NOT NULL, " +
                "`requires_category` INTEGER NOT NULL, " +
                "`requires_second_account` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`)" +
                ")"
    )
}

private fun seedPaymentTypes(database: SupportSQLiteDatabase) {
    database.execSQL(
        "INSERT OR IGNORE INTO `payment_type_table` " +
                "(`id`, `payment_type_code`, `payment_type_name`, `affects_reports`, `requires_category`, `requires_second_account`) " +
                "VALUES (0, 'income', 'Income', 1, 1, 0)"
    )
    database.execSQL(
        "INSERT OR IGNORE INTO `payment_type_table` " +
                "(`id`, `payment_type_code`, `payment_type_name`, `affects_reports`, `requires_category`, `requires_second_account`) " +
                "VALUES (1, 'spending', 'Spending', 1, 1, 0)"
    )
    database.execSQL(
        "INSERT OR IGNORE INTO `payment_type_table` " +
                "(`id`, `payment_type_code`, `payment_type_name`, `affects_reports`, `requires_category`, `requires_second_account`) " +
                "VALUES (2, 'transfer', 'Transfer', 0, 0, 1)"
    )
}

private fun createMoneyMovingValidationTriggers(database: SupportSQLiteDatabase) {
    database.execSQL(
        "CREATE TRIGGER IF NOT EXISTS `validate_money_moving_insert` " +
                "BEFORE INSERT ON `money_moving_table` " +
                "WHEN NOT (" + moneyMovingPaymentTypeRule("NEW") + ") " +
                "BEGIN SELECT RAISE(ABORT, 'invalid money movement payment type'); END"
    )
    database.execSQL(
        "CREATE TRIGGER IF NOT EXISTS `validate_money_moving_update` " +
                "BEFORE UPDATE ON `money_moving_table` " +
                "WHEN NOT (" + moneyMovingPaymentTypeRule("NEW") + ") " +
                "BEGIN SELECT RAISE(ABORT, 'invalid money movement payment type'); END"
    )
}

private fun moneyMovingPaymentTypeRule(alias: String): String {
    return "((" +
            "$alias.payment_type_id IN (0, 1) " +
            "AND $alias.category IS NOT NULL " +
            "AND $alias.transfer_group_id IS NULL " +
            "AND $alias.transfer_direction IS NULL" +
            ") OR (" +
            "$alias.payment_type_id = 2 " +
            "AND $alias.category IS NULL " +
            "AND $alias.transfer_group_id IS NOT NULL " +
            "AND $alias.transfer_direction IN (0, 1)" +
            "))"
}
