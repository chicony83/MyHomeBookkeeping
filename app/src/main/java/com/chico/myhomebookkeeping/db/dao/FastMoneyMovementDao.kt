package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.FastMoneyMovement

@Dao
interface FastMoneyMovementDao {
    @Insert
    suspend fun addBlank(fast: FastMoneyMovement):Long

    @Query("SELECT * FROM fast_movement_table")
    suspend fun getAllBlanks():List<FastMoneyMovement>
}