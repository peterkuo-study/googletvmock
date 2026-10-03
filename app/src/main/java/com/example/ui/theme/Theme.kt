package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TvDarkColorScheme = darkColorScheme(
    primary = GoogleBlue,
    onPrimary = Color.White,
    primaryContainer = TvSurfaceHighlight,
    onPrimaryContainer = TvTextPrimary,
    secondary = GoogleYellow,
    onSecondary = Color.Black,
    secondaryContainer = TvSurfaceVariant,
    onSecondaryContainer = TvTextPrimary,
    tertiary = GoogleRed,
    background = TvBackground,
    onBackground = TvTextPrimary,
    surface = TvSurface,
    onSurface = TvTextPrimary,
    surfaceVariant = TvSurfaceVariant,
    onSurfaceVariant = TvTextSecondary,
    outline = TvSurfaceHighlight
)

@Composable
fun GoogleTvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TvDarkColorScheme,
        typography = Typography,
        content = content
    )
}
