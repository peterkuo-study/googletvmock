package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TvLauncherDao {

    // Categories
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("DELETE FROM categories WHERE id = :categoryId AND isCustom = 1")
    suspend fun deleteCategory(categoryId: Int)

    @Query("UPDATE categories SET name = :newName WHERE id = :categoryId")
    suspend fun updateCategoryName(categoryId: Int, newName: String)

    // App-Category mappings
    @Query("SELECT * FROM app_category_mappings")
    fun getAllCategoryMappings(): Flow<List<AppCategoryMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: AppCategoryMappingEntity)

    @Query("DELETE FROM app_category_mappings WHERE packageName = :packageName AND categoryId = :categoryId")
    suspend fun removeMapping(packageName: String, categoryId: Int)

    @Query("DELETE FROM app_category_mappings WHERE categoryId = :categoryId")
    suspend fun removeMappingsForCategory(categoryId: Int)

    @Query("DELETE FROM app_category_mappings WHERE packageName = :packageName")
    suspend fun removeMappingsForApp(packageName: String)

    // Favorites
    @Query("SELECT * FROM app_favorites ORDER BY sortOrder ASC")
    fun getAllFavorites(): Flow<List<AppFavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: AppFavoriteEntity)

    @Query("DELETE FROM app_favorites WHERE packageName = :packageName")
    suspend fun removeFavorite(packageName: String)

    // Watchlist
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun removeFromWatchlist(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :id)")
    fun isInWatchlist(id: String): Flow<Boolean>
}
