package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.UserParentCategory

@Dao
interface UserParentCategoriesDao {

    @Insert
    suspend fun addNewUserParentCategory(userParentCategory: UserParentCategory):Long

    @Insert
    suspend fun addUserParentCategories(userParentCategories: List<UserParentCategory>)

    @Query("SELECT * FROM user_parent_categories_table ORDER BY name ASC")
    suspend fun getAllUserParentCategoriesSortNameASC(): List<UserParentCategory>

    @Query("SELECT * FROM user_parent_categories_table ORDER BY name DESC")
    suspend fun getAllUserParentCategoriesSortNameDESC(): List<UserParentCategory>

    @Query("SELECT * FROM user_parent_categories_table ORDER BY categoriesId ASC")
    suspend fun getAllUserParentCategoriesSortIdASC(): List<UserParentCategory>

    @Query("SELECT * FROM user_parent_categories_table ORDER BY categoriesId DESC")
    suspend fun getAllUserParentCategoriesSortIdDESC(): List<UserParentCategory>

    @Query("SELECT * FROM user_parent_categories_table WHERE categoriesId = :id")
    suspend fun getOneUserParentCategory(id: Long): UserParentCategory

    @Query("UPDATE user_parent_categories_table SET name = :name, is_income = :isIncome, icon_res = :iconResource WHERE categoriesId = :id")
    suspend fun changeUserParentCategory(
        id: Long,
        name: String,
        isIncome: Boolean,
        iconResource: Int
    ):Int
}