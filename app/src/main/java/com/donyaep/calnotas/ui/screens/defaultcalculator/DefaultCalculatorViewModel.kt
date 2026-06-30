package com.donyaep.calnotas.ui.screens.defaultcalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.RoundingMode

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
        val grades = listOf(state.grade1, state.grade2, state.grade3, state.grade4, state.grade5, state.grade6)
        val values = grades.map { it.toDoubleOrNull() }
        val filled = values.map { it ?: 0.0 }.toDoubleArray()

        val final1 = roundTo2(filled[0] * weights[0] + filled[1] * weights[1])
        val final2 = roundTo2(filled[2] * weights[2] + filled[3] * weights[3])
        val final3 = roundTo2(filled[4] * weights[4] + filled[5] * weights[5])
        val total = final1 + final2 + final3

        val prediction = calculatePrediction(values)

        return state.copy(
            final1 = final1,
            final2 = final2,
            final3 = final3,
            total = total,
            hasAnyInput = grades.any { it.isNotBlank() },
            predictionState = prediction.state,
            requiredMinGrade = prediction.requiredMinGrade,
            emptyFieldsCount = prediction.emptyFieldsCount,
            safetyGrade = prediction.safetyGrade,
            safetyGradeFieldIndex = prediction.safetyGradeFieldIndex
        )
    }

    // Pesos en orden [formativa1, cognitiva1, formativa2, cognitiva2, formativa3, cognitiva3].
    private val weights = doubleArrayOf(0.15, 0.15, 0.15, 0.15, 0.20, 0.20)

    // Matches JS's toFixed(2) (round-half-up) used by the web app; kotlin.math.round ties to even,
    // which would give 1.12 instead of 1.13 for the documented 4.5/3.0 example.
    private fun roundTo2(value: Double): Double =
        BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toDouble()

    // Replica el redondeo por corte (toFixed(2)) usado en recalculate() para mantener
    // la predicción 100% consistente con la nota final mostrada.
    private fun totalFromValues(values: DoubleArray): Double {
        val d1 = roundTo2(values[0] * weights[0] + values[1] * weights[1])
        val d2 = roundTo2(values[2] * weights[2] + values[3] * weights[3])
        val d3 = roundTo2(values[4] * weights[4] + values[5] * weights[5])
        return d1 + d2 + d3
    }

    private fun passes(total: Double): Boolean = total >= 3.0

    private data class Prediction(
        val state: PredictionState,
        val requiredMinGrade: Double = 0.0,
        val emptyFieldsCount: Int = 0,
        val safetyGrade: Double? = null,
        val safetyGradeFieldIndex: Int = 0
    )

    private fun calculatePrediction(values: List<Double?>): Prediction {
        val emptyCount = values.count { it == null }
        if (values.all { it == null }) return Prediction(PredictionState.NONE)
        if (emptyCount == 0) return Prediction(PredictionState.COMPLETE)

        fun totalWithFill(fill: Double) = totalFromValues(values.map { it ?: fill }.toDoubleArray())

        if (passes(totalWithFill(0.0))) {
            return Prediction(PredictionState.GUARANTEED, emptyFieldsCount = emptyCount)
        }
        if (!passes(totalWithFill(5.0))) {
            return Prediction(PredictionState.IMPOSSIBLE, emptyFieldsCount = emptyCount)
        }

        for (step in 1..50) {
            val candidate = step / 10.0
            if (passes(totalWithFill(candidate))) {
                val safety = calculateSafetyGrade(values, emptyCount)
                return Prediction(
                    state = PredictionState.POSSIBLE,
                    requiredMinGrade = candidate,
                    emptyFieldsCount = emptyCount,
                    safetyGrade = safety?.first,
                    safetyGradeFieldIndex = safety?.second ?: 0
                )
            }
        }
        // Inalcanzable: los guardas de arriba (relleno 0.0 y 5.0) ya cubren los extremos.
        return Prediction(PredictionState.IMPOSSIBLE, emptyFieldsCount = emptyCount)
    }

    // Nota mínima en el SIGUIENTE campo vacío que asegura aprobar por sí sola,
    // asumiendo 0.0 en los demás campos vacíos. Solo aporta valor con 2+ vacíos.
    private fun calculateSafetyGrade(values: List<Double?>, emptyCount: Int): Pair<Double, Int>? {
        if (emptyCount < 2) return null

        val nextEmptyIndex = values.indexOfFirst { it == null }
        val base = values.map { it ?: 0.0 }.toDoubleArray()

        for (step in 1..50) {
            val candidate = step / 10.0
            val attempt = base.copyOf()
            attempt[nextEmptyIndex] = candidate
            if (passes(totalFromValues(attempt))) {
                return candidate to (nextEmptyIndex + 1)
            }
        }
        return null
    }
}
