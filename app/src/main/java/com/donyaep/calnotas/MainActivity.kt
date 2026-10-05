package com.donyaep.calnotas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.donyaep.calnotas.ui.navigation.AppNavHost
import com.donyaep.calnotas.ui.settings.AppSettingsViewModel
import com.donyaep.calnotas.ui.settings.ThemeModePreference
import com.donyaep.calnotas.ui.settings.toAppCompatNightMode
import com.donyaep.calnotas.ui.theme.CalNotasTheme

class MainActivity : AppCompatActivity() {
    private val appSettingsViewModel: AppSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by appSettingsViewModel.uiState.collectAsStateWithLifecycle()

            val useDarkTheme = when (settings.themeMode) {
                ThemeModePreference.SYSTEM -> null
                ThemeModePreference.LIGHT -> false
                ThemeModePreference.DARK -> true
            }

            CalNotasTheme(
                useDarkTheme = useDarkTheme
            ) {
                // Fondo opaco del tema detrás de la navegación: mientras una pantalla desliza sobre
                // otra no debe asomar el fondo de la ventana, que no sigue el color dinámico.
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // El tema elegido en Configuración solo cambia el de Compose. Aquí, con la app fuera de
        // la vista, se le pasa también a AppCompat para que una actividad recreada abra con el
        // fondo nativo correcto; AppCompat lo aplica en onStart, antes del primer cuadro.
        AppCompatDelegate.setDefaultNightMode(appSettingsViewModel.uiState.value.themeMode.toAppCompatNightMode())
    }
}
