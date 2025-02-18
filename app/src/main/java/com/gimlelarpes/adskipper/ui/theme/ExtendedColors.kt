package com.gimlelarpes.adskipper.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.gimlelarpes.adskipper.ui.theme.ExtendedColors

@Immutable
data class ExtendedColors(
    val surfaceHighContrast: Color
)

val LocalExtendedColorScheme = staticCompositionLocalOf {
    ExtendedColors(
        surfaceHighContrast = Color.Unspecified,
    )
}