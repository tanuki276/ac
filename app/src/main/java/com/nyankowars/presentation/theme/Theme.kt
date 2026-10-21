package com.nyankowars.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurplePrimary = Color(0xFF6D4AFF)
val PinkPrimary = Color(0xFFFF5C8A)

private val LightColors = lightColorScheme(
    primary = PurplePrimary,
    secondary = PinkPrimary
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB8A7FF),
    secondary = Color(0xFFFFA8BE)
)

@Composable
fun NyankoWarsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
