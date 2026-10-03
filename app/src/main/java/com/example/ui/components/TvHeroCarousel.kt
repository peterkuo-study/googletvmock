package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun TvHeroCarousel(
    heroItems: List<MediaItem>,
    activeIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onPlay: (MediaItem) -> Unit,
    onToggleWatchlist: (MediaItem) -> Unit,
    onShowDetail: (MediaItem) -> Unit,
    isWatchlisted: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    if (heroItems.isEmpty()) return
    val currentItem = heroItems.getOrNull(activeIndex) ?: heroItems.first()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        // Background Backdrop with Crossfade animation
        Crossfade(
            targetState = currentItem.backdropResId,
            animationSpec = tween(durationMillis = 800),
            label = "hero_crossfade"
        ) { resId ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = currentItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Left-to-Right Scrim: Darkens left side for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    TvBackground,
                                    TvBackground.copy(alpha = 0.92f),
                                    TvBackground.copy(alpha = 0.6f),
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = 1400f
                            )
                        )
                )
                // Bottom-to-Top Scrim: Blends bottom edge with app rows
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    TvBackground.copy(alpha = 0.7f),
                                    TvBackground
                                )
                            )
                        )
                )
            }
        }

        // Hero Content overlay (Title, Badges, Overview, Buttons)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 28.dp)
                .fillMaxWidth(0.65f)
        ) {
            // Provider & Category Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = currentItem.provider.uppercase(),
                    color = GoogleBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Text(text = "•", color = TvTextMuted, fontSize = 12.sp)
                Text(
                    text = currentItem.subtitle,
                    color = TvTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Big Cinematic Title
            Text(
                text = currentItem.title,
                color = TvTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row: Rating, Year, Duration, Tags
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Rating Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF2B2516))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "評分",
                        tint = TvGoldBadge,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${currentItem.rating} (${currentItem.ratingSource})",
                        color = TvGoldBadge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(text = currentItem.year, color = TvTextSecondary, fontSize = 13.sp)
                Text(text = "•", color = TvTextMuted, fontSize = 12.sp)
                Text(text = currentItem.durationOrEpisodes, color = TvTextSecondary, fontSize = 13.sp)

                // Format Tags (4K, Dolby, etc.)
                currentItem.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF262C3A))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            color = TvTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Plot Overview
            Text(
                text = currentItem.overview,
                color = TvTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Action Buttons Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play Button (Primary)
                TvHeroActionButton(
                    icon = Icons.Default.PlayArrow,
                    label = "立即播放",
                    isPrimary = true,
                    onClick = { onPlay(currentItem) },
                    testTag = "hero_play_button"
                )

                // Watchlist Toggle Button
                val inWatchlist = isWatchlisted(currentItem.id)
                TvHeroActionButton(
                    icon = if (inWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    label = if (inWatchlist) "已在待播" else "加入待播",
                    isPrimary = false,
                    onClick = { onToggleWatchlist(currentItem) },
                    testTag = "hero_watchlist_button"
                )

                // Details Button
                TvHeroActionButton(
                    icon = Icons.Default.Info,
                    label = "詳細資訊",
                    isPrimary = false,
                    onClick = { onShowDetail(currentItem) },
                    testTag = "hero_details_button"
                )
            }
        }

        // Indicator Dots (Bottom Right)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 48.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            heroItems.indices.forEach { index ->
                val isSelected = index == activeIndex
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (isSelected) 28.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoogleBlue else Color(0x66FFFFFF))
                        .clickable { onSelectIndex(index) }
                        .testTag("hero_indicator_$index")
                )
            }
        }
    }
}

@Composable
private fun TvHeroActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    var isFocused by remember { mutableStateOf(false) }

    val bg = when {
        isFocused -> TvFocusBorder
        isPrimary -> GoogleBlue
        else -> TvSurfaceVariant
    }

    val contentColor = when {
        isFocused -> Color.Black
        isPrimary -> Color.White
        else -> TvTextPrimary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .scale(if (isFocused) 1.08f else 1.0f)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
