package com.donyaep.calnotas.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<AppSettingsUiState> = combine(
        userPreferencesRepository.themeMode,
        userPreferencesRepository.languageCode
    ) { themeMode, languageCode ->
        AppSettingsUiState(
            themeMode = ThemeModePreference.fromKey(themeMode),
            languageCode = languageCode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettingsUiState()
    )

    fun setThemeMode(mode: ThemeModePreference) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(mode.key)
        }
    }

    fun setLanguageCode(code: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(code)
        }
    }
}
