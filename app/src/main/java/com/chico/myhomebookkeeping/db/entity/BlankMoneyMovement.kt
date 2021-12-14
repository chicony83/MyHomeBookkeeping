package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blanks_table")
data class BlankMoneyMovement(
    val name: String,
    val cash_account: Int,
    val currency: Int,
    val category: Int,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
