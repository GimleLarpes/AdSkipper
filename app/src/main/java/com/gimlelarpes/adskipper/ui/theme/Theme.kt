package com.gimlelarpes.adskipper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary40,
    secondary = Secondary40,
    tertiary = Tertriary40,
    error = Error40,

    onPrimary = Primary20,
    onSecondary = Secondary20,
    onTertiary = Tertriary20,
    onError = Error20,

    primaryContainer = Primary30,
    secondaryContainer = Secondary30,
    tertiaryContainer = Tertriary30,
    errorContainer = Error30,

    onPrimaryContainer = Primary90,
    onSecondaryContainer = Secondary90,
    onTertiaryContainer = Tertriary90,
    onErrorContainer = Error90,

    inversePrimary = Primary80,

    surfaceDim = DarkGrey,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary80,
    secondary = Secondary80,
    tertiary = Tertriary80,
    error = Error80,

    onPrimary = Grey,
    onSecondary = Grey,
    onTertiary = Grey,
    onError = Grey,

    primaryContainer = Primary90,
    secondaryContainer = Secondary90,
    tertiaryContainer = Tertriary90,
    errorContainer = Error90,

    onPrimaryContainer = Primary10,
    onSecondaryContainer = Secondary10,
    onTertiaryContainer = Tertriary10,
    onErrorContainer = Error10,

    inversePrimary = Primary80,

    surfaceDim = LightGrey,

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
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Implement extended colors
    val extendedColors = ExtendedColors(
        surfaceHighContrast = if (darkTheme) DarkGrey else White,
    )

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}