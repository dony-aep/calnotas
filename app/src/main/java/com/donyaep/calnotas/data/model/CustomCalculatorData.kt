package com.donyaep.calnotas.data.model

data class CustomFieldData(
    val name: String = "",
    val percentage: String = "",
    val grade: String = ""
)

data class CustomCalculatorData(
    val minPassingGrade: String = "3.0",
    val fields: List<CustomFieldData> = emptyList()
)
