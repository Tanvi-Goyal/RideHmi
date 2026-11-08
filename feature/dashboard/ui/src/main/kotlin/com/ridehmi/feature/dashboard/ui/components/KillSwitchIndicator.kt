package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.StatusColors
import com.ridehmi.core.model.KillSwitchState

@Composable
internal fun KillSwitchIndicator(
    killSwitch: KillSwitchState?,
    modifier: Modifier = Modifier
) {
    val (value, color, container) = when (killSwitch) {
        KillSwitchState.ACTIVE -> Triple(
            R.string.kill_switch_active,
            StatusColors.Danger,
            StatusColors.DangerContainer
        )

        KillSwitchState.INACTIVE -> Triple(
            R.string.kill_switch_inactive,
            StatusColors.Inactive,
            Color.Transparent
        )

        null -> Triple(R.string.value_unknown, StatusColors.Inactive, Color.Transparent)
    }

    StatusIndicator(
        label = stringResource(R.string.kill_switch_label),
        value = stringResource(value),
        icon = R.drawable.ic_warning,
        color = color,
        containerColor = container,
        modifier = modifier,
    )
}
