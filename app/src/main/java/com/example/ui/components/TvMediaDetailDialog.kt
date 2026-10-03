package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MediaItem
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.TvGoldBadge
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceHighlight
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun TvMediaDetailDialog(
    item: MediaItem,
    isWatchlisted: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TvSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceHighlight),
            modifier = Modifier
                .width(620.dp)
                .testTag("media_detail_dialog")
        ) {
            Column {
                // Header Backdrop Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.backdropResId),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x33000000), Color(0xAA141822), TvSurface)
                                )
                            )
                    )

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "關閉",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x88000000))
                                .padding(4.dp)
                        )
                    }

                    // Title & Badges over image bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = item.provider.uppercase(),
                            color = GoogleBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = item.title,
                            color = TvTextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Details body
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Rating & Year row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2B2516))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TvGoldBadge,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${item.rating} (${item.ratingSource})",
                                color = TvGoldBadge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(text = item.year, color = TvTextSecondary, fontSize = 13.sp)
                        Text(text = "•", color = TvTextSecondary, fontSize = 13.sp)
                        Text(text = item.durationOrEpisodes, color = TvTextSecondary, fontSize = 13.sp)

                        item.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TvSurfaceHighlight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = TvTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = item.overview,
                        color = TvTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                                onPlay()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_play_in_detail")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("立即播放預告片", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onToggleWatchlist,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_watchlist_in_detail")
                        ) {
                            Icon(
                                if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isWatchlisted) GoogleBlue else TvTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isWatchlisted) "已在待播清單" else "加入待播清單",
                                color = TvTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
