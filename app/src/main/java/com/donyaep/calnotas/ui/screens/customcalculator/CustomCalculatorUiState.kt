package com.donyaep.calnotas.ui.screens.customcalculator

enum class CustomFlashMessage {
    CALCULATOR_SAVED,
    INVALID_GRADE
}

enum class PercentageValidation {
    NONE,
    INSUFFICIENT,
    EXCEEDED
}

data class CustomFieldUi(
    val id: Int,
    val name: String = "",
    val percentage: String = "",
    val grade: String = ""
)

data class CustomCalculatorUiState(
    val fields: List<CustomFieldUi> = emptyList(),
    val minPassingGrade: String = "3.0",
    val totalFinalGrade: Double = 0.0,
    val totalPercentage: Double = 0.0,
    val percentageValidation: PercentageValidation = PercentageValidation.NONE,
    val flashMessage: CustomFlashMessage? = null
)
