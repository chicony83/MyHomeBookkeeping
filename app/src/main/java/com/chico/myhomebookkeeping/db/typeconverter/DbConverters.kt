package com.chico.myhomebookkeeping.db.typeconverter

import androidx.room.TypeConverter
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.google.gson.Gson



class DbConverters {

    @TypeConverter
    fun listToJson(value: List<ChildCategory>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<ChildCategory>::class.java).toList()

}