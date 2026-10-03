package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.TvLauncherDatabase
import com.example.data.model.AppCategory
import com.example.data.model.InstalledApp
import com.example.data.model.MediaItem
import com.example.data.model.TvNavTab
import com.example.data.repository.TvLauncherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface TvDialogState {
    data object None : TvDialogState
    data object Settings : TvDialogState
    data object CreateCategory : TvDialogState
    data class EditCategory(val category: AppCategory) : TvDialogState
    data class AppOptions(val app: InstalledApp) : TvDialogState
    data class AssignCategories(val app: InstalledApp) : TvDialogState
    data class MediaDetail(val item: MediaItem) : TvDialogState
    data object DefaultLauncherInfo : TvDialogState
}

data class TvUiState(
    val currentTab: TvNavTab = TvNavTab.FOR_YOU,
    val installedApps: List<InstalledApp> = emptyList(),
    val favoriteApps: List<InstalledApp> = emptyList(),
    val categories: List<AppCategory> = emptyList(),
    val selectedAppCategoryFilterId: Int? = null,
    val heroItems: List<MediaItem> = emptyList(),
    val activeHeroIndex: Int = 0,
    val continueWatching: List<MediaItem> = emptyList(),
    val topPicks: List<MediaItem> = emptyList(),
    val watchlistIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val activeDialog: TvDialogState = TvDialogState.None,
    val ambientMode: Boolean = false,
    val currentTimeString: String = "",
    val isLoading: Boolean = true,
    val toastMessage: String? = null
)

class TvLauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TvLauncherDatabase.getDatabase(application, viewModelScope)
    private val repository = TvLauncherRepository(application, db.launcherDao())

    private val _uiState = MutableStateFlow(
        TvUiState(
            heroItems = repository.getHeroRecommendations(),
            continueWatching = repository.getContinueWatchingItems(),
            topPicks = repository.getTopPicks()
        )
    )
    val uiState: StateFlow<TvUiState> = _uiState.asStateFlow()

    private var rawInstalledApps: List<InstalledApp> = emptyList()

    init {
        // Start live clock
        startClockUpdates()
        // Auto rotate hero carousel every 8 seconds if in FOR_YOU tab
        startHeroAutoRotate()
        // Observe database and package manager
        observeData()
    }

    private fun startClockUpdates() {
        viewModelScope.launch(Dispatchers.Default) {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            while (isActive) {
                val now = timeFormat.format(Date())
                _uiState.update { it.copy(currentTimeString = now) }
                delay(1000)
            }
        }
    }

    private fun startHeroAutoRotate() {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(8000)
                _uiState.update { current ->
                    if (current.currentTab == TvNavTab.FOR_YOU && current.heroItems.isNotEmpty() && current.activeDialog == TvDialogState.None) {
                        current.copy(activeHeroIndex = (current.activeHeroIndex + 1) % current.heroItems.size)
                    } else {
                        current
                    }
                }
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            rawInstalledApps = repository.getInstalledApps()

            combine(
                repository.categories,
                repository.favorites,
                repository.categoryMappings,
                repository.watchlist
            ) { categories, favorites, mappings, watchlist ->
                val favSet = favorites.map { it.packageName }.toSet()
                val mappingMap = mappings.groupBy({ it.packageName }, { it.categoryId })
                val watchlistSet = watchlist.map { it.id }.toSet()

                val enrichedApps = rawInstalledApps.map { app ->
                    app.copy(
                        isFavorite = favSet.contains(app.packageName),
                        categoryIds = mappingMap[app.packageName]?.toSet() ?: emptySet()
                    )
                }

                val favApps = enrichedApps.filter { it.isFavorite }
                val fallbackFavs = if (favApps.isEmpty()) enrichedApps.take(6) else favApps

                EnrichedData(
                    apps = enrichedApps,
                    favorites = fallbackFavs,
                    categories = categories,
                    watchlistSet = watchlistSet
                )
            }.collect { enriched ->
                _uiState.update { state ->
                    state.copy(
                        installedApps = enriched.apps,
                        favoriteApps = enriched.favorites,
                        categories = enriched.categories,
                        watchlistIds = enriched.watchlistSet,
                        isLoading = false
                    )
                }
            }
        }
    }

    private data class EnrichedData(
        val apps: List<InstalledApp>,
        val favorites: List<InstalledApp>,
        val categories: List<AppCategory>,
        val watchlistSet: Set<String>
    )

    fun refreshApps() {
        viewModelScope.launch {
            rawInstalledApps = repository.getInstalledApps()
            observeData()
        }
    }

    fun selectTab(tab: TvNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectHeroIndex(index: Int) {
        _uiState.update { it.copy(activeHeroIndex = index) }
    }

    fun selectCategoryFilter(categoryId: Int?) {
        _uiState.update { it.copy(selectedAppCategoryFilterId = categoryId) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openDialog(dialog: TvDialogState) {
        _uiState.update { it.copy(activeDialog = dialog) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(activeDialog = TvDialogState.None) }
    }

    fun launchApp(app: InstalledApp): Boolean {
        return repository.launchApp(app.packageName)
    }

    fun toggleFavorite(app: InstalledApp) {
        viewModelScope.launch {
            repository.toggleFavorite(app.packageName, app.isFavorite)
        }
    }

    fun toggleWatchlist(item: MediaItem) {
        viewModelScope.launch {
            val isInWatchlist = _uiState.value.watchlistIds.contains(item.id)
            repository.toggleWatchlist(item, isInWatchlist)
            showToast(if (isInWatchlist) "已從待播清單移除" else "已加入待播清單")
        }
    }

    fun createCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createCustomCategory(name.trim())
            dismissDialog()
            showToast("已建立分類：$name")
        }
    }

    fun updateCategory(categoryId: Int, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updateCategoryName(categoryId, newName.trim())
            dismissDialog()
            showToast("已更新分類名稱")
        }
    }

    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            if (_uiState.value.selectedAppCategoryFilterId == categoryId) {
                _uiState.update { it.copy(selectedAppCategoryFilterId = null) }
            }
            dismissDialog()
            showToast("已刪除分類")
        }
    }

    fun setAppCategories(packageName: String, categoryIds: Set<Int>) {
        viewModelScope.launch {
            repository.setAppCategories(packageName, categoryIds)
            dismissDialog()
            showToast("已更新應用程式分類")
        }
    }

    fun toggleAmbientMode() {
        _uiState.update { it.copy(ambientMode = !it.ambientMode) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
