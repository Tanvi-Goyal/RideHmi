package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.MotorState

@Composable
internal fun MotorIndicator(
    motor: MotorState?,
    modifier: Modifier = Modifier
) {
    val (value, color, container) = when (motor) {
        MotorState.ON -> Triple(R.string.motor_on, StatusColors.Ok, StatusColors.OkContainer)
        MotorState.OFF -> Triple(R.string.motor_off, StatusColors.Inactive, Color.Transparent)
        null -> Triple(R.string.value_unknown, StatusColors.Inactive, Color.Transparent)
    }

    StatusIndicator(
        label = stringResource(R.string.motor_label),
        value = stringResource(value),
        icon = R.drawable.ic_power,
        color = color,
        containerColor = container,
        modifier = modifier,
    )
}
