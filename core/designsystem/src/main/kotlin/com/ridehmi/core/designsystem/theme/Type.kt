package com.ridehmi.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val HmiTypography = Typography()

object HmiTextStyles {
    val SpeedValue = TextStyle(
        fontSize = 160.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 160.sp
    )
    val SpeedUnit =
        TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 4.sp
        )
    val Pill = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    )
}
