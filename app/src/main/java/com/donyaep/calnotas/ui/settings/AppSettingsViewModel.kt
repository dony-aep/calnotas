package com.donyaep.calnotas.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
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
            themeMode = ThemeModePreference.fromKey(userPreferencesRepository.themeMode.value),
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
        // viewModelScope corre en Main.immediate: el tema en memoria cambia antes de volver de
        // launch, y solo la escritura en disco queda pendiente. AppCompat no se toca aquí: su
        // cambio de configuración bloqueaba el hilo principal ~115 ms justo después del cambio.
        // MainActivity se lo pasa en onStop, cuando ya no se ve.
        viewModelScope.launch {
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
