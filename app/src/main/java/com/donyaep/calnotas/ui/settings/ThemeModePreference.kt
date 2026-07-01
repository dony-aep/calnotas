package com.donyaep.calnotas.ui.settings

import androidx.appcompat.app.AppCompatDelegate

enum class ThemeModePreference(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(value: String): ThemeModePreference {
            return entries.firstOrNull { it.key == value } ?: SYSTEM
        }
    }
}

fun ThemeModePreference.toAppCompatNightMode(): Int = when (this) {
    ThemeModePreference.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    ThemeModePreference.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    ThemeModePreference.DARK -> AppCompatDelegate.MODE_NIGHT_YES
}
