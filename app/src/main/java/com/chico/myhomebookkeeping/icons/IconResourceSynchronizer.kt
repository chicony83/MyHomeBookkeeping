package com.chico.myhomebookkeeping.icons

import android.content.Context
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dataBase

/**
 * Android drawable IDs are build-specific and can change after an application update.
 * The icon table keeps the stable icon name, so it can be used to repair every stored
 * drawable reference before the UI reads it.
 */
object IconResourceSynchronizer {

    fun synchronize(context: Context) {
        val roomDatabase = dataBase.getDataBase(context.applicationContext)
        val database = roomDatabase.openHelper.writableDatabase
        val currentIcons = IconsMaps(context.resources, context.packageName).run {
            getCategoriesIconsMap() + getCashAccountIconsList() + getNoCategoryIconsList()
        }
        val fallbackIcon = R.drawable.no_image

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
                val currentResource = currentIcons[icon.name] ?: fallbackIcon
                replaceResource(database, icon.temporaryResource, currentResource)
                database.execSQL(
                    "UPDATE icon_resource_table SET icon_resource = ? WHERE id = ?",
                    arrayOf(currentResource, icon.id)
                )
            }

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
