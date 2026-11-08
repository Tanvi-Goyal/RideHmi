package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.RideState

@Composable
internal fun RideStateBadge(
    rideState: RideState,
    modifier: Modifier = Modifier
) {
    val label = when (rideState) {
        RideState.PARKED -> R.string.ride_state_parked
        RideState.READY -> R.string.ride_state_ready
        RideState.RIDING -> R.string.ride_state_riding
        RideState.ACCELERATING -> R.string.ride_state_accelerating
        RideState.MOTOR_DISABLED -> R.string.ride_state_motor_disabled
        RideState.UNKNOWN -> return
    }
    val color = if (rideState == RideState.MOTOR_DISABLED)
        StatusColors.Danger else StatusColors.Ok

    Text(
        text = stringResource(label).uppercase(),
        style = HmiTextStyles.Pill,
        color = color,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .padding(horizontal = 24.dp, vertical = 8.dp),
    )
}
