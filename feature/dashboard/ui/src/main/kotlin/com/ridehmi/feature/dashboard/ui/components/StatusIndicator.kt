package com.ridehmi.feature.dashboard.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.ridehmi.core.designsystem.theme.HmiTextStyles

/**
 * A labelled status, e.g. "Motor / ON". Color, icon and text all change together, so the
 * status never depends on color alone.
 */
@Composable
internal fun StatusIndicator(
    label: String,
    value: String,
    @DrawableRes icon: Int,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Column(
        modifier = modifier.clearAndSetSemantics { contentDescription = "$label $value" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .background(containerColor, shape)
                .border(1.dp, color, shape)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Text(text = value, style = HmiTextStyles.Pill, color = color)
        }
    }
}
