package com.ridehmi.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HmiColorScheme = darkColorScheme(
    background = Black,
    onBackground = White,
    surface = Surface,
    onSurface = White,
    onSurfaceVariant = OnSurfaceMuted,
    primary = StatusColors.Ok,
    error = StatusColors.Danger,
)

@Composable
fun HmiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HmiColorScheme,
        typography = HmiTypography,
        content = content,
    )
}
