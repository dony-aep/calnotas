package com.donyaep.calnotas.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "calnotas_preferences"
)

object PreferenceKeys {
    val ThemeMode = stringPreferencesKey("theme_mode")
    val LanguageCode = stringPreferencesKey("language_code")
    val CustomCalculatorData = stringPreferencesKey("custom_calculator_data")
}
