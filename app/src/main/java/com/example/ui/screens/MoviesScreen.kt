package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.components.TvPosterCard
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvTextPrimary

@Composable
fun MoviesScreen(
    topPicks: List<MediaItem>,
    onSelectMedia: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .testTag("movies_screen"),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            Column {
                Text(
                    text = "電影與熱門節目",
                    color = TvTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "來自各大串流平台的即時精選大作",
                    color = com.example.ui.theme.TvTextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Section 1: Sci-Fi & Adventure
        item {
            TvMediaSection(
                title = "震撼科幻與太空探險",
                items = topPicks,
                onSelectMedia = onSelectMedia
            )
        }

        // Section 2: Blockbuster Action
        item {
            TvMediaSection(
                title = "超級英雄與動作大片",
                items = topPicks.reversed(),
                onSelectMedia = onSelectMedia
            )
        }

        // Section 3: Anime & Nature
        item {
            TvMediaSection(
                title = "極限自然生態與吉卜力風奇幻",
                items = topPicks.shuffled(),
                onSelectMedia = onSelectMedia
            )
        }
    }
}

@Composable
private fun TvMediaSection(
    title: String,
    items: List<MediaItem>,
    onSelectMedia: (MediaItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = TvTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items, key = { "${title}_${it.id}" }) { item ->
                TvPosterCard(
                    item = item,
                    onClick = { onSelectMedia(item) }
                )
            }
        }
    }
}
