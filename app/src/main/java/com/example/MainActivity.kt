package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TvNavTab
import com.example.ui.components.AssignCategoriesDialog
import com.example.ui.components.CreateCategoryDialog
import com.example.ui.components.TvAppActionDialog
import com.example.ui.components.TvMediaDetailDialog
import com.example.ui.components.TvSettingsDialog
import com.example.ui.components.TvTopBar
import com.example.ui.screens.AmbientScreen
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.GoogleTvTheme
import com.example.ui.theme.TvBackground
import com.example.ui.viewmodel.TvDialogState
import com.example.ui.viewmodel.TvLauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TvLauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GoogleTvTheme {
                TvLauncherApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh installed apps in case new apps were installed or uninstalled
        viewModel.refreshApps()
    }
}

@Composable
fun TvLauncherApp(viewModel: TvLauncherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Toast message handler
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Android TV Back Button Logic
    BackHandler {
        when {
            uiState.ambientMode -> {
                viewModel.toggleAmbientMode()
            }
            uiState.activeDialog !is TvDialogState.None -> {
                viewModel.dismissDialog()
            }
            uiState.currentTab != TvNavTab.FOR_YOU -> {
                viewModel.selectTab(TvNavTab.FOR_YOU)
            }
            else -> {
                // Already on Home Tab: Launcher stays open on TV
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TvBackground)
            .safeDrawingPadding(),
        containerColor = TvBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Google TV Top Bar
                TvTopBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    currentTimeString = uiState.currentTimeString,
                    onOpenSettings = { viewModel.openDialog(TvDialogState.Settings) },
                    onToggleAmbient = { viewModel.toggleAmbientMode() }
                )

                // Main Content Area with Crossfade
                Crossfade(
                    targetState = uiState.currentTab,
                    animationSpec = tween(durationMillis = 350),
                    label = "tab_crossfade",
                    modifier = Modifier.weight(1f)
                ) { tab ->
                    when (tab) {
                        TvNavTab.FOR_YOU -> {
                            HomeScreen(
                                heroItems = uiState.heroItems,
                                activeHeroIndex = uiState.activeHeroIndex,
                                favoriteApps = uiState.favoriteApps,
                                continueWatching = uiState.continueWatching,
                                topPicks = uiState.topPicks,
                                onSelectHeroIndex = { viewModel.selectHeroIndex(it) },
                                onPlayMedia = { item ->
                                    viewModel.openDialog(TvDialogState.MediaDetail(item))
                                },
                                onToggleWatchlist = { item ->
                                    viewModel.toggleWatchlist(item)
                                },
                                onShowMediaDetail = { item ->
                                    viewModel.openDialog(TvDialogState.MediaDetail(item))
                                },
                                isWatchlisted = { id -> uiState.watchlistIds.contains(id) },
                                onLaunchApp = { app ->
                                    viewModel.launchApp(app)
                                },
                                onAppLongClick = { app ->
                                    viewModel.openDialog(TvDialogState.AppOptions(app))
                                },
                                onNavigateToApps = {
                                    viewModel.selectTab(TvNavTab.APPS)
                                }
                            )
                        }

                        TvNavTab.APPS -> {
                            AppsScreen(
                                installedApps = uiState.installedApps,
                                categories = uiState.categories,
                                selectedCategoryFilterId = uiState.selectedAppCategoryFilterId,
                                onSelectCategoryFilter = { viewModel.selectCategoryFilter(it) },
                                onOpenCreateCategory = {
                                    viewModel.openDialog(TvDialogState.CreateCategory)
                                },
                                onLaunchApp = { app ->
                                    viewModel.launchApp(app)
                                },
                                onAppLongClick = { app ->
                                    viewModel.openDialog(TvDialogState.AppOptions(app))
                                }
                            )
                        }

                        TvNavTab.MOVIES -> {
                            MoviesScreen(
                                topPicks = uiState.topPicks,
                                onSelectMedia = { item ->
                                    viewModel.openDialog(TvDialogState.MediaDetail(item))
                                }
                            )
                        }

                        TvNavTab.SEARCH -> {
                            SearchScreen(
                                searchQuery = uiState.searchQuery,
                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                installedApps = uiState.installedApps,
                                allMedia = uiState.topPicks + uiState.heroItems,
                                onLaunchApp = { app ->
                                    viewModel.launchApp(app)
                                },
                                onAppLongClick = { app ->
                                    viewModel.openDialog(TvDialogState.AppOptions(app))
                                },
                                onSelectMedia = { item ->
                                    viewModel.openDialog(TvDialogState.MediaDetail(item))
                                }
                            )
                        }

                        TvNavTab.LIBRARY -> {
                            LibraryScreen(
                                allMedia = uiState.topPicks + uiState.heroItems,
                                watchlistIds = uiState.watchlistIds,
                                onSelectMedia = { item ->
                                    viewModel.openDialog(TvDialogState.MediaDetail(item))
                                },
                                onExploreHome = {
                                    viewModel.selectTab(TvNavTab.FOR_YOU)
                                }
                            )
                        }
                    }
                }
            }

            // Ambient Mode / Screensaver Overlay
            if (uiState.ambientMode) {
                AmbientScreen(
                    currentTime = uiState.currentTimeString,
                    onExit = { viewModel.toggleAmbientMode() }
                )
            }

            // Dialogs
            when (val dialog = uiState.activeDialog) {
                is TvDialogState.CreateCategory -> {
                    CreateCategoryDialog(
                        onDismiss = { viewModel.dismissDialog() },
                        onCreate = { name -> viewModel.createCategory(name) }
                    )
                }

                is TvDialogState.AppOptions -> {
                    TvAppActionDialog(
                        app = dialog.app,
                        onDismiss = { viewModel.dismissDialog() },
                        onLaunch = { viewModel.launchApp(dialog.app) },
                        onToggleFavorite = { viewModel.toggleFavorite(dialog.app) },
                        onOpenAssignCategories = {
                            viewModel.openDialog(TvDialogState.AssignCategories(dialog.app))
                        }
                    )
                }

                is TvDialogState.AssignCategories -> {
                    AssignCategoriesDialog(
                        app = dialog.app,
                        allCategories = uiState.categories,
                        onDismiss = { viewModel.dismissDialog() },
                        onSave = { categoryIds ->
                            viewModel.setAppCategories(dialog.app.packageName, categoryIds)
                        }
                    )
                }

                is TvDialogState.Settings -> {
                    TvSettingsDialog(
                        categories = uiState.categories,
                        onDismiss = { viewModel.dismissDialog() },
                        onOpenCreateCategory = {
                            viewModel.openDialog(TvDialogState.CreateCategory)
                        },
                        onDeleteCategory = { id -> viewModel.deleteCategory(id) },
                        onRefreshApps = { viewModel.refreshApps() },
                        onOpenAmbient = { viewModel.toggleAmbientMode() }
                    )
                }

                is TvDialogState.MediaDetail -> {
                    TvMediaDetailDialog(
                        item = dialog.item,
                        isWatchlisted = uiState.watchlistIds.contains(dialog.item.id),
                        onDismiss = { viewModel.dismissDialog() },
                        onPlay = {
                            viewModel.showToast("正在透過 Google TV 串流引擎播放《${dialog.item.title}》")
                        },
                        onToggleWatchlist = {
                            viewModel.toggleWatchlist(dialog.item)
                        }
                    )
                }

                else -> {}
            }
        }
    }
}
