package com.ridehmi.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles

@Composable
internal fun SpeedReadout(speedKmh: Int?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = speedKmh?.toString() ?: stringResource(R.string.value_unknown),
            style = HmiTextStyles.SpeedValue,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.unit_kmh),
            style = HmiTextStyles.SpeedUnit,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
