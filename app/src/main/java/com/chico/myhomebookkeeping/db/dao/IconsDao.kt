package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Icons

@Dao
interface IconsDao {
    @Insert
    suspend fun addNewIcon(newIcon: Icons): Long

}