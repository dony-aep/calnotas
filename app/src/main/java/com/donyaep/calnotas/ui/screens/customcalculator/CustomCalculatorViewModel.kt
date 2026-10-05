package com.donyaep.calnotas.ui.screens.customcalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.model.CustomCalculatorData
import com.donyaep.calnotas.data.model.CustomFieldData
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomCalculatorViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository
) : ViewModel() {

    private val gson = Gson()

    // Va antes de _uiState: restore() lo incrementa al construir el estado inicial.
    private var nextFieldId = 1

    // El estado inicial sale de lo que el repositorio ya tiene en memoria, para que el primer
    // cuadro no muestre la calculadora vacía mientras la pantalla entra.
    private val _uiState = MutableStateFlow(
        userPreferencesRepository.customCalculatorDataSnapshot.value?.let(::restore)
            ?: CustomCalculatorUiState()
    )
    val uiState: StateFlow<CustomCalculatorUiState> = _uiState.asStateFlow()

    init {
        // Solo si la lectura que se lanza al arrancar la app todavía no terminó.
        if (userPreferencesRepository.customCalculatorDataSnapshot.value == null) {
            viewModelScope.launch {
                loadSavedData()
            }
        }
        // Reiniciar debe poder borrar lo guardado aunque en pantalla ya no quede ningún campo.
        viewModelScope.launch {
            userPreferencesRepository.customCalculatorDataSnapshot.collect { raw ->
                _uiState.update { it.copy(hasSavedData = !raw.isNullOrEmpty()) }
            }
        }
    }

    fun addField() {
        _uiState.update { state ->
            state.copy(fields = state.fields + CustomFieldUi(id = nextFieldId++))
        }
        recalculate()
    }

    fun removeField(id: Int) {
        _uiState.update { state ->
            state.copy(fields = state.fields.filterNot { it.id == id })
        }
        recalculate()
    }

    fun onNameChanged(id: Int, value: String) {
        updateField(id) { it.copy(name = value) }
    }

    fun onPercentageChanged(id: Int, value: String) {
        val normalized = value.replace(',', '.')
        updateField(id) { it.copy(percentage = normalized) }
        recalculate()
    }

    fun onGradeChanged(id: Int, value: String) {
        val normalized = value.replace(',', '.')
        if (normalized.isBlank()) {
            updateField(id) { it.copy(grade = "") }
            recalculate()
            return
        }

        val parsed = normalized.toDoubleOrNull()
        if (parsed == null || parsed < 0.0 || parsed > 5.0) {
            updateField(id) { it.copy(grade = "") }
            setMessage(CustomFlashMessage.INVALID_GRADE)
            recalculate()
            return
        }

        updateField(id) { it.copy(grade = normalized) }
        recalculate()
    }

    fun onMinPassingGradeChanged(value: String) {
        _uiState.update { it.copy(minPassingGrade = value.replace(',', '.')) }
    }

    fun resetAll() {
        _uiState.value = CustomCalculatorUiState()
        nextFieldId = 1
        viewModelScope.launch {
            userPreferencesRepository.clearCustomCalculatorData()
        }
    }

    fun dismissFlashMessage() {
        _uiState.update { it.copy(flashMessage = null) }
    }

    fun saveCalculator() {
        viewModelScope.launch {
            val state = _uiState.value
            val payload = CustomCalculatorData(
                minPassingGrade = state.minPassingGrade,
                fields = state.fields.map {
                    CustomFieldData(
                        name = it.name,
                        percentage = it.percentage,
                        grade = it.grade
                    )
                }
            )

            userPreferencesRepository.setCustomCalculatorData(
                gson.toJson(payload)
            )
            setMessage(CustomFlashMessage.CALCULATOR_SAVED)
        }
    }

    private suspend fun loadSavedData() {
        val raw = userPreferencesRepository.customCalculatorData.first() ?: return
        restore(raw)?.let { restored -> _uiState.value = restored }
    }

    private fun restore(raw: String): CustomCalculatorUiState? {
        if (raw.isBlank()) return null
        val data = runCatching { gson.fromJson(raw, CustomCalculatorData::class.java) }.getOrNull()
            ?: return null

        val restoredFields = data.fields.map {
            CustomFieldUi(
                id = nextFieldId++,
                name = it.name,
                percentage = it.percentage,
                grade = it.grade
            )
        }
        return CustomCalculatorUiState(
            fields = restoredFields,
            minPassingGrade = data.minPassingGrade.ifBlank { "3.0" },
            hasSavedData = true
        ).withTotals()
    }

    private fun updateField(id: Int, transform: (CustomFieldUi) -> CustomFieldUi) {
        _uiState.update { state ->
            state.copy(fields = state.fields.map { if (it.id == id) transform(it) else it })
        }
    }

    private fun recalculate() {
        _uiState.update { it.withTotals() }
    }

    private fun CustomCalculatorUiState.withTotals(): CustomCalculatorUiState {
        var total = 0.0
        var totalPercentage = 0.0

        fields.forEach { field ->
            val percentage = field.percentage.toDoubleOrNull() ?: 0.0
            val grade = field.grade.toDoubleOrNull() ?: 0.0

            if (field.percentage.isNotBlank() && field.grade.isNotBlank()) {
                totalPercentage += percentage
                total += (percentage / 100.0) * grade
            }
        }

        val percentageValidation: PercentageValidation
        val finalTotal: Double

        when {
            totalPercentage > 100.0 -> {
                percentageValidation = PercentageValidation.EXCEEDED
                finalTotal = 0.0
            }
            totalPercentage in 0.0001..99.9999 -> {
                percentageValidation = PercentageValidation.INSUFFICIENT
                // Con el esquema a medias la nota sigue siendo útil: es lo que se lleva hasta ahora.
                finalTotal = total
            }
            else -> {
                percentageValidation = PercentageValidation.NONE
                finalTotal = total
            }
        }

        return copy(
            totalFinalGrade = finalTotal,
            totalPercentage = totalPercentage,
            percentageValidation = percentageValidation
        )
    }

    private fun setMessage(message: CustomFlashMessage) {
        _uiState.update { it.copy(flashMessage = message) }
    }
}
