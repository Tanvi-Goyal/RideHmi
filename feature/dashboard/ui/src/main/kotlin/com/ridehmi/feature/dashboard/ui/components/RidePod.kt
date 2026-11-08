package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.DeckColors
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.model.RideState

@Composable
internal fun RidePod(rideState: RideState, modifier: Modifier = Modifier) {
    val accent = rideState.accentColor()
    Box(
        modifier.size(width = 340.dp, height = 76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier
                .size(
                    width = 340.dp,
                    height = 76.dp
                )
        ) {
            val w = size.width
            val h = size.height
            val pod = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(0.88f * w, h)
                lineTo(0.12f * w, h)
                close()
            }

            drawPath(
                pod,
                Brush.verticalGradient(listOf(DeckColors.Top, DeckColors.Bottom))
            )

            drawRoundRect(
                color = accent,
                topLeft = Offset(0.1f * w, 0f),
                size = Size(0.8f * w, 5.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
            )
        }
        rideState.label()?.let { label ->
            Text(
                text = stringResource(label).uppercase(),
                style = HmiTextStyles.Pill,
                color = if (rideState == RideState.PARKED)
                    MaterialTheme.colorScheme.onBackground
                else accent,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(DeckColors.Bottom, CircleShape)
                    .padding(horizontal = 22.dp, vertical = 8.dp),
            )
        }
    }
}

private fun RideState.label(): Int? = when (this) {
    RideState.PARKED -> R.string.ride_state_parked
    RideState.READY -> R.string.ride_state_ready
    RideState.RIDING -> R.string.ride_state_riding
    RideState.ACCELERATING -> R.string.ride_state_accelerating
    RideState.MOTOR_DISABLED -> R.string.ride_state_motor_disabled
    RideState.UNKNOWN -> null
}
