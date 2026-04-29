package com.donyaep.calnotas

import android.app.Application
import com.donyaep.calnotas.data.AppContainer

class CalNotasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
    }
}
