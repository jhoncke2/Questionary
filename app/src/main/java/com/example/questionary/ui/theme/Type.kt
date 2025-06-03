package com.example.questionary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.questionary.R

val CustomFontFamily = FontFamily(
    Font(R.font.press_start_2p)
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = CustomFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = CustomFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = CustomFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = CustomFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = CustomFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = CustomFontFamily),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Bold
    ),
    titleSmall = baseline.titleSmall.copy(fontFamily = CustomFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = CustomFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = CustomFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = CustomFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = CustomFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = CustomFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = CustomFontFamily),
)

