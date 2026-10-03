package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.R
import com.example.data.db.AppCategoryMappingEntity
import com.example.data.db.AppFavoriteEntity
import com.example.data.db.CategoryEntity
import com.example.data.db.TvLauncherDao
import com.example.data.db.WatchlistEntity
import com.example.data.model.AppCategory
import com.example.data.model.InstalledApp
import com.example.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class TvLauncherRepository(
    private val context: Context,
    private val dao: TvLauncherDao
) {

    val categories: Flow<List<AppCategory>> = dao.getAllCategories().combine(dao.getAllCategoryMappings()) { entities, mappings ->
        entities.map { entity ->
            AppCategory(
                id = entity.id,
                name = entity.name,
                iconName = entity.iconName,
                order = entity.sortOrder,
                isCustom = entity.isCustom
            )
        }
    }

    val favorites: Flow<List<AppFavoriteEntity>> = dao.getAllFavorites()
    val watchlist: Flow<List<WatchlistEntity>> = dao.getAllWatchlist()
    val categoryMappings: Flow<List<AppCategoryMappingEntity>> = dao.getAllCategoryMappings()

    /**
     * Query all installed applications from Android PackageManager.
     */
    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val myPackageName = context.packageName

        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        val launcherApps = pm.queryIntentActivities(launcherIntent, 0)
        val leanbackApps = pm.queryIntentActivities(leanbackIntent, 0)

        val allResolveInfos = (launcherApps + leanbackApps)
            .distinctBy { it.activityInfo.packageName }
            .filter { it.activityInfo.packageName != myPackageName }

        val apps = mutableListOf<InstalledApp>()

        for (resolveInfo in allResolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(pm).toString()
            val icon = resolveInfo.loadIcon(pm)
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

            apps.add(
                InstalledApp(
                    packageName = pkg,
                    appName = label,
                    icon = icon,
                    isSystemApp = isSystem
                )
            )
        }

        // If the device has very few apps (e.g. blank Android emulator), provide popular TV apps
        if (apps.size < 4) {
            val demoTvApps = getPopularTvDemoApps()
            for (demo in demoTvApps) {
                if (apps.none { it.packageName == demo.packageName }) {
                    apps.add(demo)
                }
            }
        }

        apps.sortedBy { it.appName.lowercase() }
    }

    private fun getPopularTvDemoApps(): List<InstalledApp> {
        return listOf(
            InstalledApp("com.google.android.youtube.tv", "YouTube", null),
            InstalledApp("com.netflix.ninja", "Netflix", null),
            InstalledApp("com.disney.disneyplus", "Disney+", null),
            InstalledApp("com.amazon.amazonvideo.livingroom", "Prime Video", null),
            InstalledApp("com.spotify.tv.android", "Spotify", null),
            InstalledApp("com.android.vending", "Google Play 商店", null),
            InstalledApp("tv.twitch.android.app", "Twitch", null),
            InstalledApp("com.plexapp.android", "Plex", null),
            InstalledApp("com.google.android.videos", "Google TV 影視", null),
            InstalledApp("com.android.tv.settings", "TV 設定", null)
        )
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
                ?: pm.getLeanbackLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleFavorite(packageName: String, isFavorite: Boolean) {
        if (isFavorite) {
            dao.removeFavorite(packageName)
        } else {
            dao.insertFavorite(AppFavoriteEntity(packageName = packageName, sortOrder = 0))
        }
    }

    suspend fun createCustomCategory(name: String, iconName: String = "folder"): Long {
        return dao.insertCategory(
            CategoryEntity(
                name = name,
                iconName = iconName,
                isCustom = true
            )
        )
    }

    suspend fun deleteCategory(categoryId: Int) {
        dao.deleteCategory(categoryId)
        dao.removeMappingsForCategory(categoryId)
    }

    suspend fun updateCategoryName(categoryId: Int, newName: String) {
        dao.updateCategoryName(categoryId, newName)
    }

    suspend fun setAppCategories(packageName: String, categoryIds: Set<Int>) {
        dao.removeMappingsForApp(packageName)
        for (catId in categoryIds) {
            dao.insertMapping(AppCategoryMappingEntity(packageName, catId))
        }
    }

    suspend fun toggleWatchlist(item: MediaItem, isInWatchlist: Boolean) {
        if (isInWatchlist) {
            dao.removeFromWatchlist(item.id)
        } else {
            dao.insertWatchlist(
                WatchlistEntity(
                    id = item.id,
                    title = item.title,
                    backdropResId = item.backdropResId,
                    rating = item.rating,
                    year = item.year,
                    overview = item.overview
                )
            )
        }
    }

    /**
     * Google TV Hero Spotlight Recommendations
     */
    fun getHeroRecommendations(): List<MediaItem> {
        return listOf(
            MediaItem(
                id = "hero_1",
                title = "星際星雲：深空旅者",
                subtitle = "Google TV 獨家首播",
                backdropResId = R.drawable.hero_movie_scifi_1791004731275,
                rating = "9.4",
                ratingSource = "IMDb",
                year = "2026",
                durationOrEpisodes = "2 小時 28 分",
                tags = listOf("4K UHD", "杜比視界", "杜比全景聲", "IMAX Enhanced"),
                genres = listOf("硬派科幻", "宇宙探險", "心靈震撼"),
                overview = "在遠離太陽系深處的環狀星雲行星，一位宇航探險家發現了一座沉睡了億萬年的遠古外星文明星門，人類的命運即將由此改寫。",
                provider = "Google TV 推薦"
            ),
            MediaItem(
                id = "hero_2",
                title = "新東京：賽博霓虹之夜",
                subtitle = "全球熱播原創影集",
                backdropResId = R.drawable.hero_movie_cyber_1791004743873,
                rating = "9.1",
                ratingSource = "爛番茄 97%",
                year = "2026",
                durationOrEpisodes = "第 2 季全集",
                tags = listOf("4K HDR", "5.1 聲道", "頂級動作"),
                genres = listOf("賽博龐克", "犯罪懸疑", "超能動作"),
                overview = "西元2089年的高聳雨夜都會，空中飛車在立體全息招牌間穿梭。一名被剝奪記憶的生化偵探，必須在各大巨型企業夾縫中找回真正的自我。",
                provider = "Netflix"
            ),
            MediaItem(
                id = "hero_3",
                title = "阿爾卑斯極光：冰川秘境",
                subtitle = "BBC 頂級生態紀錄片",
                backdropResId = R.drawable.hero_movie_nature_1791004754125,
                rating = "9.7",
                ratingSource = "IMDb",
                year = "2025",
                durationOrEpisodes = "1 小時 45 分",
                tags = listOf("8K 重製", "杜比視界", "全景音效"),
                genres = listOf("自然自然", "地理探秘", "視覺饗宴"),
                overview = "見證落日餘暉染金巍峨雪山與碧藍冰湖的震撼瞬間。歷時四年極地追蹤，捕捉地球最壯麗、最脆弱的奇蹟之美。",
                provider = "Disney+"
            )
        )
    }

    /**
     * Continue Watching (繼續觀賞) row items
     */
    fun getContinueWatchingItems(): List<MediaItem> {
        return listOf(
            MediaItem(
                id = "cw_1",
                title = "星際星雲：深空旅者",
                subtitle = "剩餘 42 分鐘",
                backdropResId = R.drawable.hero_movie_scifi_1791004731275,
                rating = "9.4",
                year = "2026",
                progress = 0.68f,
                overview = "宇航員抵達了未知的第二軌道，啟動了遠古躍遷引擎..."
            ),
            MediaItem(
                id = "cw_2",
                title = "新東京：賽博霓虹之夜",
                subtitle = "第 2 季 第 4 集",
                backdropResId = R.drawable.hero_movie_cyber_1791004743873,
                rating = "9.1",
                year = "2026",
                progress = 0.35f,
                overview = "在下水道黑市交易中，秘密數據卡落入神祕駭客手中..."
            ),
            MediaItem(
                id = "cw_3",
                title = "天空之城：浮空秘境",
                subtitle = "剩餘 15 分鐘",
                backdropResId = R.drawable.poster_fantasy_1791004828430,
                rating = "9.3",
                year = "2025",
                progress = 0.88f,
                overview = "飛行少年與守護精靈終於登上懸浮在雲端的永恆之塔..."
            )
        )
    }

    /**
     * Popular and Trending Content
     */
    fun getTopPicks(): List<MediaItem> {
        return listOf(
            MediaItem(
                id = "pick_1",
                title = "泰坦崛起：鋼鐵先鋒",
                subtitle = "2026 年度票房冠軍",
                backdropResId = R.drawable.poster_superhero_1791004805660,
                rating = "8.9",
                ratingSource = "IMDb",
                year = "2026",
                durationOrEpisodes = "2 小時 10 分",
                tags = listOf("4K UHD", "動作科幻"),
                genres = listOf("超級英雄", "科幻震撼"),
                overview = "當外星機械大軍入侵大都會，重裝機甲英雄誓死捍衛最後的家園防線。"
            ),
            MediaItem(
                id = "pick_2",
                title = "天空之城：浮空秘境",
                subtitle = "日本吉卜力風奇幻神作",
                backdropResId = R.drawable.poster_fantasy_1791004828430,
                rating = "9.5",
                ratingSource = "爛番茄 98%",
                year = "2025",
                durationOrEpisodes = "1 小時 58 分",
                tags = listOf("4K 重製", "家庭奇幻"),
                genres = listOf("動漫動畫", "冒險成長"),
                overview = "浮游在星空深處的不可思議秘境島嶼，承載著古老魔法與守護精靈的諾言。"
            ),
            MediaItem(
                id = "pick_3",
                title = "星際星雲：深空旅者",
                subtitle = "科幻史詩",
                backdropResId = R.drawable.hero_movie_scifi_1791004731275,
                rating = "9.4",
                ratingSource = "IMDb",
                year = "2026",
                durationOrEpisodes = "2 小時 28 分",
                tags = listOf("4K UHD"),
                genres = listOf("科幻", "冒險"),
                overview = "探尋宇宙最神秘的深空星雲。"
            ),
            MediaItem(
                id = "pick_4",
                title = "新東京：賽博霓虹之夜",
                subtitle = "熱播影集",
                backdropResId = R.drawable.hero_movie_cyber_1791004743873,
                rating = "9.1",
                ratingSource = "爛番茄",
                year = "2026",
                durationOrEpisodes = "全 10 集",
                tags = listOf("HDR"),
                genres = listOf("賽博龐克", "動作"),
                overview = "霓虹與陰影中的殊死搏鬥。"
            ),
            MediaItem(
                id = "pick_5",
                title = "阿爾卑斯極光：冰川秘境",
                subtitle = "極限自然記錄",
                backdropResId = R.drawable.hero_movie_nature_1791004754125,
                rating = "9.7",
                ratingSource = "IMDb",
                year = "2025",
                durationOrEpisodes = "1 小時 45 分",
                tags = listOf("8K 紀錄"),
                genres = listOf("自然探索"),
                overview = "大自然最壯闊美麗的一幕。"
            )
        )
    }
}
