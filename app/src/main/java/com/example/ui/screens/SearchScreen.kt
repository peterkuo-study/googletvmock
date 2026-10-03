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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledApp
import com.example.data.model.MediaItem
import com.example.ui.components.TvAppTile
import com.example.ui.components.TvPosterCard
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceHighlight
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    installedApps: List<InstalledApp>,
    allMedia: List<MediaItem>,
    onLaunchApp: (InstalledApp) -> Unit,
    onAppLongClick: (InstalledApp) -> Unit,
    onSelectMedia: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestedKeywords = listOf("YouTube", "Netflix", "Disney+", "科幻", "賽博龐克", "太空", "動畫", "自然")

    val matchingApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) emptyList()
        else installedApps.filter {
            it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    val matchingMedia = remember(searchQuery, allMedia) {
        if (searchQuery.isBlank()) emptyList()
        else allMedia.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.overview.contains(searchQuery, ignoreCase = true) ||
                    it.genres.any { g -> g.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .padding(horizontal = 48.dp, vertical = 20.dp)
            .testTag("search_screen")
    ) {
        // Search Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text("搜尋電影、節目、應用程式或歌手...", color = TvTextMuted, fontSize = 16.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜尋",
                        tint = GoogleBlue,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "清除",
                                tint = TvTextSecondary
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TvTextPrimary,
                    unfocusedTextColor = TvTextPrimary,
                    focusedBorderColor = GoogleBlue,
                    unfocusedBorderColor = TvSurfaceHighlight,
                    focusedContainerColor = TvSurfaceVariant,
                    unfocusedContainerColor = TvSurfaceVariant
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field")
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Google Voice Search Mic button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF263238))
                    .clickable {
                        // Demo Voice prompt: fills keyword "星際"
                        onQueryChange("星際")
                    }
                    .testTag("voice_search_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Google TV 語音搜尋",
                    tint = GoogleRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Suggested Keywords
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "熱門搜尋：",
                color = TvTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            suggestedKeywords.forEach { keyword ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TvSurfaceVariant)
                        .clickable { onQueryChange(keyword) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("keyword_$keyword")
                ) {
                    Text(
                        text = keyword,
                        color = TvTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Search Results
        if (searchQuery.isNotBlank()) {
            if (matchingApps.isEmpty() && matchingMedia.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "找不到與「$searchQuery」相符的應用程式或影音項目",
                        color = TvTextSecondary,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Matching Apps row
                if (matchingApps.isNotEmpty()) {
                    Text(
                        text = "應用程式搜尋結果 (${matchingApps.size})",
                        color = TvTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(matchingApps, key = { it.packageName }) { app ->
                            TvAppTile(
                                app = app,
                                onClick = { onLaunchApp(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Matching Media row
                if (matchingMedia.isNotEmpty()) {
                    Text(
                        text = "影視與節目搜尋結果 (${matchingMedia.size})",
                        color = TvTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(matchingMedia, key = { it.id }) { item ->
                            TvPosterCard(
                                item = item,
                                onClick = { onSelectMedia(item) }
                            )
                        }
                    }
                }
            }
        } else {
            // Empty state before searching
            Column(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "猜你喜歡",
                    color = TvTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allMedia.take(5), key = { it.id }) { item ->
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
