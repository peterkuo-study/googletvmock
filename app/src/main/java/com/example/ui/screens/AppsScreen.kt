package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCategory
import com.example.data.model.InstalledApp
import com.example.ui.components.TvAppTile
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurfaceHighlight
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun AppsScreen(
    installedApps: List<InstalledApp>,
    categories: List<AppCategory>,
    selectedCategoryFilterId: Int?,
    onSelectCategoryFilter: (Int?) -> Unit,
    onOpenCreateCategory: () -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onAppLongClick: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter apps based on selected category:
    // - null: All apps
    // - -1: Favorites only
    // - id: Specific category mapping
    val filteredApps = remember(installedApps, selectedCategoryFilterId) {
        when (selectedCategoryFilterId) {
            null -> installedApps
            -1 -> installedApps.filter { it.isFavorite }
            else -> installedApps.filter { it.categoryIds.contains(selectedCategoryFilterId) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .padding(horizontal = 48.dp, vertical = 12.dp)
            .testTag("apps_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "應用程式庫",
                    color = TvTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "共 ${installedApps.size} 個電視應用程式 • 支援自訂分類與快速啟動",
                    color = TvTextSecondary,
                    fontSize = 13.sp
                )
            }

            // Add Custom Category Button
            Button(
                onClick = onOpenCreateCategory,
                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("btn_create_custom_category")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "新增自訂分類",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "全部" Chip
            item {
                TvCategoryChip(
                    name = "全部程式 (${installedApps.size})",
                    isSelected = selectedCategoryFilterId == null,
                    onClick = { onSelectCategoryFilter(null) },
                    testTag = "chip_category_all"
                )
            }

            // "我的最愛" Chip
            item {
                TvCategoryChip(
                    name = "⭐ 我的最愛",
                    isSelected = selectedCategoryFilterId == -1,
                    onClick = { onSelectCategoryFilter(-1) },
                    testTag = "chip_category_fav"
                )
            }

            // Dynamic categories from Room DB
            items(categories) { category ->
                val count = installedApps.count { it.categoryIds.contains(category.id) }
                TvCategoryChip(
                    name = if (category.isCustom) "${category.name} ($count)*" else "${category.name} ($count)",
                    isSelected = selectedCategoryFilterId == category.id,
                    onClick = { onSelectCategoryFilter(category.id) },
                    testTag = "chip_category_${category.id}"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hint bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141A26))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = GoogleBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "提示：點擊直接開啟應用程式；長按遙控器「確認鍵」或滑鼠長按，可為應用程式指定自訂分類或釘選至首頁。",
                color = TvTextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Apps
        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "此分類尚無任何應用程式",
                        color = TvTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "請切換到「全部程式」，長按任一應用程式並選擇「設定分類標籤」即可加入此分類！",
                        color = TvTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    TvAppTile(
                        app = app,
                        onClick = { onLaunchApp(app) },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TvCategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    var isFocused by remember { mutableStateOf(false) }

    val bg by animateColorAsState(
        targetValue = when {
            isFocused -> TvFocusBorder
            isSelected -> GoogleBlue
            else -> TvSurfaceVariant
        },
        label = "chip_bg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color.Black
            isSelected -> Color.White
            else -> TvTextSecondary
        },
        label = "chip_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}
