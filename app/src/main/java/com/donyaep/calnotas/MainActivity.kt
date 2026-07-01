package com.donyaep.calnotas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.donyaep.calnotas.ui.navigation.AppNavHost
import com.donyaep.calnotas.ui.settings.AppSettingsViewModel
import com.donyaep.calnotas.ui.settings.ThemeModePreference
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
                AppNavHost()
            }
        }
    }
}
