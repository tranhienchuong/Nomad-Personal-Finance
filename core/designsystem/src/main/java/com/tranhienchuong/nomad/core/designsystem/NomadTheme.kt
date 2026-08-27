package com.tranhienchuong.nomad.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = NomadLightPrimary,
    onPrimary = NomadLightOnPrimary,
    secondary = NomadLightSecondary,
    background = NomadLightBackground,
    surface = NomadLightSurface,
)

private val DarkColors = darkColorScheme(
    primary = NomadDarkPrimary,
    onPrimary = NomadDarkOnPrimary,
    secondary = NomadDarkSecondary,
    background = NomadDarkBackground,
    surface = NomadDarkSurface,
)

@Composable
fun NomadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NomadTypography,
        shapes = NomadShapes,
        content = content,
    )
}
