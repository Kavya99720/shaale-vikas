package com.shaalevikas.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green700 = Color(0xFF388E3C)
val Green500 = Color(0xFF4CAF50)
val Green100 = Color(0xFFC8E6C9)
val Amber500 = Color(0xFFFFC107)
val Amber700 = Color(0xFFFFA000)
val Orange500 = Color(0xFFFF9800)
val DeepOrange = Color(0xFFE64A19)
val Brown700 = Color(0xFF5D4037)
val BackgroundLight = Color(0xFFF5F5F5)
val SurfaceWhite = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green700,
    secondary = Amber700,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Amber700,
    background = BackgroundLight,
    surface = SurfaceWhite,
    error = DeepOrange,
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C)
)

@Composable
fun ShaaleVikasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
