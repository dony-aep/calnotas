package com.donyaep.calnotas.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.donyaep.calnotas.data.local.PreferenceKeys
import com.donyaep.calnotas.data.local.userPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserPreferencesRepository(
    private val context: Context
) {
    private val dataStore = context.userPreferencesDataStore

    // Synchronous mirror of theme_mode so the very first Compose frame can render with
    // the persisted theme instead of a hardcoded default (avoids a startup flash).
    private val syncPrefs = context.getSharedPreferences("calnotas_sync_prefs", Context.MODE_PRIVATE)

    // El tema elegido vive en memoria y se comparte con toda la app: al tocarlo, la pantalla se
    // redibuja en el mismo cuadro, sin esperar a que DataStore escriba en disco y lo vuelva a
    // emitir (eso tardaba entre 70 y 180 ms). DataStore solo se lee una vez, al arrancar.
    private val _themeMode = MutableStateFlow(currentThemeModeSync())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        val initial = _themeMode.value
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val stored = dataStore.data.first()[PreferenceKeys.ThemeMode] ?: "system"
            syncPrefs.edit().putString(KEY_THEME_MODE, stored).apply()
            // Si el usuario ya eligió otro tema mientras se leía el disco, gana su elección.
            _themeMode.compareAndSet(initial, stored)
        }
    }

    val languageCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.LanguageCode] ?: "system"
    }

    val customCalculatorData: Flow<String?> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.CustomCalculatorData]
    }

    // Se lee al arrancar y queda en memoria para que la calculadora personalizada pinte su
    // primer cuadro con lo guardado, en vez de su estado vacío mientras entra deslizando.
    // Null solo antes de la primera lectura; "" si no hay nada guardado.
    val customCalculatorDataSnapshot: StateFlow<String?> = customCalculatorData
        .map { it.orEmpty() }
        .stateIn(CoroutineScope(SupervisorJob() + Dispatchers.IO), SharingStarted.Eagerly, null)

    fun currentThemeModeSync(): String = syncPrefs.getString(KEY_THEME_MODE, "system") ?: "system"

    suspend fun setThemeMode(value: String) {
        _themeMode.value = value
        syncPrefs.edit().putString(KEY_THEME_MODE, value).apply()
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.ThemeMode] = value
        }
    }

    suspend fun setLanguageCode(value: String) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.LanguageCode] = value
        }
    }

    suspend fun setCustomCalculatorData(value: String) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.CustomCalculatorData] = value
        }
    }

    suspend fun clearCustomCalculatorData() {
        dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.CustomCalculatorData)
        }
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
