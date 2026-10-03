package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TvNavTab
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun TvTopBar(
    currentTab: TvNavTab,
    onTabSelected: (TvNavTab) -> Unit,
    currentTimeString: String,
    onOpenSettings: () -> Unit,
    onToggleAmbient: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Google TV Brand Identity
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            // Google TV 4-dot colored pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1B202D))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(GoogleBlue))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(GoogleRed))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(GoogleYellow))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(GoogleGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Google TV",
                    color = TvTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Center: Navigation Tabs
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TvNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                TvNavTabPill(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(tab) }
                )
            }
        }

        // Right: System Info (Clock, WiFi, Ambient, Settings, Profile)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Clock
            Text(
                text = currentTimeString.ifEmpty { "22:00" },
                color = TvTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Wi-Fi Icon
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi 連線正常",
                tint = TvTextSecondary,
                modifier = Modifier.size(20.dp)
            )

            // Ambient Mode Button (Screensaver)
            TvIconButton(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "環境模式",
                        tint = TvTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onToggleAmbient,
                testTag = "ambient_mode_button"
            )

            // Settings Button
            TvIconButton(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "設定",
                        tint = TvTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onOpenSettings,
                testTag = "settings_button"
            )

            // User Profile Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GoogleBlue, GoogleRed)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "使用者帳號",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TvNavTabPill(
    tab: TvNavTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFocused -> TvFocusBorder
            isSelected -> Color(0xFF283144)
            else -> Color.Transparent
        },
        label = "tab_pill_bg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color.Black
            isSelected -> TvTextPrimary
            else -> TvTextSecondary
        },
        label = "tab_pill_text"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("tab_${tab.name.lowercase()}")
    ) {
        val icon = when (tab) {
            TvNavTab.SEARCH -> Icons.Default.Search
            TvNavTab.FOR_YOU -> Icons.Default.Home
            TvNavTab.MOVIES -> Icons.Default.Movie
            TvNavTab.APPS -> Icons.Outlined.Apps
            TvNavTab.LIBRARY -> Icons.Default.VideoLibrary
        }
        Icon(
            imageVector = icon,
            contentDescription = tab.title,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = tab.title,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TvIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isFocused) TvFocusBorder else TvSurfaceVariant)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .scale(if (isFocused) 1.15f else 1.0f)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        icon()
    }
}
