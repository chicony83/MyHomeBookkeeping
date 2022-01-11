package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.chico.myhomebookkeeping.db.entity.IconsResource

@Dao
interface IconResourcesDao {
    @Insert
    suspend fun addNewIcon(newIcon: IconsResource): Long
}