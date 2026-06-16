package com.example.fountainofwealth.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    primaryContainer = Mint,
    onPrimaryContainer = EmeraldDark,
    secondary = WarningGold,
    background = AppBackground,
    onBackground = Slate,
    surface = CardWhite,
    onSurface = Slate,
    surfaceVariant = SoftMint,
    onSurfaceVariant = MutedSlate,
    outline = Color(0xFFE2E8F0)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun FountainOfWealthTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}