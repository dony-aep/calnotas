package com.donyaep.calnotas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalAppDarkTheme = compositionLocalOf { false }

@Composable
@ReadOnlyComposable
fun isAppInDarkTheme(): Boolean = LocalAppDarkTheme.current

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalNotasTheme(
    useDarkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = useDarkTheme ?: isSystemInDarkTheme()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // NOTE: expressiveDarkColorScheme() does not exist yet in material3 1.5.0-alpha08
        // (the version pinned in gradle/libs.versions.toml) — only the light variant ships
        // so far. Falls back to the standard darkColorScheme() until it's available.
        darkTheme -> darkColorScheme()
        else -> expressiveLightColorScheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppDarkTheme provides darkTheme) {
        MaterialExpressiveTheme(
            colorScheme = animateColorScheme(colorScheme),
            motionScheme = MotionScheme.expressive(),
            typography = AppTypography,
            content = content
        )
    }
}

// Switching between light/dark/dynamic ColorScheme objects otherwise swaps every color
// instantly (no cross-fade) since the base MaterialTheme.colorScheme roles aren't animated
// by default. Cross-fading every role here gives smooth theme transitions app-wide instead
// of a hard cut, matching the spring-based motion used elsewhere (e.g. result cards).
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val animationSpec = spring<Color>(stiffness = Spring.StiffnessLow)

    @Composable
    fun animate(color: Color) = animateColorAsState(color, animationSpec).value

    return ColorScheme(
        primary = animate(target.primary),
        onPrimary = animate(target.onPrimary),
        primaryContainer = animate(target.primaryContainer),
        onPrimaryContainer = animate(target.onPrimaryContainer),
        inversePrimary = animate(target.inversePrimary),
        secondary = animate(target.secondary),
        onSecondary = animate(target.onSecondary),
        secondaryContainer = animate(target.secondaryContainer),
        onSecondaryContainer = animate(target.onSecondaryContainer),
        tertiary = animate(target.tertiary),
        onTertiary = animate(target.onTertiary),
        tertiaryContainer = animate(target.tertiaryContainer),
        onTertiaryContainer = animate(target.onTertiaryContainer),
        background = animate(target.background),
        onBackground = animate(target.onBackground),
        surface = animate(target.surface),
        onSurface = animate(target.onSurface),
        surfaceVariant = animate(target.surfaceVariant),
        onSurfaceVariant = animate(target.onSurfaceVariant),
        surfaceTint = animate(target.surfaceTint),
        inverseSurface = animate(target.inverseSurface),
        inverseOnSurface = animate(target.inverseOnSurface),
        error = animate(target.error),
        onError = animate(target.onError),
        errorContainer = animate(target.errorContainer),
        onErrorContainer = animate(target.onErrorContainer),
        outline = animate(target.outline),
        outlineVariant = animate(target.outlineVariant),
        scrim = animate(target.scrim),
        surfaceBright = animate(target.surfaceBright),
        surfaceDim = animate(target.surfaceDim),
        surfaceContainer = animate(target.surfaceContainer),
        surfaceContainerHigh = animate(target.surfaceContainerHigh),
        surfaceContainerHighest = animate(target.surfaceContainerHighest),
        surfaceContainerLow = animate(target.surfaceContainerLow),
        surfaceContainerLowest = animate(target.surfaceContainerLowest),
        primaryFixed = animate(target.primaryFixed),
        primaryFixedDim = animate(target.primaryFixedDim),
        onPrimaryFixed = animate(target.onPrimaryFixed),
        onPrimaryFixedVariant = animate(target.onPrimaryFixedVariant),
        secondaryFixed = animate(target.secondaryFixed),
        secondaryFixedDim = animate(target.secondaryFixedDim),
        onSecondaryFixed = animate(target.onSecondaryFixed),
        onSecondaryFixedVariant = animate(target.onSecondaryFixedVariant),
        tertiaryFixed = animate(target.tertiaryFixed),
        tertiaryFixedDim = animate(target.tertiaryFixedDim),
        onTertiaryFixed = animate(target.onTertiaryFixed),
        onTertiaryFixedVariant = animate(target.onTertiaryFixedVariant)
    )
}
