package com.donyaep.calnotas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.model.CustomCalculatorData
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Un campo de la calculadora guardada. [position] es su número en pantalla, empezando en 1. */
data class SavedField(val name: String, val position: Int)

class HomeViewModel(
    userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository
) : ViewModel() {

    private val gson = Gson()

    // Parte de lo que ya está en memoria para que el primer cuadro muestre lo guardado.
    val savedFields: StateFlow<List<SavedField>> = userPreferencesRepository.customCalculatorDataSnapshot
        .map(::parse)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            parse(userPreferencesRepository.customCalculatorDataSnapshot.value)
        )

    private fun parse(raw: String?): List<SavedField> {
        if (raw.isNullOrBlank()) return emptyList()
        val data = runCatching { gson.fromJson(raw, CustomCalculatorData::class.java) }.getOrNull()
            ?: return emptyList()
        return data.fields.mapIndexed { index, field -> SavedField(field.name.trim(), index + 1) }
    }
}
