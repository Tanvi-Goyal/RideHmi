package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.util.lerp
import com.ridehmi.core.designsystem.theme.DeckColors
import com.ridehmi.core.model.RideState

private const val FULL_SCALE_KMH = 150f

@Composable
internal fun SpeedDeck(
    speedKmh: Int?,
    rideState: RideState,
    modifier: Modifier = Modifier
) {
    val fill by animateFloatAsState(
        targetValue = ((speedKmh ?: 0) / FULL_SCALE_KMH).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "deckFill",
    )
    val accent by animateColorAsState(
        targetValue = rideState.accentColor(),
        animationSpec = tween(durationMillis = 400),
        label = "deckAccent",
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height

        drawPath(
            trapezoid(
                top = 0.30f * h,
                bottom = 0.72f * h,
                topInset = 0.20f * w,
                bottomInset = 0.02f * w
            ),
            Brush.verticalGradient(
                listOf(DeckColors.Top, DeckColors.Bottom),
                startY = 0.30f * h,
                endY = 0.72f * h
            ),
        )
        drawPerforations()
        drawPath(
            trapezoid(
                top = 0f,
                bottom = 0.36f * h,
                topInset = 0.37f * w,
                bottomInset = 0.27f * w
            ),
            Brush.verticalGradient(
                listOf(DeckColors.Raised, DeckColors.Top),
                startY = 0f,
                endY = 0.36f * h
            ),
        )
        drawPath(
            trapezoid(
                top = 0.72f * h,
                bottom = 0.84f * h,
                topInset = 0.02f * w,
                bottomInset = 0.05f * w
            ),
            DeckColors.Face,
        )
        drawLightBar(fill, accent)
    }
}

private fun DrawScope.drawLightBar(fill: Float, accent: Color) {
    val w = size.width
    val h = size.height
    val top = 0.745f * h
    val height = 0.06f * h
    val halfTrack = 0.45f * w
    val radius = CornerRadius(height / 2)

    drawRoundRect(
        color = accent.copy(alpha = 0.18f),
        topLeft = Offset(w / 2 - halfTrack, top),
        size = Size(halfTrack * 2, height),
        cornerRadius = radius,
    )
    if (fill <= 0f) return

    val half = halfTrack * fill
    listOf(18f to 0.08f, 10f to 0.14f, 4f to 0.25f).forEach { (spread, alpha) ->
        drawRoundRect(
            color = accent.copy(alpha = alpha),
            topLeft = Offset(w / 2 - half - spread, top - spread),
            size = Size(half * 2 + spread * 2, height + spread * 2),
            cornerRadius = CornerRadius(height / 2 + spread),
        )
    }
    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(accent.copy(alpha = 0.55f), accent, accent.copy(alpha = 0.55f)),
            startX = w / 2 - half,
            endX = w / 2 + half,
        ),
        topLeft = Offset(w / 2 - half, top),
        size = Size(half * 2, height),
        cornerRadius = radius,
    )
}

private fun DrawScope.drawPerforations() {
    val w = size.width
    val h = size.height
    val radius = 0.006f * h
    val step = radius * 5
    for (row in 0 until 4) {
        val y = (0.46f + row * 0.06f) * h
        val progress = (y - 0.30f * h) / (0.42f * h)
        var x = lerp(0.20f * w, 0.02f * w, progress) + step * 2
        while (x < 0.30f * w) {
            drawCircle(DeckColors.Dots, radius, Offset(x, y))
            drawCircle(DeckColors.Dots, radius, Offset(w - x, y))
            x += step
        }
    }
}

private fun DrawScope.trapezoid(
    top: Float,
    bottom: Float,
    topInset: Float,
    bottomInset: Float
) = Path().apply {
    moveTo(topInset, top)
    lineTo(size.width - topInset, top)
    lineTo(size.width - bottomInset, bottom)
    lineTo(bottomInset, bottom)
    close()
}
