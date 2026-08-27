package com.tranhienchuong.nomad.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = NomadLightPrimary,
    onPrimary = NomadLightOnPrimary,
    primaryContainer = NomadLightPrimaryContainer,
    onPrimaryContainer = NomadLightOnPrimaryContainer,
    secondary = NomadLightSecondary,
    onSecondary = NomadLightOnSecondary,
    secondaryContainer = NomadLightSecondaryContainer,
    onSecondaryContainer = NomadLightOnSecondaryContainer,
    background = NomadLightBackground,
    surface = NomadLightSurface,
    outline = NomadLightOutline,
    error = NomadLightError,
)

private val DarkColors = darkColorScheme(
    primary = NomadDarkPrimary,
    onPrimary = NomadDarkOnPrimary,
    primaryContainer = NomadDarkPrimaryContainer,
    onPrimaryContainer = NomadDarkOnPrimaryContainer,
    secondary = NomadDarkSecondary,
    onSecondary = NomadDarkOnSecondary,
    secondaryContainer = NomadDarkSecondaryContainer,
    onSecondaryContainer = NomadDarkOnSecondaryContainer,
    background = NomadDarkBackground,
    surface = NomadDarkSurface,
    outline = NomadDarkOutline,
    error = NomadDarkError,
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
