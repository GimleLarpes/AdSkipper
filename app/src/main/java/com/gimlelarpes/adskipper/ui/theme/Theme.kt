package com.gimlelarpes.adskipper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Blue40,
    error = Error40,

    onPrimary = Green20,
    onSecondary = GreenGrey20,
    onTertiary = Blue20,
    onError = Error20,

    primaryContainer = Green30,
    secondaryContainer = GreenGrey30,
    tertiaryContainer = Blue30,
    errorContainer = Error30,

    onPrimaryContainer = Green90,
    onSecondaryContainer = GreenGrey90,
    onTertiaryContainer = Blue90,
    onErrorContainer = Error90,

    inversePrimary = Green80,

    surfaceDim = DarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = Blue80,
    error = Error80,

    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onError = White,

    primaryContainer = Green90,
    secondaryContainer = GreenGrey90,
    tertiaryContainer = Blue90,
    errorContainer = Error90,

    onPrimaryContainer = Green10,
    onSecondaryContainer = GreenGrey10,
    onTertiaryContainer = Blue10,
    onErrorContainer = Error10,

    inversePrimary = Green80,

    surfaceDim = LightGrey

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AdSkipperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}