package com.donyaep.calnotas.ui.settings

data class AppSettingsUiState(
    val themeMode: ThemeModePreference = ThemeModePreference.SYSTEM,
    val languageCode: String = "system"
)
