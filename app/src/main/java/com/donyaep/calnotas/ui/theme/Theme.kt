package com.donyaep.calnotas.ui.theme

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalAppDarkTheme = compositionLocalOf { false }

@Composable
@ReadOnlyComposable
fun isAppInDarkTheme(): Boolean = LocalAppDarkTheme.current

@Composable
fun CalNotasTheme(
    useDarkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // En «Predeterminado del sistema» se pregunta al sistema y no a la configuración de la
    // actividad: al volver de un tema forzado, AppCompat no le hace llegar a Compose el uiMode
    // del sistema (la actividad declara uiMode en configChanges) y la app se quedaba en el tema
    // anterior hasta reiniciarla. La configuración actual solo sirve de clave, para recalcular
    // cuando el sistema cambia de modo con la app abierta.
    val configuration = LocalConfiguration.current
    val darkTheme = remember(useDarkTheme, configuration) {
        useDarkTheme ?: ((Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES)
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // material3 solo trae la variante expresiva clara; para el modo oscuro el par es darkColorScheme().
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
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            typography = AppTypography,
            content = content
        )
    }
}
