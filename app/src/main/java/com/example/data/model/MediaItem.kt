package com.example.data.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed application on the TV device.
 */
data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val isFavorite: Boolean = false,
    val categoryIds: Set<Int> = emptySet(),
    val launchIntentAvailable: Boolean = true
)

/**
 * Represents a category for apps.
 */
data class AppCategory(
    val id: Int,
    val name: String,
    val iconName: String = "category",
    val order: Int = 0,
    val isCustom: Boolean = false
)

/**
 * Media item for recommendations, continue watching, movies and shows.
 */
data class MediaItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val backdropResId: Int,
    val rating: String = "9.2",
    val ratingSource: String = "IMDb",
    val year: String = "2026",
    val durationOrEpisodes: String = "2 小時 15 分",
    val tags: List<String> = listOf("4K UHD", "HDR10+", "杜比全景聲"),
    val genres: List<String> = listOf("科幻", "冒險"),
    val overview: String = "",
    val progress: Float? = null, // for continue watching (0.0 to 1.0)
    val provider: String = "Google TV"
)

enum class TvNavTab(val title: String, val iconName: String) {
    SEARCH("搜尋", "search"),
    FOR_YOU("首頁推薦", "home"),
    MOVIES("電影與節目", "movie"),
    APPS("應用程式", "apps"),
    LIBRARY("影音庫", "video_library")
}
