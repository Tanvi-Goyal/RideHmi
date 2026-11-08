package com.ridehmi.feature.dashboard.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.KillSwitchState

@Composable
private fun AlertCard(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    iconColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(44.dp)
        )

        val valueStyle = if (value == stringResource(R.string.value_unknown)) {
            HmiTextStyles.AlertValue.copy(letterSpacing = 4.sp)
        } else {
            HmiTextStyles.AlertValue
        }

        Column {
            Text(label, style = HmiTextStyles.AlertLabel, color = textColor)
            Text(value, style = valueStyle, color = textColor)
        }
    }
}

@Composable
internal fun KillSwitchAlert(
    killSwitch: KillSwitchState?,
    modifier: Modifier = Modifier
) {
    val label = stringResource(R.string.kill_switch_label)
    val value = stringResource(
        when (killSwitch) {
            KillSwitchState.ACTIVE -> R.string.kill_switch_active
            KillSwitchState.INACTIVE -> R.string.kill_switch_inactive
            null -> R.string.value_unknown
        },
    )
    val active = killSwitch == KillSwitchState.ACTIVE

    AlertCard(
        icon = R.drawable.ic_bolt,
        label = label,
        value = value,
        iconColor = if (active) StatusColors.Danger else StatusColors.Inactive,
        textColor = if (active) Color.White else StatusColors.Inactive,
        modifier = modifier.clearAndSetSemantics { contentDescription = "$label $value" },
    )
}

@Composable
internal fun SignalLostAlert(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.signal_lost)
    AlertCard(
        icon = R.drawable.ic_warning,
        label = stringResource(R.string.signal_lost_label),
        value = stringResource(R.string.signal_lost_value),
        iconColor = StatusColors.Warning,
        textColor = StatusColors.Warning,
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
    )
}
