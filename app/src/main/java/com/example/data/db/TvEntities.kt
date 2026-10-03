package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconName: String = "category",
    val sortOrder: Int = 0,
    val isCustom: Boolean = true
)

@Entity(
    tableName = "app_category_mappings",
    primaryKeys = ["packageName", "categoryId"]
)
data class AppCategoryMappingEntity(
    val packageName: String,
    val categoryId: Int
)

@Entity(tableName = "app_favorites")
data class AppFavoriteEntity(
    @PrimaryKey
    val packageName: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val backdropResId: Int,
    val rating: String,
    val year: String,
    val overview: String,
    val addedAt: Long = System.currentTimeMillis()
)
