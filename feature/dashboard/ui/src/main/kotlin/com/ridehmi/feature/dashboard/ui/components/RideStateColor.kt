package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.ui.graphics.Color
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.RideState

internal fun RideState.accentColor(): Color = when (this) {
    RideState.READY, RideState.RIDING -> StatusColors.Ok
    RideState.ACCELERATING -> StatusColors.Accelerating
    RideState.MOTOR_DISABLED -> StatusColors.Danger
    RideState.PARKED, RideState.UNKNOWN -> StatusColors.Inactive
}
