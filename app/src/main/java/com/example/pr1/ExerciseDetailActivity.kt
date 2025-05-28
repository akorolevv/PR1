package com.example.pr1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ActivityExerciseDetailBinding

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseDetailBinding

    companion object {
        const val EXTRA_EXERCISE_ID = "exercise_id"
        const val EXTRA_EXERCISE_NAME = "exercise_name"
        const val EXTRA_EXERCISE_BODY_PART = "exercise_body_part"
        const val EXTRA_EXERCISE_TARGET = "exercise_target"
        const val EXTRA_EXERCISE_EQUIPMENT = "exercise_equipment"
        const val EXTRA_EXERCISE_DESCRIPTION = "exercise_description"
        const val EXTRA_EXERCISE_DIFFICULTY = "exercise_difficulty"
        const val EXTRA_EXERCISE_DURATION = "exercise_duration"
        const val EXTRA_EXERCISE_INSTRUCTIONS = "exercise_instructions"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadExerciseData()
    }

    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadExerciseData() {
        val exercise = ExerciseResponse(
            id = intent.getIntExtra(EXTRA_EXERCISE_ID, 0),
            name = intent.getStringExtra(EXTRA_EXERCISE_NAME) ?: "",
            bodyPart = intent.getStringExtra(EXTRA_EXERCISE_BODY_PART) ?: "",
            target = intent.getStringExtra(EXTRA_EXERCISE_TARGET) ?: "",
            equipment = intent.getStringExtra(EXTRA_EXERCISE_EQUIPMENT) ?: "",
            description = intent.getStringExtra(EXTRA_EXERCISE_DESCRIPTION) ?: "",
            difficulty = intent.getStringExtra(EXTRA_EXERCISE_DIFFICULTY) ?: "",
            duration = intent.getIntExtra(EXTRA_EXERCISE_DURATION, 0),
            instructions = intent.getStringExtra(EXTRA_EXERCISE_INSTRUCTIONS) ?: ""
        )

        displayExerciseInfo(exercise)
    }

    private fun displayExerciseInfo(exercise: ExerciseResponse) {
        binding.exerciseName.text = exercise.name
        binding.exerciseDescription.text = exercise.description.ifEmpty { "Описание недоступно" }

        // Информация об упражнении
        val infoText = buildString {
            if (exercise.bodyPart.isNotEmpty()) {
                append("Область: ${exercise.bodyPart}\n")
            }
            if (exercise.target.isNotEmpty()) {
                append("Цель: ${exercise.target}\n")
            }
            if (exercise.equipment.isNotEmpty()) {
                append("Оборудование: ${exercise.equipment}\n")
            }
            if (exercise.difficulty.isNotEmpty()) {
                append("Сложность: ${exercise.difficulty}\n")
            }
            if (exercise.duration > 0) {
                append("Длительность: ${exercise.duration} мин")
            }
        }
        binding.exerciseInfo.text = infoText

        // Инструкции
        binding.exerciseInstructions.text = exercise.instructions.ifEmpty {
            "Подробные инструкции скоро будут добавлены"
        }
    }
}