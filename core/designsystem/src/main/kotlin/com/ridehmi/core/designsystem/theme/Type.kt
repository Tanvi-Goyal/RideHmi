package com.ridehmi.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ridehmi.core.designsystem.R

private fun manrope(weight: FontWeight) = Font(
    resId = R.font.manrope,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val Manrope = FontFamily(
    manrope(FontWeight.Medium),
    manrope(FontWeight.SemiBold),
    manrope(FontWeight.Bold),
    manrope(FontWeight.ExtraBold),
)

private val Default = Typography()

internal val HmiTypography = Typography(
    displayLarge = Default.displayLarge.copy(fontFamily = Manrope),
    displayMedium = Default.displayMedium.copy(fontFamily = Manrope),
    displaySmall = Default.displaySmall.copy(fontFamily = Manrope),
    headlineLarge = Default.headlineLarge.copy(fontFamily = Manrope),
    headlineMedium = Default.headlineMedium.copy(fontFamily = Manrope),
    headlineSmall = Default.headlineSmall.copy(fontFamily = Manrope),
    titleLarge = Default.titleLarge.copy(fontFamily = Manrope),
    titleMedium = Default.titleMedium.copy(fontFamily = Manrope),
    titleSmall = Default.titleSmall.copy(fontFamily = Manrope),
    bodyLarge = Default.bodyLarge.copy(fontFamily = Manrope),
    bodyMedium = Default.bodyMedium.copy(fontFamily = Manrope),
    bodySmall = Default.bodySmall.copy(fontFamily = Manrope),
    labelLarge = Default.labelLarge.copy(fontFamily = Manrope),
    labelMedium = Default.labelMedium.copy(fontFamily = Manrope),
    labelSmall = Default.labelSmall.copy(fontFamily = Manrope),
)

object HmiTextStyles {
    val SpeedValue = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 160.sp,
        letterSpacing = (-4).sp,
    )
    val SpeedUnit = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 6.sp,
    )
    val Pill = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        letterSpacing = 2.sp,
    )
    val AlertLabel = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 20.sp,
    )
    val AlertValue = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 30.sp,
    )
}
