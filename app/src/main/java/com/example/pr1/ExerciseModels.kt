// ExerciseModels.kt
package com.example.pr1.data.models

data class ExerciseResponse(
    val id: Int = 0,
    val name: String? = "",
    val bodyPart: String? = "",
    val target: String? = "",
    val equipment: String? = "",
    val description: String? = "",
    val difficulty: String? = "Начинающий",
    val duration: Int = 10,
    val instructions: String? = ""
)