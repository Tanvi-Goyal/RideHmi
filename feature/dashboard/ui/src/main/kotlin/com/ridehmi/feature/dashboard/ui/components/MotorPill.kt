package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.MotorState

@Composable
internal fun MotorPill(motor: MotorState?, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.motor_label)
    val value = stringResource(
        when (motor) {
            MotorState.ON -> R.string.motor_on
            MotorState.OFF -> R.string.motor_off
            null -> R.string.value_unknown
        },
    )
    val on = motor == MotorState.ON
    val color = if (on) StatusColors.Ok else StatusColors.Inactive

    Row(
        modifier = modifier
            .clearAndSetSemantics { contentDescription = "$label $value" }
            .background(
                if (on) StatusColors.OkContainer
                else StatusColors.InactiveContainer, CircleShape
            )
            .padding(horizontal = 26.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painterResource(R.drawable.ic_power),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Text(value, style = HmiTextStyles.Pill, color = color)
    }
}
