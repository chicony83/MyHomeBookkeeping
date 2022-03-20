package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.IconCategory

@Dao
interface IconCategoryDao {

    @Insert
    suspend fun addNewIconCategory(iconCategory: IconCategory): Long

    @Query("SELECT * FROM icon_category_table")
    suspend fun getAllIconCategories(): List<IconCategory>

    @Query("SELECT * FROM icon_category_table WHERE id = :selectedId")
    suspend fun getIconCategoryById(selectedId: Int): IconCategory
}