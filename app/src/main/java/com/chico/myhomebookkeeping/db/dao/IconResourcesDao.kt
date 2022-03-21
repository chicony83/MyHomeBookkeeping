package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.IconsResource

@Dao
interface IconResourcesDao {
    @Insert
    suspend fun addNewIcon(newIcon: IconsResource): Long

    @Query("SELECT * FROM icon_resource_table")
    suspend fun getListIcons(): List<IconsResource>

    @Query("SELECT * FROM icon_resource_table WHERE id = :id")
    suspend fun getIconById(id: Int): IconsResource

    @Query("SELECT * FROM icon_resource_table WHERE icon_name = :name")
    suspend fun getIconByName(name: String): IconsResource
}