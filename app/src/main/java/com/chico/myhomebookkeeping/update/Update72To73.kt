package com.chico.myhomebookkeeping.update

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.enums.icon.names.CategoriesOfIconsNames
import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.icons.CategoryIconCatalog
import com.chico.myhomebookkeeping.icons.DefaultCategoryIconAssignments

class Update72To73 {
    suspend fun update(app: Application) {
        Message.log("...updating 72 to 73...")
        UpdateIconsToMaterial().update(app)
        Message.log("...updating 72 to 73 complete...")
    }

    class UpdateIconsToMaterial {
        fun update(app: Application) {
            val roomDatabase = dataBase.getDataBase(app.applicationContext)
            val database = roomDatabase.openHelper.writableDatabase

            database.beginTransaction()
            try {
                val categoryIconCategoryId = categoryIconCategoryId(database) ?: return
                val legacyResources = legacyResources(database, categoryIconCategoryId)

                applyDefaultAssignments(database)

                legacyResources.forEach { (resource, key) ->
                    database.execSQL(
                        "UPDATE category_table SET icon_key = ? WHERE icon_category = ? AND icon_key IS NULL",
                        arrayOf<Any>(key, resource)
                    )
                    database.execSQL(
                        "UPDATE parent_categories_table SET icon_key = ? WHERE name_icon_parent_category = ? AND icon_key IS NULL",
                        arrayOf<Any>(key, resource)
                    )
                }

                database.execSQL(
                    "UPDATE category_table SET icon_key = ? WHERE icon_key IS NULL",
                    arrayOf<Any>(CategoryIconCatalog.DEFAULT_KEY)
                )
                database.execSQL(
                    "UPDATE parent_categories_table SET icon_key = ? WHERE icon_key IS NULL",
                    arrayOf<Any>(CategoryIconCatalog.DEFAULT_KEY)
                )

                addMissingCatalogIcons(database, categoryIconCategoryId)
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
                roomDatabase.close()
            }
        }

        private fun categoryIconCategoryId(database: SupportSQLiteDatabase): Long? {
            return database.query(
                "SELECT id FROM icon_category_table WHERE name = ? LIMIT 1",
                arrayOf<Any>(CategoriesOfIconsNames.Categories.name)
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else null
            }
        }

        private fun legacyResources(database: SupportSQLiteDatabase, categoryId: Long): Map<Int, String> {
            return buildMap {
                database.query(
                    "SELECT icon_name, icon_resource FROM icon_resource_table WHERE icon_category = ?",
                    arrayOf<Any>(categoryId)
                ).use { cursor ->
                    val nameIndex = cursor.getColumnIndexOrThrow("icon_name")
                    val resourceIndex = cursor.getColumnIndexOrThrow("icon_resource")
                    while (cursor.moveToNext()) {
                        CategoryIconCatalog.legacyCanonicalKey(cursor.getString(nameIndex))?.let { key ->
                            put(cursor.getInt(resourceIndex), key)
                        }
                    }
                }
            }
        }

        private fun addMissingCatalogIcons(database: SupportSQLiteDatabase, categoryId: Long) {
            CategoryIconCatalog.keys.forEach { key ->
                database.execSQL(
                    "INSERT INTO icon_resource_table (icon_name, icon_category, icon_resource) " +
                        "SELECT ?, ?, ? WHERE NOT EXISTS (" +
                        "SELECT 1 FROM icon_resource_table WHERE icon_name = ? AND icon_category = ?" +
                        ")",
                    arrayOf<Any>(key, categoryId, R.drawable.no_image, key, categoryId)
                )
            }
        }

        private fun applyDefaultAssignments(database: SupportSQLiteDatabase) {
            DefaultCategoryCatalog.groups.forEach { group ->
                val parentIds = mutableListOf<Long>()
                database.query(
                    "SELECT id FROM parent_categories_table WHERE parent_category_name IN (?, ?, ?) " +
                        "OR parent_category_name_ru IN (?, ?, ?) OR parent_category_name_pl IN (?, ?, ?)",
                    arrayOf<Any>(
                        group.parentName, group.parentNameRu, group.parentNamePl,
                        group.parentName, group.parentNameRu, group.parentNamePl,
                        group.parentName, group.parentNameRu, group.parentNamePl
                    )
                ).use { cursor -> while (cursor.moveToNext()) parentIds += cursor.getLong(0) }

                parentIds.forEach { parentId ->
                    database.execSQL(
                        "UPDATE parent_categories_table SET icon_key = ? WHERE id = ? AND icon_key IS NULL",
                        arrayOf<Any>(DefaultCategoryIconAssignments.parentKey(group), parentId)
                    )
                    group.subcategories.indices.forEach { index ->
                        database.execSQL(
                            "UPDATE category_table SET icon_key = ? WHERE parent_category_id = ? " +
                                "AND (category_name IN (?, ?, ?) OR category_name_ru IN (?, ?, ?) " +
                                "OR category_name_pl IN (?, ?, ?)) AND icon_key IS NULL",
                            arrayOf<Any>(
                                DefaultCategoryIconAssignments.childKey(group, index), parentId,
                                group.subcategories[index], group.subcategoriesRu[index], group.subcategoriesPl[index],
                                group.subcategories[index], group.subcategoriesRu[index], group.subcategoriesPl[index],
                                group.subcategories[index], group.subcategoriesRu[index], group.subcategoriesPl[index]
                            )
                        )
                    }
                }
            }
        }
    }
}
