package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.components.TvPosterCard
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun LibraryScreen(
    allMedia: List<MediaItem>,
    watchlistIds: Set<String>,
    onSelectMedia: (MediaItem) -> Unit,
    onExploreHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val watchlistedMedia = allMedia.filter { watchlistIds.contains(it.id) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .testTag("library_screen"),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column {
                Text(
                    text = "我的影音庫",
                    color = TvTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "您的待播清單、已購內容與影集收藏",
                    color = TvTextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Watchlist Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = GoogleBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "待播清單 (${watchlistedMedia.size})",
                        color = TvTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (watchlistedMedia.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                color = com.example.ui.theme.TvSurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "待播清單目前是空的",
                                color = TvTextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onExploreHome,
                                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("explore_home_from_library")
                            ) {
                                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("前往首頁瀏覽電影並加入待播", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(watchlistedMedia, key = { "watchlist_${it.id}" }) { item ->
                            TvPosterCard(
                                item = item,
                                onClick = { onSelectMedia(item) }
                            )
                        }
                    }
                }
            }
        }

        // Recent Purchases & Rentals
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "已購與租借電影",
                    color = TvTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allMedia.take(3), key = { "purchased_${it.id}" }) { item ->
                        TvPosterCard(
                            item = item,
                            onClick = { onSelectMedia(item) }
                        )
                    }
                }
            }
        }
    }
}
