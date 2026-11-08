package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.designsystem.theme.StatusColors

@Composable
internal fun SignalLostWarning(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painterResource(R.drawable.ic_warning),
            contentDescription = null,
            tint = StatusColors.Warning,
            modifier = Modifier.size(24.dp),
        )
        Text(
            stringResource(R.string.signal_lost),
            style = HmiTextStyles.Pill,
            color = StatusColors.Warning
        )
    }
}
