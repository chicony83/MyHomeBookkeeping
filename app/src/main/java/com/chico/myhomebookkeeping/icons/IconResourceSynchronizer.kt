package com.chico.myhomebookkeeping.icons

import android.content.Context
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.enums.icon.names.CategoriesOfIconsNames

/**
 * Android drawable IDs are build-specific and can change after an application update.
 * The icon table keeps the stable icon name, so it can be used to repair every stored
 * drawable reference before the UI reads it.
 */
object IconResourceSynchronizer {

    fun synchronize(context: Context) {
        val roomDatabase = dataBase.getDataBase(context.applicationContext)
        val database = roomDatabase.openHelper.writableDatabase
        val iconsMaps = IconsMaps(context.resources, context.packageName)
        val currentCategoryIcons = iconsMaps.getCategoriesIconsMap()
        val currentIcons = currentCategoryIcons +
            iconsMaps.getCashAccountIconsList() +
            iconsMaps.getNoCategoryIconsList()

        database.beginTransaction()
        try {
            val storedIcons = buildList {
                database.query(
                    "SELECT id, icon_name, icon_resource FROM icon_resource_table"
                ).use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow("id")
                    val nameIndex = cursor.getColumnIndexOrThrow("icon_name")
                    val resourceIndex = cursor.getColumnIndexOrThrow("icon_resource")
                    while (cursor.moveToNext()) {
                        add(
                            StoredIcon(
                                id = cursor.getLong(idIndex),
                                name = cursor.getString(nameIndex),
                                oldResource = cursor.getInt(resourceIndex)
                            )
                        )
                    }
                }
            }

            // First replace old IDs with unique temporary values. This also handles the
            // rare case where two resource IDs swap values between application builds.
            storedIcons.forEach { icon ->
                replaceResource(database, icon.oldResource, icon.temporaryResource)
            }

            storedIcons.forEach { icon ->
                val currentResource = currentIcons[icon.name] ?: icon.oldResource
                replaceResource(database, icon.temporaryResource, currentResource)
                database.execSQL(
                    "UPDATE icon_resource_table SET icon_resource = ? WHERE id = ?",
                    arrayOf<Any>(currentResource, icon.id)
                )
            }

            addMissingIcons(database, currentCategoryIcons)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            roomDatabase.close()
        }
    }

    private fun replaceResource(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        oldResource: Int,
        newResource: Int
    ) {
        ICON_COLUMNS.forEach { (table, column) ->
            database.execSQL(
                "UPDATE $table SET $column = ? WHERE $column = ?",
                arrayOf(newResource, oldResource)
            )
        }
    }

    private fun addMissingIcons(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        currentIcons: Map<String, Int>
    ) {
        val categoryId = getIconCategoryId(database, CategoriesOfIconsNames.Categories.name)
            ?: return
        currentIcons.forEach { (name, resource) ->
            val exists = database.query(
                "SELECT 1 FROM icon_resource_table WHERE icon_name = ? AND icon_category = ? LIMIT 1",
                arrayOf<Any>(name, categoryId)
            ).use { cursor -> cursor.moveToFirst() }
            if (!exists) {
                database.execSQL(
                    "INSERT INTO icon_resource_table (icon_name, icon_category, icon_resource) VALUES (?, ?, ?)",
                    arrayOf<Any>(name, categoryId, resource)
                )
            }
        }
    }

    private fun getIconCategoryId(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        name: String
    ): Long? {
        return database.query(
            "SELECT id FROM icon_category_table WHERE name = ? LIMIT 1",
            arrayOf<Any>(name)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            } else {
                null
            }
        }
    }

    private data class StoredIcon(
        val id: Long,
        val name: String,
        val oldResource: Int
    ) {
        val temporaryResource: Int = -1_000_000 - id.toInt()
    }

    private val ICON_COLUMNS = listOf(
        "cash_account_table" to "icon_cash_account",
        "category_table" to "icon_category",
        "currency_table" to "icon_currency",
        "fast_payments_table" to "icon",
        "parent_categories_table" to "name_icon_parent_category"
    )
}
