package com.donyaep.calnotas.ui.settings

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
