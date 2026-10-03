package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledApp
import com.example.data.model.MediaItem
import com.example.ui.components.TvAddAppTile
import com.example.ui.components.TvAppTile
import com.example.ui.components.TvContinueWatchingCard
import com.example.ui.components.TvHeroCarousel
import com.example.ui.components.TvPosterCard
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun HomeScreen(
    heroItems: List<MediaItem>,
    activeHeroIndex: Int,
    favoriteApps: List<InstalledApp>,
    continueWatching: List<MediaItem>,
    topPicks: List<MediaItem>,
    onSelectHeroIndex: (Int) -> Unit,
    onPlayMedia: (MediaItem) -> Unit,
    onToggleWatchlist: (MediaItem) -> Unit,
    onShowMediaDetail: (MediaItem) -> Unit,
    isWatchlisted: (String) -> Boolean,
    onLaunchApp: (InstalledApp) -> Unit,
    onAppLongClick: (InstalledApp) -> Unit,
    onNavigateToApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
        // 1. Google TV Hero Spotlight Carousel
        item {
            TvHeroCarousel(
                heroItems = heroItems,
                activeIndex = activeHeroIndex,
                onSelectIndex = onSelectHeroIndex,
                onPlay = onPlayMedia,
                onToggleWatchlist = onToggleWatchlist,
                onShowDetail = onShowMediaDetail,
                isWatchlisted = isWatchlisted
            )
        }

        // 2. "您的應用程式" (Your Apps Row)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "您的應用程式",
                        color = TvTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onNavigateToApps() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("all_apps_link")
                    ) {
                        Text(
                            text = "查看全部",
                            color = GoogleBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = GoogleBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(favoriteApps, key = { it.packageName }) { app ->
                        TvAppTile(
                            app = app,
                            onClick = { onLaunchApp(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }

                    // Add / Manage Apps tile
                    item {
                        TvAddAppTile(onClick = onNavigateToApps)
                    }
                }
            }
        }

        // 3. "繼續觀賞" (Continue Watching Row)
        if (continueWatching.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp)
                ) {
                    Text(
                        text = "繼續觀賞",
                        color = TvTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(continueWatching, key = { it.id }) { item ->
                            TvContinueWatchingCard(
                                item = item,
                                onClick = { onShowMediaDetail(item) }
                            )
                        }
                    }
                }
            }
        }

        // 4. "熱門推薦與精選" (Top Picks Row)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
            ) {
                Text(
                    text = "為您推薦的精選電影與影集",
                    color = TvTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(topPicks, key = { it.id }) { item ->
                        TvPosterCard(
                            item = item,
                            onClick = { onShowMediaDetail(item) }
                        )
                    }
                }
            }
        }
    }
}
