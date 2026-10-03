package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.MediaItem
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

/**
 * Landscape Card for Continue Watching row (16:9).
 */
@Composable
fun TvContinueWatchingCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "cw_card_scale"
    )

    Column(
        modifier = modifier
            .width(220.dp)
            .zIndex(if (isFocused) 10f else 1f)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable { onClick() }
            .testTag("cw_card_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(124.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TvSurfaceVariant)
                .border(
                    BorderStroke(
                        width = if (isFocused) 3.dp else 1.dp,
                        color = if (isFocused) TvFocusBorder else Color(0x22FFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (isFocused) Modifier.shadow(12.dp, RoundedCornerShape(12.dp), spotColor = Color.White)
                    else Modifier
                )
        ) {
            Image(
                painter = painterResource(id = item.backdropResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Play overlay icon when focused
            if (isFocused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x44000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(GoogleBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "繼續觀看",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Progress bar
            item.progress?.let { progress ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0x66000000))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .background(GoogleBlue)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = if (isFocused) TvTextPrimary else TvTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = item.subtitle,
            color = TvTextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

/**
 * Portrait Card for Top Picks & Movie rows (2:3 or 3:4).
 */
@Composable
fun TvPosterCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "poster_card_scale"
    )

    Column(
        modifier = modifier
            .width(160.dp)
            .zIndex(if (isFocused) 10f else 1f)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable { onClick() }
            .testTag("poster_card_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(230.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TvSurfaceVariant)
                .border(
                    BorderStroke(
                        width = if (isFocused) 3.dp else 1.dp,
                        color = if (isFocused) TvFocusBorder else Color(0x22FFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (isFocused) Modifier.shadow(14.dp, RoundedCornerShape(12.dp), spotColor = Color.White)
                    else Modifier
                )
        ) {
            Image(
                painter = painterResource(id = item.backdropResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating badge on top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xDD000000))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "評分",
                        tint = TvGoldBadge,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = item.rating,
                        color = TvGoldBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dark gradient at bottom of poster
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC000000))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = if (isFocused) TvTextPrimary else TvTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${item.year} • ${item.genres.firstOrNull() ?: item.subtitle}",
            color = TvTextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
