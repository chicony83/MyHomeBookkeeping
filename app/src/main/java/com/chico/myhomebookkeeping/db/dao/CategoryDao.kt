package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories

@Dao
interface CategoryDao {

    @Insert
    suspend fun addCategory(category: Categories): Long

    @Query("SELECT COUNT(*) FROM category_table")
    suspend fun getCategoriesCount(): Int

    @Query("SELECT * FROM category_table ORDER BY category_name ASC")
    suspend fun getAllCategoriesSortNameASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY category_order ASC, category_name ASC")
    suspend fun getAllCategoriesSortOrderASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY category_name DESC")
    suspend fun getAllCategoriesSortNameDESC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId ASC")
    suspend fun getAllCategoriesSortIdASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId DESC")
    suspend fun getAllCategoriesSortIdDESC(): List<Categories>

    @Query("SELECT * FROM category_table WHERE categoriesId = :id")
    suspend fun getOneCategory(id: Int): Categories

    @Query("SELECT * FROM category_table WHERE category_name = :name ORDER BY categoriesId ASC LIMIT 1")
    suspend fun getOneCategoryByCanonicalName(name: String): Categories?

    @Query("SELECT * FROM category_table WHERE category_name IN (:names) OR category_name_ru IN (:names) OR category_name_pl IN (:names) ORDER BY categoriesId ASC LIMIT 1")
    suspend fun getOneCategoryByAnyDefaultName(names: List<String>): Categories?

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome, icon_key = :iconKey, parent_category_id = NULL WHERE categoriesId = :id")
    suspend fun changeLineWithoutCategory(
        id: Int,
        name: String,
        isIncome: Boolean,
        iconKey: String
    ): Int

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome, icon_key = :iconKey, parent_category_id = :parentCategoryId WHERE categoriesId = :id")
    suspend fun changeLineFull(
        id: Int,
        name: String,
        isIncome: Boolean,
        iconKey: String,
        parentCategoryId: Int
    ):Int

    @Query("SELECT * FROM category_table WHERE parent_category_id = :parentCategoryId ORDER BY category_name ASC")
    suspend fun getAllCategoriesWithParentIdSortNameAsc(parentCategoryId: Int): List<Categories>?

    @Query("SELECT * FROM category_table WHERE parent_category_id IS null ORDER BY category_name ASC")
    suspend fun getAllCategoriesWithoutParentCategory(): List<Categories>?

    @Query("UPDATE category_table SET category_order = :order WHERE categoriesId = :id")
    suspend fun updateCategoryOrder(id: Int, order: Int): Int

    @Query("UPDATE category_table SET is_favorite = :isFavorite WHERE categoriesId = :id")
    suspend fun updateCategoryFavorite(id: Int, isFavorite: Boolean): Int

    @Query("UPDATE category_table SET usage_count = usage_count + 1 WHERE categoriesId = :id")
    suspend fun incrementUsageCount(id: Int): Int

    @Query("UPDATE category_table SET usage_count = CASE WHEN usage_count > 0 THEN usage_count - 1 ELSE 0 END WHERE categoriesId = :id")
    suspend fun decrementUsageCount(id: Int): Int

    @Query(
        "UPDATE category_table " +
                "SET usage_count = (" +
                "SELECT COUNT(*) FROM money_moving_table " +
                "WHERE money_moving_table.category = category_table.categoriesId " +
                "AND money_moving_table.payment_type_id IN (0, 1)" +
                ") " +
                "WHERE category_name != 'Transfer fee'"
    )
    suspend fun rebuildUsageCount(): Int

    @Query("UPDATE category_table SET usage_count = 0 WHERE category_name = 'Transfer fee'")
    suspend fun clearTransferFeeUsageCount(): Int

    @Query("UPDATE category_table SET parent_category_id = :parentCategoryId, category_order = :order WHERE categoriesId = :id")
    suspend fun updateCategoryParentAndOrder(id: Int, parentCategoryId: Int?, order: Int): Int

    @Query("UPDATE category_table SET category_name_ru = :localizedName WHERE category_name = :canonicalName AND (category_name_ru IS NULL OR TRIM(category_name_ru) = '')")
    suspend fun fillMissingLocalizedName(canonicalName: String, localizedName: String): Int

}
