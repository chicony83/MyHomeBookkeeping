package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.chico.myhomebookkeeping.db.entity.IconCategory

@Dao
interface IconCategoryDao {

    @Insert
    suspend fun addNewIconCategory(iconCategory:IconCategory):Long
}