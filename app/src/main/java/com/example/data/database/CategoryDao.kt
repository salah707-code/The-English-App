package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

data class CategoryCountResult(
    val category: String,
    val count: Int
)

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY orderIndex ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoriesCount(): Int

    @Query("SELECT category, COUNT(*) as count FROM words WHERE isDeleted = 0 GROUP BY category")
    fun getCategoryWordCounts(): Flow<List<CategoryCountResult>>
}
