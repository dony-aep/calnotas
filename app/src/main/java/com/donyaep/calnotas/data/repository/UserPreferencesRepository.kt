package com.donyaep.calnotas.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.donyaep.calnotas.data.local.PreferenceKeys
import com.donyaep.calnotas.data.local.userPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val context: Context
) {
    private val dataStore = context.userPreferencesDataStore

    // Synchronous mirror of theme_mode so the very first Compose frame can render with
    // the persisted theme instead of a hardcoded default (avoids a startup flash).
    private val syncPrefs = context.getSharedPreferences("calnotas_sync_prefs", Context.MODE_PRIVATE)

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        val value = prefs[PreferenceKeys.ThemeMode] ?: "system"
        syncPrefs.edit().putString(KEY_THEME_MODE, value).apply()
        value
    }

    val languageCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.LanguageCode] ?: "system"
    }

    val customCalculatorData: Flow<String?> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.CustomCalculatorData]
    }

    fun currentThemeModeSync(): String = syncPrefs.getString(KEY_THEME_MODE, "system") ?: "system"

    suspend fun setThemeMode(value: String) {
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

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
