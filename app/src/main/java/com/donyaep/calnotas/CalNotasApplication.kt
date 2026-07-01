package com.donyaep.calnotas

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.ui.settings.ThemeModePreference
import com.donyaep.calnotas.ui.settings.toAppCompatNightMode

class CalNotasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)

        // Apply the persisted theme's day/night mode before any Activity is created, so the
        // native starting window (drawn by the OS before Compose renders) resolves the right
        // light/dark resource variant instead of following the system default.
        val themeMode = ThemeModePreference.fromKey(AppContainer.userPreferencesRepository.currentThemeModeSync())
        AppCompatDelegate.setDefaultNightMode(themeMode.toAppCompatNightMode())
    }
}
