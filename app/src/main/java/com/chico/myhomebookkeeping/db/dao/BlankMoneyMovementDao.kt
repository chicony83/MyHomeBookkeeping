package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.BlankMoneyMovement

@Dao
interface BlankMoneyMovementDao {
    @Insert
    suspend fun addBlank(blank: BlankMoneyMovement):Long

    @Query("SELECT * FROM blanks_table")
    suspend fun selectAllBlanks():List<BlankMoneyMovement>
}