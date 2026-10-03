package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Apps
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.InstalledApp
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceHighlight
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TvAppTile(
    app: InstalledApp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "app_tile_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(96.dp)
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
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("app_tile_${app.packageName.replace('.', '_')}")
    ) {
        // App Icon Container
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isFocused) TvSurfaceHighlight else TvSurfaceVariant)
                .border(
                    BorderStroke(
                        width = if (isFocused) 3.5.dp else 1.dp,
                        color = if (isFocused) TvFocusBorder else Color(0x22FFFFFF)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .then(
                    if (isFocused) Modifier.shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color.White)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            // Render App Icon
            val imageBitmap = remember(app.icon) {
                app.icon?.let { drawableToBitmap(it)?.asImageBitmap() }
            }

            if (imageBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = imageBitmap,
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                // Stylish Fallback Icon with app initial
                val initialLetter = app.appName.firstOrNull()?.uppercase() ?: "A"
                val bgColors = remember(app.packageName) {
                    val hash = app.packageName.hashCode()
                    when (kotlin.math.abs(hash) % 4) {
                        0 -> listOf(GoogleBlue, Color(0xFF1967D2))
                        1 -> listOf(GoogleRed, Color(0xFFC5221F))
                        2 -> listOf(GoogleYellow, Color(0xFFF29900))
                        else -> listOf(Color(0xFF34A853), Color(0xFF137333))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(bgColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialLetter,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Favorite Star Indicator if pinned
            if (app.isFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "已釘選為常用",
                        tint = TvGoldBadge,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // App Label
        Text(
            text = app.appName,
            color = if (isFocused) TvTextPrimary else TvTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(92.dp)
        )
    }
}

/**
 * Tile for "+ 新增/管理應用程式"
 */
@Composable
fun TvAddAppTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.15f else 1.0f,
        label = "add_app_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(96.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .testTag("manage_apps_tile")
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isFocused) TvFocusBorder else TvSurface)
                .border(
                    BorderStroke(
                        width = if (isFocused) 3.5.dp else 1.dp,
                        color = if (isFocused) TvFocusBorder else Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "新增更多應用程式",
                tint = if (isFocused) Color.Black else TvTextSecondary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "新增 / 管理",
            color = if (isFocused) TvTextPrimary else TvTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }

    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
