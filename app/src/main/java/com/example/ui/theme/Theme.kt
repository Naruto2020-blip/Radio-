package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = MatteSlate,
    onSurfaceVariant = OnSurfaceDark
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // We force our beautiful dark retro-modern theme to deliver an immersive receiver-inspired visual design
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
