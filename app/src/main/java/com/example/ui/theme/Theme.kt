package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3F0000),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = YouTubeBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003258),
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = YouTubeGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF063B16),
    onTertiaryContainer = Color(0xFFB7F3C0),
    background = YouTubeDarkBg,
    onBackground = TextPrimaryDark,
    surface = YouTubeDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = YouTubeDarkChip,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF3F3F3F)
)

private val LightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF065FD4),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = YouTubeGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB7F3C0),
    onTertiaryContainer = Color(0xFF002109),
    background = YouTubeLightBg,
    onBackground = TextPrimaryLight,
    surface = YouTubeLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = YouTubeLightChip,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD9D9D9)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
