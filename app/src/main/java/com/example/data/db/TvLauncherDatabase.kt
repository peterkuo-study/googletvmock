package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        AppCategoryMappingEntity::class,
        AppFavoriteEntity::class,
        WatchlistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TvLauncherDatabase : RoomDatabase() {
    abstract fun launcherDao(): TvLauncherDao

    companion object {
        @Volatile
        private var INSTANCE: TvLauncherDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TvLauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TvLauncherDatabase::class.java,
                    "google_tv_launcher.db"
                ).addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialCategories(database.launcherDao())
                    }
                }
            }

            suspend fun populateInitialCategories(dao: TvLauncherDao) {
                val defaultCategories = listOf(
                    CategoryEntity(id = 1, name = "串流影音", iconName = "tv", sortOrder = 1, isCustom = false),
                    CategoryEntity(id = 2, name = "遊戲娛樂", iconName = "sports_esports", sortOrder = 2, isCustom = false),
                    CategoryEntity(id = 3, name = "實用工具", iconName = "build", sortOrder = 3, isCustom = false),
                    CategoryEntity(id = 4, name = "音樂廣播", iconName = "music_note", sortOrder = 4, isCustom = false),
                    CategoryEntity(id = 5, name = "新聞體育", iconName = "newspaper", sortOrder = 5, isCustom = false)
                )
                for (cat in defaultCategories) {
                    dao.insertCategory(cat)
                }
            }
        }
    }
}
