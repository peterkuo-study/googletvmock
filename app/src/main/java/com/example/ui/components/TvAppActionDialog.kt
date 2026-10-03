package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InstalledApp
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceHighlight
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun TvAppActionDialog(
    app: InstalledApp,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenAssignCategories: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TvSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceHighlight),
            modifier = Modifier
                .width(380.dp)
                .testTag("app_action_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Text(
                    text = app.appName,
                    color = TvTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = app.packageName,
                    color = TvTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action 1: Launch App
                TvActionItem(
                    icon = Icons.Default.PlayArrow,
                    title = "開啟應用程式",
                    tint = GoogleBlue,
                    onClick = {
                        onDismiss()
                        onLaunch()
                    },
                    testTag = "action_launch"
                )

                // Action 2: Toggle Favorite / Pin to Home
                TvActionItem(
                    icon = if (app.isFavorite) Icons.Default.StarOutline else Icons.Default.Star,
                    title = if (app.isFavorite) "從首頁常用移除" else "釘選至首頁常用",
                    tint = TvGoldBadge,
                    onClick = {
                        onDismiss()
                        onToggleFavorite()
                    },
                    testTag = "action_toggle_fav"
                )

                // Action 3: Assign Custom Categories
                TvActionItem(
                    icon = Icons.Default.Category,
                    title = "設定分類標籤...",
                    tint = GoogleBlue,
                    onClick = {
                        onDismiss()
                        onOpenAssignCategories()
                    },
                    testTag = "action_assign_category"
                )

                // Action 4: System App Info
                TvActionItem(
                    icon = Icons.Default.Info,
                    title = "應用程式詳細資訊",
                    tint = TvTextSecondary,
                    onClick = {
                        onDismiss()
                        openAppDetails(context, app.packageName)
                    },
                    testTag = "action_app_info"
                )

                // Action 5: Uninstall (if not system app)
                if (!app.isSystemApp) {
                    TvActionItem(
                        icon = Icons.Default.Delete,
                        title = "解除安裝此程式",
                        tint = GoogleRed,
                        onClick = {
                            onDismiss()
                            uninstallApp(context, app.packageName)
                        },
                        testTag = "action_uninstall"
                    )
                }
            }
        }
    }
}

@Composable
private fun TvActionItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) TvSurfaceHighlight else Color.Transparent)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isFocused) Color.White else tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = if (isFocused) Color.White else TvTextPrimary,
            fontSize = 15.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private fun openAppDetails(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

private fun uninstallApp(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
