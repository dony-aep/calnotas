package com.donyaep.calnotas.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository
) : ViewModel() {

    // AppCompat's per-app language store is process-local and always correct synchronously,
    // so it's used directly instead of a DataStore Flow (avoids the cold-start default-then-flip
    // gap that previously forced a full Activity recreate on every launch).
    private val _languageCode = MutableStateFlow(currentLanguageCode())

    val uiState: StateFlow<AppSettingsUiState> = combine(
        userPreferencesRepository.themeMode,
        _languageCode
    ) { themeMode, languageCode ->
        AppSettingsUiState(
            themeMode = ThemeModePreference.fromKey(themeMode),
            languageCode = languageCode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettingsUiState(
            themeMode = ThemeModePreference.fromKey(userPreferencesRepository.currentThemeModeSync()),
            languageCode = _languageCode.value
        )
    )

    init {
        // One-time migration: earlier versions only persisted the language choice in DataStore.
        // AppCompat's own auto-store starts empty for existing installs, so seed it once from
        // the legacy value to avoid silently reverting an existing user's language choice.
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            viewModelScope.launch {
                val legacyCode = userPreferencesRepository.languageCode.first()
                if (legacyCode != "system") {
                    setLanguageCode(legacyCode)
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeModePreference) {
        viewModelScope.launch {
            // Defer the native day/night switch by a frame so a dialog dismissing right now
            // (the theme picker) finishes its exit-animation snapshot against the still-current
            // theme, instead of racing the switch and freezing mid-fade on a mismatched frame.
            delay(16)
            AppCompatDelegate.setDefaultNightMode(mode.toAppCompatNightMode())
            userPreferencesRepository.setThemeMode(mode.key)
        }
    }

    fun setLanguageCode(code: String) {
        val locales = if (code == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(code)
        }
        AppCompatDelegate.setApplicationLocales(locales)
        _languageCode.value = code
    }

    private fun currentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) "system" else locales.toLanguageTags()
    }
}
