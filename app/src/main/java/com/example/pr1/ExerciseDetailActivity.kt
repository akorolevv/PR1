package com.example.pr1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pr1.data.UserManager
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ActivityExerciseDetailBinding

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseDetailBinding
    private lateinit var userManager: UserManager
    private var exercise: ExerciseResponse? = null
    private var isFavorite = false

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

        userManager = UserManager(this)

        setupToolbar()
        loadExerciseData()

        // Проверяем, находится ли упражнение в избранном
        exercise?.let { checkIfFavorite(it.id) }
    }

    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.favoriteButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun loadExerciseData() {
        exercise = ExerciseResponse(
            id = intent.getIntExtra(EXTRA_EXERCISE_ID, 0),
            name = intent.getStringExtra(EXTRA_EXERCISE_NAME),
            bodyPart = intent.getStringExtra(EXTRA_EXERCISE_BODY_PART),
            target = intent.getStringExtra(EXTRA_EXERCISE_TARGET),
            equipment = intent.getStringExtra(EXTRA_EXERCISE_EQUIPMENT),
            description = intent.getStringExtra(EXTRA_EXERCISE_DESCRIPTION),
            difficulty = intent.getStringExtra(EXTRA_EXERCISE_DIFFICULTY),
            duration = intent.getIntExtra(EXTRA_EXERCISE_DURATION, 0),
            instructions = intent.getStringExtra(EXTRA_EXERCISE_INSTRUCTIONS)
        )

        exercise?.let { displayExerciseInfo(it) }
    }

    private fun displayExerciseInfo(exercise: ExerciseResponse) {
        // Безопасная работа с nullable полями
        binding.exerciseName.text = exercise.name?.takeIf { it.isNotBlank() } ?: "Название упражнения"
        binding.exerciseDescription.text = exercise.description?.takeIf { it.isNotBlank() } ?: "Описание недоступно"

        // Информация об упражнении с безопасной обработкой nullable полей
        val infoText = buildString {
            exercise.bodyPart?.takeIf { it.isNotBlank() }?.let { bodyPart ->
                append("Область: $bodyPart\n")
            }
            exercise.target?.takeIf { it.isNotBlank() }?.let { target ->
                append("Цель: $target\n")
            }
            exercise.equipment?.takeIf { it.isNotBlank() }?.let { equipment ->
                append("Оборудование: $equipment\n")
            }
            exercise.difficulty?.takeIf { it.isNotBlank() }?.let { difficulty ->
                append("Сложность: $difficulty\n")
            }
            if (exercise.duration > 0) {
                append("Длительность: ${exercise.duration} мин")
            }
        }
        binding.exerciseInfo.text = if (infoText.isNotEmpty()) infoText else "Информация недоступна"

        // Инструкции с безопасной обработкой
        binding.exerciseInstructions.text = exercise.instructions?.takeIf { it.isNotBlank() }
            ?: "Подробные инструкции скоро будут добавлены"
    }

    private fun checkIfFavorite(exerciseId: Int) {
        if (!userManager.isLoggedIn()) {
            // Если пользователь не авторизован, скрываем кнопку избранного
            binding.favoriteButton.visibility = android.view.View.GONE
            return
        }

        // Проверяем статус избранного через API
        userManager.checkIfFavorite(
            exerciseId = exerciseId,
            onSuccess = { isInFavorites ->
                runOnUiThread {
                    isFavorite = isInFavorites
                    updateFavoriteUI()
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    // В случае ошибки показываем кнопку как не избранное
                    isFavorite = false
                    updateFavoriteUI()
                }
            }
        )
    }

    private fun toggleFavorite() {
        val currentExercise = exercise ?: return

        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "Необходимо войти в аккаунт", Toast.LENGTH_SHORT).show()
            return
        }

        // Отключаем кнопку во время запроса
        binding.favoriteButton.isEnabled = false

        if (isFavorite) {
            // Удаляем из избранного
            userManager.removeFromFavorites(
                exerciseId = currentExercise.id,
                onSuccess = {
                    runOnUiThread {
                        isFavorite = false
                        updateFavoriteUI()
                        binding.favoriteButton.isEnabled = true
                        Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { errorMessage ->
                    runOnUiThread {
                        binding.favoriteButton.isEnabled = true
                        Toast.makeText(this, "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            )
        } else {
            // Добавляем в избранное
            userManager.addToFavorites(
                exerciseId = currentExercise.id,
                onSuccess = {
                    runOnUiThread {
                        isFavorite = true
                        updateFavoriteUI()
                        binding.favoriteButton.isEnabled = true
                        Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { errorMessage ->
                    runOnUiThread {
                        binding.favoriteButton.isEnabled = true
                        Toast.makeText(this, "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    private fun updateFavoriteUI() {
        if (isFavorite) {
            // Заполненная звезда для избранного
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            // Пустая звезда для не избранного
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }
}