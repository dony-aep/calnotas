package com.donyaep.calnotas.ui.screens.defaultcalculator

enum class DefaultCalculatorError {
    INVALID_NUMBER,
    INVALID_RANGE
}

data class DefaultCalculatorUiState(
    val grade1: String = "",
    val grade2: String = "",
    val grade3: String = "",
    val grade4: String = "",
    val grade5: String = "",
    val grade6: String = "",
    val final1: Double = 0.0,
    val final2: Double = 0.0,
    val final3: Double = 0.0,
    val total: Double = 0.0,
    val hasAnyInput: Boolean = false,
    val error: DefaultCalculatorError? = null
)
