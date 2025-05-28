package com.example.pr1

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ItemSearchResultCustomBinding

class ExerciseAdapter(
    private var exercises: List<ExerciseResponse>,
    private val onExerciseClick: (ExerciseResponse) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemSearchResultCustomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int = exercises.size

    fun updateExercises(newExercises: List<ExerciseResponse>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(private val binding: ItemSearchResultCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val exercise = exercises[position]

                    // Создаем Intent для перехода к деталям упражнения
                    val intent = Intent(binding.root.context, ExerciseDetailActivity::class.java).apply {
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_ID, exercise.id)
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_NAME, exercise.name ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_BODY_PART, exercise.bodyPart ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_TARGET, exercise.target ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_EQUIPMENT, exercise.equipment ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DESCRIPTION, exercise.description ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DIFFICULTY, exercise.difficulty ?: "")
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DURATION, exercise.duration)
                        putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_INSTRUCTIONS, exercise.instructions ?: "")
                    }

                    binding.root.context.startActivity(intent)
                    onExerciseClick(exercise)
                }
            }

            // Улучшаем визуальное представление
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
            binding.meditationTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_primary))
            binding.meditationInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_secondary))
        }

        fun bind(exercise: ExerciseResponse) {
            // Безопасное отображение названия с проверкой на null
            val safeName = exercise.name?.takeIf { it.isNotBlank() } ?: "Без названия"
            binding.meditationTitle.text = safeName

            // Более подробная информация об упражнении с проверками на null
            val info = buildString {
                val safeBodyPart = exercise.bodyPart?.takeIf { it.isNotBlank() }
                val safeTarget = exercise.target?.takeIf { it.isNotBlank() }
                val safeDifficulty = exercise.difficulty?.takeIf { it.isNotBlank() }

                if (safeBodyPart != null) {
                    append("Область: $safeBodyPart")
                }
                if (safeTarget != null) {
                    if (isNotEmpty()) append(" • ")
                    append("Цель: $safeTarget")
                }
                if (exercise.duration > 0) {
                    if (isNotEmpty()) append(" • ")
                    append("${exercise.duration} мин")
                }
                if (safeDifficulty != null) {
                    if (isNotEmpty()) append(" • ")
                    append(safeDifficulty)
                }
            }
            binding.meditationInfo.text = if (info.isNotEmpty()) info else "Информация недоступна"
        }

        // Функция для преобразования текста в формат "Каждое Слово С Большой Буквы"
        private fun String.capitalizeWords(): String {
            return this.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
        }
    }
}