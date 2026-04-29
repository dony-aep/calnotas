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

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.ThemeMode] ?: "system"
    }

    val languageCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.LanguageCode] ?: "system"
    }

    val customCalculatorData: Flow<String?> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.CustomCalculatorData]
    }

    suspend fun setThemeMode(value: String) {
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
}
