package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.chico.myhomebookkeeping.db.entity.ParentCategories

@Dao
interface ParentCategoriesDao {

    @Insert
    suspend fun addNewParentCategory(parentCategories: ParentCategories):Long
}