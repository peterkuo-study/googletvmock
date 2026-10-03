package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.ui.theme.TvFocusBorder
import com.example.ui.theme.TvSurfaceVariant

/**
 * TV Focusable container that handles focus scale, glowing border, D-Pad enter, and click.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TvFocusableContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    focusedScale: Float = 1.08f,
    focusedBorderWidth: Dp = 3.dp,
    focusedBorderColor: Color = TvFocusBorder,
    unfocusedBackgroundColor: Color = TvSurfaceVariant,
    focusedBackgroundColor: Color = TvSurfaceVariant,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "tv_focus_scale"
    )

    Surface(
        shape = shape,
        color = if (isFocused) focusedBackgroundColor else unfocusedBackgroundColor,
        border = if (isFocused) BorderStroke(focusedBorderWidth, focusedBorderColor) else null,
        modifier = modifier
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
            .then(
                if (isFocused) Modifier.shadow(16.dp, shape = shape, spotColor = Color.White)
                else Modifier
            )
    ) {
        Box(content = { content(isFocused) })
    }
}
