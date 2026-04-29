package com.donyaep.calnotas.ui.screens.defaultcalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DefaultCalculatorUiState())
    val uiState: StateFlow<DefaultCalculatorUiState> = _uiState.asStateFlow()

    fun onGradeChanged(index: Int, value: String) {
        val normalized = value.replace(',', '.')

        if (normalized.isBlank()) {
            updateGrade(index, "")
            return
        }

        val parsed = normalized.toDoubleOrNull()
        if (parsed == null) {
            updateGrade(index, "")
            showError(DefaultCalculatorError.INVALID_NUMBER)
            return
        }

        if (parsed < 0.0 || parsed > 5.0) {
            updateGrade(index, "")
            showError(DefaultCalculatorError.INVALID_RANGE)
            return
        }

        updateGrade(index, normalized)
    }

    fun clearAll() {
        _uiState.value = DefaultCalculatorUiState()
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun showError(error: DefaultCalculatorError) {
        _uiState.update { it.copy(error = error) }
    }

    private fun updateGrade(index: Int, value: String) {
        _uiState.update { state ->
            val newState = when (index) {
                1 -> state.copy(grade1 = value)
                2 -> state.copy(grade2 = value)
                3 -> state.copy(grade3 = value)
                4 -> state.copy(grade4 = value)
                5 -> state.copy(grade5 = value)
                6 -> state.copy(grade6 = value)
                else -> state
            }
            recalculate(newState)
        }
    }

    private fun recalculate(state: DefaultCalculatorUiState): DefaultCalculatorUiState {
        val f1 = state.grade1.toDoubleOrNull() ?: 0.0
        val c1 = state.grade2.toDoubleOrNull() ?: 0.0
        val f2 = state.grade3.toDoubleOrNull() ?: 0.0
        val c2 = state.grade4.toDoubleOrNull() ?: 0.0
        val f3 = state.grade5.toDoubleOrNull() ?: 0.0
        val c3 = state.grade6.toDoubleOrNull() ?: 0.0

        val final1 = (f1 * 0.15) + (c1 * 0.15)
        val final2 = (f2 * 0.15) + (c2 * 0.15)
        val final3 = (f3 * 0.20) + (c3 * 0.20)
        val total = final1 + final2 + final3

        return state.copy(
            final1 = final1,
            final2 = final2,
            final3 = final3,
            total = total,
            hasAnyInput = listOf(
                state.grade1,
                state.grade2,
                state.grade3,
                state.grade4,
                state.grade5,
                state.grade6
            ).any { it.isNotBlank() }
        )
    }
}
