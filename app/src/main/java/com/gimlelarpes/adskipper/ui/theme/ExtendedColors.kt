package com.gimlelarpes.adskipper.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val surfaceHighContrast: Color,
)

val LocalExtendedColorScheme = staticCompositionLocalOf {
    ExtendedColors(
        surfaceHighContrast = Color.Unspecified,
    )
}