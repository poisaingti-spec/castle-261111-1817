package com.crafted.castle.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MaitisPurplePrimary,
    secondary = MaitisPurpleSecondary,
    tertiary = MaitisPurpleTertiary,
    background = MaitisDarkSurface,
    surface = MaitisDarkSurface,
    onPrimary = MaitisPurpleOnPrimary,
    onSecondary = MaitisPurpleOnSecondary,
    onSurface = MaitisDarkOnSurface,
    surfaceVariant = MaitisDarkSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = MaitisPurplePrimary,
    secondary = MaitisPurpleSecondary,
    tertiary = MaitisPurpleTertiary,
    background = MaitisLightSurface,
    surface = MaitisLightSurface,
    onPrimary = MaitisPurpleOnPrimary,
    onSecondary = MaitisPurpleOnSecondary,
    onSurface = MaitisLightOnSurface,
    surfaceVariant = MaitisLightSurfaceVariant
)

@Composable
fun CastleTheme(
    darkTheme: Boolean = true, // Default to Dark to match Maitis Purple vibe
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}