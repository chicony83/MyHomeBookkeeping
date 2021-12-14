package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Icons

@Dao
interface IconsDao {

    @Insert
    suspend fun addIcon(icon: Icons): Long

    @Query("SELECT * FROM icons_table")
    suspend fun getAllIcons(): List<Icons>
}